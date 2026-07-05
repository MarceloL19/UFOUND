package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.RegistroObjetoPerdidoForm;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.util.List;

@Service
public class ObjetoPerdidoService {

    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final UploadService uploadService;
    private final EstadoObjetoService estadoObjetoService;
    private final CoincidenciaService coincidenciaService;

    public ObjetoPerdidoService(ObjetoPerdidoRepository objetoPerdidoRepository, UploadService uploadService,
                                EstadoObjetoService estadoObjetoService, CoincidenciaService coincidenciaService) {
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.uploadService = uploadService;
        this.estadoObjetoService = estadoObjetoService;
        this.coincidenciaService = coincidenciaService;
    }

    @Transactional
    public ObjetoPerdido registrar(RegistroObjetoPerdidoForm form, MultipartFile imagen, Usuario usuario) {
        validar(form);

        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setNombre(form.getNombre().trim());
        objeto.setCategoria(form.getCategoria());
        objeto.setUbicacion(form.getUbicacion());
        objeto.setDescripcion(form.getDescripcion().trim());
        objeto.setImagenUrl(uploadService.guardarImagenObjetoPerdido(imagen));
        objeto.setUsuario(usuario);

        ObjetoPerdido guardado = objetoPerdidoRepository.save(objeto);
        estadoObjetoService.registrarEstadoInicial(TipoObjeto.PERDIDO, guardado.getIdObjeto(), guardado.getEstado(), usuario);
        coincidenciaService.detectarParaObjetoPerdido(guardado);
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<ObjetoPerdido> listarPorUsuario(Usuario usuario) {
        return objetoPerdidoRepository.findByUsuarioOrderByFechaHoraDesc(usuario);
    }

    @Transactional(readOnly = true)
    public long contarPorUsuario(Usuario usuario) {
        return objetoPerdidoRepository.countByUsuario(usuario);
    }

    private void validar(RegistroObjetoPerdidoForm form) {
        if (form.getNombre() == null || form.getNombre().isBlank()) {
            throw new RegistroObjetoException("Ingresa el titulo del objeto perdido.");
        }
        if (form.getCategoria() == null) {
            throw new RegistroObjetoException("Selecciona una categoria.");
        }
        if (form.getUbicacion() == null) {
            throw new RegistroObjetoException("Selecciona la ubicacion donde se perdio.");
        }
        if (form.getDescripcion() == null || form.getDescripcion().isBlank()) {
            throw new RegistroObjetoException("Describe las caracteristicas del objeto.");
        }
        if (form.getDescripcion().trim().length() > 700) {
            throw new RegistroObjetoException("La descripcion no debe superar 700 caracteres.");
        }
    }
}

