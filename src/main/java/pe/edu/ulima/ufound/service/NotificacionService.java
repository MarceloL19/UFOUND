package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.Notificacion;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.NotificacionRepository;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarNoLeidas(Usuario usuario) {
        return notificacionRepository.findByUsuarioAndEstadoLeidoOrderByFechaCreacionDesc(usuario, false);
    }

    @Transactional(readOnly = true)
    public List<Notificacion> listarLeidas(Usuario usuario) {
        return notificacionRepository.findByUsuarioAndEstadoLeidoOrderByFechaCreacionDesc(usuario, true);
    }

    @Transactional(readOnly = true)
    public long contarNoLeidas(Usuario usuario) {
        return notificacionRepository.countByUsuarioAndEstadoLeido(usuario, false);
    }

    @Transactional
    public void marcarComoLeida(Long idNotificacion, Usuario usuario) {
        Notificacion notificacion = obtenerPropia(idNotificacion, usuario);
        notificacion.setEstadoLeido(true);
        notificacionRepository.save(notificacion);
    }

    @Transactional
    public void marcarTodasComoLeidas(Usuario usuario) {
        List<Notificacion> notificaciones = listarNoLeidas(usuario);
        notificaciones.forEach(notificacion -> notificacion.setEstadoLeido(true));
        notificacionRepository.saveAll(notificaciones);
    }

    private Notificacion obtenerPropia(Long idNotificacion, Usuario usuario) {
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new RegistroObjetoException("La notificacion solicitada no existe."));
        if (!notificacion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RegistroObjetoException("No tienes permisos para modificar esta notificacion.");
        }
        return notificacion;
    }
}
