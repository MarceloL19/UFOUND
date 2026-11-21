package pe.edu.ulima.ufound.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.ulima.ufound.service.PasswordResetException;
import pe.edu.ulima.ufound.service.PasswordResetService;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/recuperar-contrasena")
    public String mostrarFormularioRecuperacion(
            @RequestParam(required = false) String enviado,
            Model model) {
        if (enviado != null) {
            model.addAttribute(
                    "mensaje",
                    "Si el correo está registrado, recibirás un enlace de recuperación.");
        }

        return "recuperar-contrasena";
    }

    @PostMapping("/recuperar-contrasena")
    public String solicitarRecuperacion(@RequestParam String correo) {
        passwordResetService.solicitarRecuperacion(correo);

        return "redirect:/recuperar-contrasena?enviado";
    }

    @GetMapping("/restablecer-contrasena")
    public String mostrarFormularioRestablecimiento(
            @RequestParam(required = false) String token,
            Model model) {
        boolean tokenValido = passwordResetService.esTokenValido(token);

        model.addAttribute("token", token);
        model.addAttribute("tokenValido", tokenValido);

        if (!tokenValido) {
            model.addAttribute(
                    "error",
                    "El enlace de recuperación no es válido o ha expirado.");
        }

        return "restablecer-contrasena";
    }

    @PostMapping("/restablecer-contrasena")
    public String restablecerContrasena(
            @RequestParam String token,
            @RequestParam String nuevaContrasena,
            @RequestParam String confirmacionContrasena,
            Model model) {
        try {
            passwordResetService.restablecerContrasena(
                    token,
                    nuevaContrasena,
                    confirmacionContrasena);

            return "redirect:/login?passwordChanged";
        } catch (PasswordResetException exception) {
            model.addAttribute("token", token);
            model.addAttribute(
                    "tokenValido",
                    passwordResetService.esTokenValido(token));
            model.addAttribute("error", exception.getMessage());

            return "restablecer-contrasena";
        }
    }
}