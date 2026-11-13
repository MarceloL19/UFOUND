package pe.edu.ulima.ufound.hu35;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

@Controller
public class Hu35Controller {

    private final Hu35Service hu35Service;

    public Hu35Controller(Hu35Service hu35Service) {
        this.hu35Service = hu35Service;
    }

    /**
     * El estudiante consulta su reporte y recibe las coincidencias previamente
     * calculadas por el motor de UFOUND.
     */
    @GetMapping("/objetos-perdidos/detalle/{id}")
    public String verDetalleConSugerencias(@PathVariable Long id,
                                           HttpSession session,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        if (usuario == null || usuario.getRol() != Rol.ESTUDIANTE) {
            redirectAttributes.addFlashAttribute("accesoDenegado",
                    "Solo el estudiante propietario puede consultar sus sugerencias.");
            return "redirect:/home";
        }

        try {
            DetalleObjetoPerdidoHu35 detalle = hu35Service.obtenerDetalleConSugerencias(id, usuario);
            model.addAttribute("usuario", usuario);
            model.addAttribute("objeto", detalle.objeto());
            model.addAttribute("sugerencias", detalle.coincidencias());
            return "detalle-objeto-perdido";
        } catch (RegistroObjetoException exception) {
            redirectAttributes.addFlashAttribute("accesoDenegado", Hu35Service.MENSAJE_SIN_ACCESO);
            return "redirect:/objetos-perdidos/mis-reportes";
        }
    }
}
