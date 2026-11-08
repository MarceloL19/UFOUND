package pe.edu.ulima.ufound.service;

import pe.edu.ulima.ufound.coincidencias.MotorSimilitud;
import pe.edu.ulima.ufound.coincidencias.MotorSimilitudVisual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.NivelCoincidencia;
import pe.edu.ulima.ufound.model.Notificacion;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.NotificacionRepository;
import pe.edu.ulima.ufound.repository.ObjetoEncontradoRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.util.List;

@Service
public class CoincidenciaService {

    private static final int UMBRAL_MINIMO = 40;
    private static final int UMBRAL_NOTIFICACION = 60;

    private final CoincidenciaRepository coincidenciaRepository;
    private final NotificacionRepository notificacionRepository;
    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final ObjetoEncontradoRepository objetoEncontradoRepository;
    private final EstadoObjetoService estadoObjetoService;
    private final MotorSimilitud motorSimilitud;

    public CoincidenciaService(CoincidenciaRepository coincidenciaRepository,
            NotificacionRepository notificacionRepository,
            ObjetoPerdidoRepository objetoPerdidoRepository,
            ObjetoEncontradoRepository objetoEncontradoRepository,
            EstadoObjetoService estadoObjetoService, MotorSimilitudVisual motorSimilitud) {
        this.coincidenciaRepository = coincidenciaRepository;
        this.notificacionRepository = notificacionRepository;
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.objetoEncontradoRepository = objetoEncontradoRepository;
        this.estadoObjetoService = estadoObjetoService;
        this.motorSimilitud = motorSimilitud;
    }

    @Transactional
    public void detectarParaObjetoPerdido(ObjetoPerdido objetoPerdido) {
        objetoEncontradoRepository.findAll().forEach(encontrado -> evaluarYRegistrar(objetoPerdido, encontrado));
    }

    @Transactional
    public void detectarParaObjetoEncontrado(ObjetoEncontrado objetoEncontrado) {
        objetoPerdidoRepository.findAll().forEach(perdido -> evaluarYRegistrar(perdido, objetoEncontrado));
    }

    @Transactional(readOnly = true)
    public List<Coincidencia> listarPorUsuario(Usuario usuario) {
        return coincidenciaRepository
                .findByObjetoPerdidoUsuarioOrderByPorcentajeSimilitudDescFechaDeteccionDesc(usuario);
    }

    @Transactional(readOnly = true)
    public Coincidencia obtenerParaUsuario(Long idCoincidencia, Usuario usuario) {
        Coincidencia coincidencia = coincidenciaRepository.findById(idCoincidencia)
                .orElseThrow(() -> new RegistroObjetoException("La coincidencia solicitada no existe."));
        if (!coincidencia.getObjetoPerdido().getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RegistroObjetoException("No tienes permisos para visualizar esta coincidencia.");
        }
        return coincidencia;
    }

    @Transactional(readOnly = true)
    public long contarPorUsuario(Usuario usuario) {
        return coincidenciaRepository.countByObjetoPerdidoUsuario(usuario);
    }

    private void evaluarYRegistrar(ObjetoPerdido perdido, ObjetoEncontrado encontrado) {
        int porcentaje = calcularSimilitud(perdido, encontrado);
        if (porcentaje < UMBRAL_MINIMO) {
            return;
        }

        NivelCoincidencia nivel = obtenerNivel(porcentaje);
        Coincidencia coincidencia = coincidenciaRepository.findByObjetoPerdidoAndObjetoEncontrado(perdido, encontrado)
                .orElseGet(Coincidencia::new);
        coincidencia.setObjetoPerdido(perdido);
        coincidencia.setObjetoEncontrado(encontrado);
        coincidencia.setNivelCoincidencia(nivel);
        coincidencia.setPorcentajeSimilitud(porcentaje);
        Coincidencia guardada = coincidenciaRepository.save(coincidencia);

        if (porcentaje >= UMBRAL_NOTIFICACION) {
            crearNotificacionSiNoExiste(guardada);
            if (perdido.getEstado() == EstadoObjeto.REPORTADO) {
                estadoObjetoService.cambiarEstado(TipoObjeto.PERDIDO, perdido.getIdObjeto(),
                        EstadoObjeto.COINCIDENCIA_DETECTADA, perdido.getUsuario());
            }
        }
    }

    private void crearNotificacionSiNoExiste(Coincidencia coincidencia) {
        Usuario usuario = coincidencia.getObjetoPerdido().getUsuario();
        if (notificacionRepository.findByUsuarioAndCoincidencia(usuario, coincidencia).isPresent()) {
            return;
        }
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setCoincidencia(coincidencia);
        notificacion.setMensaje("Tu reporte \"" + coincidencia.getObjetoPerdido().getNombre()
                + "\" tiene una posible coincidencia del " + coincidencia.getPorcentajeSimilitud()
                + "% con un objeto encontrado.");
        notificacionRepository.save(notificacion);
    }

    private int calcularSimilitud(
            ObjetoPerdido perdido,
            ObjetoEncontrado encontrado) {

        return motorSimilitud.calcular(perdido, encontrado);
    }

    private NivelCoincidencia obtenerNivel(int porcentaje) {
        if (porcentaje >= 80) {
            return NivelCoincidencia.ALTO;
        }
        if (porcentaje >= 60) {
            return NivelCoincidencia.MEDIO;
        }
        return NivelCoincidencia.BAJO;
    }
}
