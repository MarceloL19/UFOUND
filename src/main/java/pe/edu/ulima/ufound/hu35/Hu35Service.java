package pe.edu.ulima.ufound.hu35;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Consulta HU-35: muestra las coincidencias que el motor existente ya calculó
 * para un reporte perdido, sin volver a calcularlas ni alterar estados.
 */
@Service
public class Hu35Service {

    static final String MENSAJE_SIN_ACCESO = "No se pudo acceder al reporte solicitado.";

    // Mismo criterio de HU-34 mientras su comparador no este integrado en main.
    private static final Comparator<Coincidencia> POR_RELEVANCIA = Comparator
            .comparing(Coincidencia::getPorcentajeSimilitud, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Coincidencia::getFechaDeteccion, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Coincidencia::getIdCoincidencia, Comparator.nullsLast(Comparator.reverseOrder()));

    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final CoincidenciaRepository coincidenciaRepository;

    public Hu35Service(ObjetoPerdidoRepository objetoPerdidoRepository,
                       CoincidenciaRepository coincidenciaRepository) {
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.coincidenciaRepository = coincidenciaRepository;
    }

    @Transactional(readOnly = true)
    public DetalleObjetoPerdidoHu35 obtenerDetalleConSugerencias(Long idObjetoPerdido, Usuario usuario) {
        if (idObjetoPerdido == null || usuario == null || usuario.getIdUsuario() == null) {
            throw new RegistroObjetoException(MENSAJE_SIN_ACCESO);
        }

        ObjetoPerdido objeto = objetoPerdidoRepository.findById(idObjetoPerdido)
                .orElseThrow(() -> new RegistroObjetoException(MENSAJE_SIN_ACCESO));

        if (objeto.getUsuario() == null
                || !Objects.equals(objeto.getUsuario().getIdUsuario(), usuario.getIdUsuario())) {
            throw new RegistroObjetoException(MENSAJE_SIN_ACCESO);
        }

        List<Coincidencia> coincidencias = coincidenciaRepository.findByObjetoPerdido(objeto).stream()
                .sorted(POR_RELEVANCIA)
                .toList();

        return new DetalleObjetoPerdidoHu35(objeto, coincidencias);
    }
}
