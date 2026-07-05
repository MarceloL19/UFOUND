package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.CoincidenciaService;
import pe.edu.ulima.ufound.service.NotificacionService;
import pe.edu.ulima.ufound.service.ObjetoEncontradoService;
import pe.edu.ulima.ufound.service.ObjetoPerdidoService;

@Controller
public class HomeController {

    private final ObjetoPerdidoService objetoPerdidoService;
    private final ObjetoEncontradoService objetoEncontradoService;
    private final CoincidenciaService coincidenciaService;
    private final NotificacionService notificacionService;

    public HomeController(ObjetoPerdidoService objetoPerdidoService, ObjetoEncontradoService objetoEncontradoService,
                          CoincidenciaService coincidenciaService, NotificacionService notificacionService) {
        this.objetoPerdidoService = objetoPerdidoService;
        this.objetoEncontradoService = objetoEncontradoService;
        this.coincidenciaService = coincidenciaService;
        this.notificacionService = notificacionService;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        model.addAttribute("usuario", usuario);

        if (usuario.getRol() == Rol.ESTUDIANTE) {
            model.addAttribute("totalReportes", objetoPerdidoService.contarPorUsuario(usuario));
            model.addAttribute("totalCoincidencias", coincidenciaService.contarPorUsuario(usuario));
            model.addAttribute("totalNotificaciones", notificacionService.contarNoLeidas(usuario));
            return "home-estudiante";
        }
        if (usuario.getRol() == Rol.SEGURIDAD) {
            model.addAttribute("totalHallazgos", objetoEncontradoService.contarPorUsuario(usuario));
            return "home-seguridad";
        }
        return "home-oficina";
    }
}

