package pe.edu.ulima.ufound.analisis;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;

import java.time.LocalDate;

@Controller
public class AnalisisPerdidasController {

    private final AnalisisPerdidasService analisisPerdidasService;

    public AnalisisPerdidasController(AnalisisPerdidasService analisisPerdidasService) {
        this.analisisPerdidasService = analisisPerdidasService;
    }

    @GetMapping("/analisis-perdidas")
    public String ver(@RequestParam(name = "periodo", required = false) String periodo,
                      @RequestParam(name = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                      @RequestParam(name = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                      HttpSession session,
                      Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        if (usuario == null) {
            return "redirect:/login";
        }
        if (usuario.getRol() != Rol.OFICINA) {
            return "redirect:/home";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("ventanas", VentanaAnalisis.values());
        model.addAttribute("analisis", analisisPerdidasService.consultar(periodo, desde, hasta));
        return "analisis-perdidas";
    }
}
