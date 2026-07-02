package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.RegistroObjetoPerdidoForm;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.ObjetoPerdidoService;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/objetos-perdidos")
public class ObjetoPerdidoController {

    private final ObjetoPerdidoService objetoPerdidoService;

    public ObjetoPerdidoController(ObjetoPerdidoService objetoPerdidoService) {
        this.objetoPerdidoService = objetoPerdidoService;
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo los estudiantes pueden registrar objetos perdidos.");
            return "redirect:/home";
        }

        cargarCatalogos(model);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegistroObjetoPerdidoForm());
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("fechaHoraActual", LocalDateTime.now());
        return "registrar-objeto-perdido";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute("form") RegistroObjetoPerdidoForm form,
                            @RequestParam(name = "imagen", required = false) MultipartFile imagen,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo los estudiantes pueden registrar objetos perdidos.");
            return "redirect:/home";
        }

        try {
            ObjetoPerdido objeto = objetoPerdidoService.registrar(form, imagen, usuario);
            return "redirect:/objetos-perdidos/confirmacion/" + objeto.getIdObjeto();
        } catch (RegistroObjetoException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/objetos-perdidos/registrar";
        }
    }

    @GetMapping("/confirmacion/{id}")
    public String confirmacion(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            return "redirect:/home";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("idObjeto", id);
        return "registro-perdido-confirmacion";
    }

    @GetMapping("/mis-reportes")
    public String listarMisReportes(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo los estudiantes pueden consultar sus objetos perdidos.");
            return "redirect:/home";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("objetos", objetoPerdidoService.listarPorUsuario(usuario));
        return "mis-objetos-perdidos";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("categorias", CategoriaObjeto.values());
        model.addAttribute("ubicaciones", UbicacionCampus.values());
    }

    private boolean esEstudiante(Usuario usuario) {
        return usuario != null && usuario.getRol() == Rol.ESTUDIANTE;
    }

    private Usuario obtenerUsuario(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioAutenticado");
    }
}
