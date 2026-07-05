package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.CoincidenciaService;
import pe.edu.ulima.ufound.service.NotificacionService;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

@Controller
public class CoincidenciaController {

    private final CoincidenciaService coincidenciaService;
    private final NotificacionService notificacionService;

    public CoincidenciaController(CoincidenciaService coincidenciaService, NotificacionService notificacionService) {
        this.coincidenciaService = coincidenciaService;
        this.notificacionService = notificacionService;
    }

    @GetMapping("/coincidencias")
    public String listarCoincidencias(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo estudiantes pueden revisar coincidencias.");
            return "redirect:/home";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("coincidencias", coincidenciaService.listarPorUsuario(usuario));
        return "coincidencias";
    }

    @GetMapping("/coincidencias/{id}")
    public String verDetalle(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo estudiantes pueden revisar coincidencias.");
            return "redirect:/home";
        }

        try {
            Coincidencia coincidencia = coincidenciaService.obtenerParaUsuario(id, usuario);
            model.addAttribute("usuario", usuario);
            model.addAttribute("coincidencia", coincidencia);
            return "detalle-coincidencia";
        } catch (RegistroObjetoException exception) {
            redirectAttributes.addFlashAttribute("accesoDenegado", exception.getMessage());
            return "redirect:/coincidencias";
        }
    }

    @GetMapping("/notificaciones")
    public String listarNotificaciones(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo estudiantes pueden revisar notificaciones.");
            return "redirect:/home";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("noLeidas", notificacionService.listarNoLeidas(usuario));
        model.addAttribute("leidas", notificacionService.listarLeidas(usuario));
        model.addAttribute("totalNoLeidas", notificacionService.contarNoLeidas(usuario));
        return "notificaciones";
    }

    @PostMapping("/notificaciones/{id}/leer")
    public String marcarComoLeida(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo estudiantes pueden revisar notificaciones.");
            return "redirect:/home";
        }

        try {
            notificacionService.marcarComoLeida(id, usuario);
        } catch (RegistroObjetoException exception) {
            redirectAttributes.addFlashAttribute("accesoDenegado", exception.getMessage());
        }
        return "redirect:/notificaciones";
    }

    @PostMapping("/notificaciones/leer-todas")
    public String marcarTodasComoLeidas(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo estudiantes pueden revisar notificaciones.");
            return "redirect:/home";
        }

        notificacionService.marcarTodasComoLeidas(usuario);
        return "redirect:/notificaciones";
    }

    private boolean esEstudiante(Usuario usuario) {
        return usuario != null && usuario.getRol() == Rol.ESTUDIANTE;
    }

    private Usuario obtenerUsuario(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioAutenticado");
    }
}
