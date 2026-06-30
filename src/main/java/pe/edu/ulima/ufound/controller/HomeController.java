package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        model.addAttribute("usuario", usuario);

        if (usuario.getRol() == Rol.ESTUDIANTE) {
            return "home-estudiante";
        }
        if (usuario.getRol() == Rol.SEGURIDAD) {
            return "home-seguridad";
        }
        return "home-oficina";
    }
}

