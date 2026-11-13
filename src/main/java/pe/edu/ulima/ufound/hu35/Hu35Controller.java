package pe.edu.ulima.ufound.hu35;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

import java.util.List;
import java.util.Objects;

/**
 * Consulta HU-35: muestra las coincidencias previamente calculadas
 * para un reporte perdido, sin recalcularlas ni alterar estados.
 */
@Service
public class Hu35Service {

    private static final String MENSAJE_SIN_ACCESO =
            "No se encontro el reporte o no tienes permisos para consultarlo.";

    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final CoincidenciaRepository coincidenciaRepository;

    public Hu35Service(
            ObjetoPerdidoRepository objetoPerdidoRepository,
            CoincidenciaRepository coincidenciaRepository) {

        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.coincidenciaRepository = coincidenciaRepository;
    }

    @Transactional(readOnly = true)
    public DetalleObjetoPerdidoHu35 obtenerDetalleConSugerencias(
            Long idObjetoPerdido,
            Usuario usuario) {

        // Validacion de entrada y autorizacion
        if (idObjetoPerdido == null
                || idObjetoPerdido <= 0
                || usuario == null
                || usuario.getIdUsuario() == null
                || usuario.getRol() != Rol.ESTUDIANTE) {

            throw new RegistroObjetoException(MENSAJE_SIN_ACCESO);
        }

        // Obtener el reporte
        ObjetoPerdido objeto = objetoPerdidoRepository
                .findById(idObjetoPerdido)
                .orElseThrow(() ->
                        new RegistroObjetoException(MENSAJE_SIN_ACCESO));

        // Validar que pertenezca al estudiante autenticado
        if (objeto.getUsuario() == null
                || !Objects.equals(
                        objeto.getUsuario().getIdUsuario(),
                        usuario.getIdUsuario())) {

            throw new RegistroObjetoException(MENSAJE_SIN_ACCESO);
        }

        // Consultar coincidencias previamente calculadas
        List<Coincidencia> coincidencias =
                coincidenciaRepository
                        .findByObjetoPerdidoOrderByPorcentajeSimilitudDescFechaDeteccionDesc(
                                objeto
                        );

        return new DetalleObjetoPerdidoHu35(objeto, coincidencias);
    }
}
