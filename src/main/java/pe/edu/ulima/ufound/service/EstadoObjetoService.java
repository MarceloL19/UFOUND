package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.HistorialEstado;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.HistorialEstadoRepository;
import pe.edu.ulima.ufound.repository.ObjetoEncontradoRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EstadoObjetoService {

    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final ObjetoEncontradoRepository objetoEncontradoRepository;
    private final HistorialEstadoRepository historialEstadoRepository;

    public EstadoObjetoService(ObjetoPerdidoRepository objetoPerdidoRepository,
                               ObjetoEncontradoRepository objetoEncontradoRepository,
                               HistorialEstadoRepository historialEstadoRepository) {
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.objetoEncontradoRepository = objetoEncontradoRepository;
        this.historialEstadoRepository = historialEstadoRepository;
    }

    @Transactional
    public void registrarEstadoInicial(TipoObjeto tipoObjeto, Long idObjeto, EstadoObjeto estado, Usuario usuario) {
        List<HistorialEstado> historial = historialEstadoRepository
                .findByTipoObjetoAndIdObjetoOrderByFechaCambioAsc(tipoObjeto, idObjeto);
        if (!historial.isEmpty()) {
            return;
        }
        HistorialEstado inicial = new HistorialEstado();
        inicial.setTipoObjeto(tipoObjeto);
        inicial.setIdObjeto(idObjeto);
        inicial.setEstadoAnterior(null);
        inicial.setEstadoNuevo(estado);
        inicial.setUsuarioResponsable(usuario);
        historialEstadoRepository.save(inicial);
    }

    @Transactional
    public void asegurarHistorialInicial(TipoObjeto tipoObjeto, Long idObjeto) {
        List<HistorialEstado> historial = historialEstadoRepository
                .findByTipoObjetoAndIdObjetoOrderByFechaCambioAsc(tipoObjeto, idObjeto);
        if (!historial.isEmpty()) {
            return;
        }
        if (tipoObjeto == TipoObjeto.PERDIDO) {
            ObjetoPerdido objeto = obtenerObjetoPerdido(idObjeto);
            registrarEstadoInicial(tipoObjeto, idObjeto, normalizarEstado(objeto.getEstado()), objeto.getUsuario());
        } else {
            ObjetoEncontrado objeto = obtenerObjetoEncontrado(idObjeto);
            registrarEstadoInicial(tipoObjeto, idObjeto, normalizarEstado(objeto.getEstado()), objeto.getUsuario());
        }
    }

    @Transactional(readOnly = true)
    public List<HistorialEstado> obtenerHistorial(TipoObjeto tipoObjeto, Long idObjeto) {
        return historialEstadoRepository.findByTipoObjetoAndIdObjetoOrderByFechaCambioAsc(tipoObjeto, idObjeto);
    }

    @Transactional
    public void cambiarEstado(TipoObjeto tipoObjeto, Long idObjeto, EstadoObjeto nuevoEstado, Usuario responsable) {
        if (nuevoEstado == null || !nuevoEstado.esGestionable()) {
            throw new RegistroObjetoException("Selecciona un estado valido.");
        }

        EstadoObjeto estadoAnterior;
        if (tipoObjeto == TipoObjeto.PERDIDO) {
            ObjetoPerdido objeto = obtenerObjetoPerdido(idObjeto);
            estadoAnterior = objeto.getEstado();
            objeto.setEstado(nuevoEstado);
            objetoPerdidoRepository.save(objeto);
        } else {
            ObjetoEncontrado objeto = obtenerObjetoEncontrado(idObjeto);
            estadoAnterior = objeto.getEstado();
            objeto.setEstado(nuevoEstado);
            objetoEncontradoRepository.save(objeto);
        }

        HistorialEstado historial = new HistorialEstado();
        historial.setTipoObjeto(tipoObjeto);
        historial.setIdObjeto(idObjeto);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(nuevoEstado);
        historial.setUsuarioResponsable(responsable);
        historialEstadoRepository.save(historial);
    }

    @Transactional(readOnly = true)
    public ObjetoPerdido obtenerObjetoPerdido(Long idObjeto) {
        return objetoPerdidoRepository.findById(idObjeto)
                .orElseThrow(() -> new RegistroObjetoException("El objeto perdido solicitado no existe."));
    }

    @Transactional(readOnly = true)
    public ObjetoEncontrado obtenerObjetoEncontrado(Long idObjeto) {
        return objetoEncontradoRepository.findById(idObjeto)
                .orElseThrow(() -> new RegistroObjetoException("El objeto encontrado solicitado no existe."));
    }

    @Transactional(readOnly = true)
    public List<EstadoObjetoResumen> listarActividadReciente() {
        List<EstadoObjetoResumen> objetos = new ArrayList<>();
        objetoPerdidoRepository.findAll().forEach(objeto -> objetos.add(new EstadoObjetoResumen(
                TipoObjeto.PERDIDO,
                objeto.getIdObjeto(),
                objeto.getNombre(),
                objeto.getCategoria(),
                objeto.getUbicacion(),
                objeto.getFechaHora(),
                objeto.getImagenUrl(),
                objeto.getEstado()
        )));
        objetoEncontradoRepository.findAll().forEach(objeto -> objetos.add(new EstadoObjetoResumen(
                TipoObjeto.ENCONTRADO,
                objeto.getIdObjeto(),
                objeto.getNombre(),
                objeto.getCategoria(),
                objeto.getUbicacion(),
                objeto.getFechaHora(),
                objeto.getImagenUrl(),
                objeto.getEstado()
        )));
        objetos.sort(Comparator.comparing(EstadoObjetoResumen::getFechaHora).reversed());
        return objetos;
    }

    @Transactional(readOnly = true)
    public boolean objetoPerdidoPerteneceA(Long idObjeto, Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        return obtenerObjetoPerdido(idObjeto).getUsuario().getIdUsuario().equals(usuario.getIdUsuario());
    }

    public List<EstadoObjeto> estadosGestionables() {
        return List.of(
                EstadoObjeto.REPORTADO,
                EstadoObjeto.EN_REVISION,
                EstadoObjeto.COINCIDENCIA_DETECTADA,
                EstadoObjeto.DISPONIBLE_OFICINA,
                EstadoObjeto.ENTREGADO,
                EstadoObjeto.ARCHIVADO
        );
    }

    @Transactional(readOnly = true)
    public List<EstadoTimelineItem> construirLineaTiempo(TipoObjeto tipoObjeto, Long idObjeto, EstadoObjeto estadoActual) {
        List<HistorialEstado> historial = obtenerHistorial(tipoObjeto, idObjeto);
        List<EstadoObjeto> estados = estadosGestionables();
        int indiceActual = Math.max(0, estados.indexOf(normalizarEstado(estadoActual)));

        return estados.stream()
                .map(estado -> {
                    HistorialEstado cambio = historial.stream()
                            .filter(item -> item.getEstadoNuevo() == estado)
                            .reduce((primero, ultimo) -> ultimo)
                            .orElse(null);
                    int indice = estados.indexOf(estado);
                    return new EstadoTimelineItem(estado, cambio, indice <= indiceActual, indice == indiceActual);
                })
                .toList();
    }

    private EstadoObjeto normalizarEstado(EstadoObjeto estado) {
        if (estado == EstadoObjeto.REGISTRADO) {
            return EstadoObjeto.REPORTADO;
        }
        if (estado == EstadoObjeto.EN_CUSTODIA) {
            return EstadoObjeto.EN_REVISION;
        }
        if (estado == EstadoObjeto.RECUPERADO) {
            return EstadoObjeto.ENTREGADO;
        }
        return estado;
    }
}
