package pe.edu.ulima.ufound.service;

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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoincidenciaService {

    private static final int UMBRAL_MINIMO = 40;
    private static final int UMBRAL_NOTIFICACION = 60;

    private final CoincidenciaRepository coincidenciaRepository;
    private final NotificacionRepository notificacionRepository;
    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final ObjetoEncontradoRepository objetoEncontradoRepository;
    private final EstadoObjetoService estadoObjetoService;

    public CoincidenciaService(CoincidenciaRepository coincidenciaRepository,
                               NotificacionRepository notificacionRepository,
                               ObjetoPerdidoRepository objetoPerdidoRepository,
                               ObjetoEncontradoRepository objetoEncontradoRepository,
                               EstadoObjetoService estadoObjetoService) {
        this.coincidenciaRepository = coincidenciaRepository;
        this.notificacionRepository = notificacionRepository;
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.objetoEncontradoRepository = objetoEncontradoRepository;
        this.estadoObjetoService = estadoObjetoService;
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
        return coincidenciaRepository.findByObjetoPerdidoUsuarioOrderByPorcentajeSimilitudDescFechaDeteccionDesc(usuario);
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

    private int calcularSimilitud(ObjetoPerdido perdido, ObjetoEncontrado encontrado) {
        int puntaje = 0;
        if (perdido.getCategoria() == encontrado.getCategoria()) {
            puntaje += 30;
        }
        if (perdido.getUbicacion() == encontrado.getUbicacion()) {
            puntaje += 20;
        }
        puntaje += similitudTexto(perdido.getNombre(), encontrado.getNombre(), 25);
        puntaje += similitudTexto(perdido.getDescripcion(), encontrado.getDescripcion(), 25);
        return Math.min(100, puntaje);
    }

    private int similitudTexto(String origen, String destino, int maximo) {
        Set<String> palabrasOrigen = extraerPalabras(origen);
        Set<String> palabrasDestino = extraerPalabras(destino);
        if (palabrasOrigen.isEmpty() || palabrasDestino.isEmpty()) {
            return 0;
        }
        Set<String> interseccion = new HashSet<>(palabrasOrigen);
        interseccion.retainAll(palabrasDestino);
        double proporcion = (double) interseccion.size() / Math.max(palabrasOrigen.size(), palabrasDestino.size());
        return (int) Math.round(proporcion * maximo);
    }

    private Set<String> extraerPalabras(String texto) {
        if (texto == null || texto.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(texto.toLowerCase(Locale.ROOT).split("[^a-z0-9áéíóúñ]+"))
                .filter(palabra -> palabra.length() > 2)
                .collect(Collectors.toSet());
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
