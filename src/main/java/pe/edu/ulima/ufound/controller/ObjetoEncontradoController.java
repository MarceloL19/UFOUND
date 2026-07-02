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
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.RegistroObjetoEncontradoForm;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.ObjetoEncontradoService;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/objetos-encontrados")
public class ObjetoEncontradoController {

    private final ObjetoEncontradoService objetoEncontradoService;

    public ObjetoEncontradoController(ObjetoEncontradoService objetoEncontradoService) {
        this.objetoEncontradoService = objetoEncontradoService;
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esSeguridad(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo seguridad puede registrar objetos encontrados.");
            return "redirect:/home";
        }

        cargarCatalogos(model);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegistroObjetoEncontradoForm());
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("fechaHoraActual", LocalDateTime.now());
        return "registrar-objeto-encontrado";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute("form") RegistroObjetoEncontradoForm form,
                            @RequestParam(name = "imagen", required = false) MultipartFile imagen,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esSeguridad(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo seguridad puede registrar objetos encontrados.");
            return "redirect:/home";
        }

        try {
            ObjetoEncontrado objeto = objetoEncontradoService.registrar(form, imagen, usuario);
            return "redirect:/objetos-encontrados/detalle/" + objeto.getIdObjeto();
        } catch (RegistroObjetoException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/objetos-encontrados/registrar";
        }
    }

    @GetMapping
    public String listar(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esSeguridad(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo seguridad puede visualizar objetos encontrados.");
            return "redirect:/home";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("objetos", objetoEncontradoService.listarTodos());
        return "objetos-encontrados";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esSeguridad(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo seguridad puede visualizar objetos encontrados.");
            return "redirect:/home";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("objeto", objetoEncontradoService.obtenerPorId(id));
        return "detalle-objeto-encontrado";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("categorias", CategoriaObjeto.values());
        model.addAttribute("ubicaciones", UbicacionCampus.values());
    }

    private boolean esSeguridad(Usuario usuario) {
        return usuario != null && usuario.getRol() == Rol.SEGURIDAD;
    }

    private Usuario obtenerUsuario(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioAutenticado");
    }
}
