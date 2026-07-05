package pe.edu.ulima.ufound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.Notificacion;
import pe.edu.ulima.ufound.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioOrderByEstadoLeidoAscFechaCreacionDesc(Usuario usuario);

    List<Notificacion> findByUsuarioAndEstadoLeidoOrderByFechaCreacionDesc(Usuario usuario, Boolean estadoLeido);

    Optional<Notificacion> findByUsuarioAndCoincidencia(Usuario usuario, Coincidencia coincidencia);

    long countByUsuarioAndEstadoLeido(Usuario usuario, Boolean estadoLeido);
}
