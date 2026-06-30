package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.AuthException;
import pe.edu.ulima.ufound.service.AuthService;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        return session.getAttribute("usuarioAutenticado") == null ? "redirect:/login" : "redirect:/home";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        return session.getAttribute("usuarioAutenticado") == null ? "login" : "redirect:/home";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        try {
            Usuario usuario = authService.autenticar(correo, password);
            session.setAttribute("usuarioAutenticado", usuario);
            return "redirect:/home";
        } catch (AuthException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("correo", correo);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}

