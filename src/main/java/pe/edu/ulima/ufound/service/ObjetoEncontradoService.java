package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.RegistroObjetoEncontradoForm;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.ObjetoEncontradoRepository;

import java.util.List;

@Service
public class ObjetoEncontradoService {

    private final ObjetoEncontradoRepository objetoEncontradoRepository;
    private final UploadService uploadService;
    private final EstadoObjetoService estadoObjetoService;
    private final CoincidenciaService coincidenciaService;

    public ObjetoEncontradoService(ObjetoEncontradoRepository objetoEncontradoRepository, UploadService uploadService,
                                   EstadoObjetoService estadoObjetoService, CoincidenciaService coincidenciaService) {
        this.objetoEncontradoRepository = objetoEncontradoRepository;
        this.uploadService = uploadService;
        this.estadoObjetoService = estadoObjetoService;
        this.coincidenciaService = coincidenciaService;
    }

    @Transactional
    public ObjetoEncontrado registrar(RegistroObjetoEncontradoForm form, MultipartFile imagen, Usuario usuario) {
        validar(form);

        ObjetoEncontrado objeto = new ObjetoEncontrado();
        objeto.setNombre(form.getNombre().trim());
        objeto.setCategoria(form.getCategoria());
        objeto.setUbicacion(form.getUbicacion());
        objeto.setDescripcion(form.getDescripcion().trim());
        objeto.setImagenUrl(uploadService.guardarImagenObjetoEncontrado(imagen));
        objeto.setUsuario(usuario);

        ObjetoEncontrado guardado = objetoEncontradoRepository.save(objeto);
        estadoObjetoService.registrarEstadoInicial(TipoObjeto.ENCONTRADO, guardado.getIdObjeto(), guardado.getEstado(), usuario);
        coincidenciaService.detectarParaObjetoEncontrado(guardado);
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<ObjetoEncontrado> listarTodos() {
        return objetoEncontradoRepository.findAllByOrderByFechaHoraDesc();
    }

    @Transactional(readOnly = true)
    public List<ObjetoEncontrado> listarPorUsuario(Usuario usuario) {
        return objetoEncontradoRepository.findByUsuarioOrderByFechaHoraDesc(usuario);
    }

    @Transactional(readOnly = true)
    public ObjetoEncontrado obtenerPorId(Long idObjeto) {
        return objetoEncontradoRepository.findById(idObjeto)
                .orElseThrow(() -> new RegistroObjetoException("El objeto encontrado solicitado no existe."));
    }

    @Transactional(readOnly = true)
    public long contarPorUsuario(Usuario usuario) {
        return objetoEncontradoRepository.countByUsuario(usuario);
    }

    private void validar(RegistroObjetoEncontradoForm form) {
        if (form.getNombre() == null || form.getNombre().isBlank()) {
            throw new RegistroObjetoException("Ingresa el nombre o descripcion corta del objeto encontrado.");
        }
        if (form.getCategoria() == null) {
            throw new RegistroObjetoException("Selecciona una categoria.");
        }
        if (form.getUbicacion() == null) {
            throw new RegistroObjetoException("Selecciona la ubicacion donde se encontro.");
        }
        if (form.getDescripcion() == null || form.getDescripcion().isBlank()) {
            throw new RegistroObjetoException("Describe las caracteristicas del objeto encontrado.");
        }
        if (form.getDescripcion().trim().length() > 700) {
            throw new RegistroObjetoException("La descripcion no debe superar 700 caracteres.");
        }
    }
}

