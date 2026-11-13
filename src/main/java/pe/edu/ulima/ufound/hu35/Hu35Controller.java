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
     * Permite al estudiante consultar su reporte y las coincidencias
     * previamente calculadas por el motor de UFOUND.
     */
    @GetMapping("/objetos-perdidos/detalle/{id}")
    public String verDetalleConSugerencias(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Obtener y validar el usuario de la sesión
        Object usuarioSesion = session.getAttribute("usuarioAutenticado");

        if (!(usuarioSesion instanceof Usuario usuario)
                || usuario.getRol() != Rol.ESTUDIANTE) {

            redirectAttributes.addFlashAttribute(
                    "accesoDenegado",
                    "Solo el estudiante propietario puede consultar sus sugerencias."
            );

            return "redirect:/home";
        }

        try {
            // Consultar el reporte y sus coincidencias
            DetalleObjetoPerdidoHu35 detalle =
                    hu35Service.obtenerDetalleConSugerencias(id, usuario);

            // Enviar los datos a la vista
            model.addAttribute("usuario", usuario);
            model.addAttribute("objeto", detalle.objeto());
            model.addAttribute("sugerencias", detalle.coincidencias());

            return "detalle-objeto-perdido";

        } catch (RegistroObjetoException exception) {

            redirectAttributes.addFlashAttribute(
                    "accesoDenegado",
                    exception.getMessage()
            );

            return "redirect:/objetos-perdidos/mis-reportes";
        }
    }
}
