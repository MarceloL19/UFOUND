package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.BusquedaObjetoService;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

import java.time.LocalDate;

@Controller
public class BusquedaController {

    private final BusquedaObjetoService busquedaObjetoService;

    public BusquedaController(BusquedaObjetoService busquedaObjetoService) {
        this.busquedaObjetoService = busquedaObjetoService;
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String texto,
                         @RequestParam(required = false) CategoriaObjeto categoria,
                         @RequestParam(required = false) UbicacionCampus ubicacion,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "La busqueda de objetos esta disponible solo para estudiantes.");
            return "redirect:/home";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("categorias", CategoriaObjeto.values());
        model.addAttribute("ubicaciones", UbicacionCampus.values());
        model.addAttribute("texto", texto);
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("ubicacionSeleccionada", ubicacion);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);
        model.addAttribute("resultados", busquedaObjetoService.buscar(texto, categoria, ubicacion, fechaDesde, fechaHasta));
        model.addAttribute("recientes", busquedaObjetoService.listarRecientes());
        return "buscar-objetos";
    }

    @GetMapping("/buscar/detalle/{tipoObjeto}/{idObjeto}")
    public String detalle(@PathVariable TipoObjeto tipoObjeto,
                          @PathVariable Long idObjeto,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esEstudiante(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "La busqueda de objetos esta disponible solo para estudiantes.");
            return "redirect:/home";
        }

        model.addAttribute("usuario", usuario);
        try {
            model.addAttribute("objeto", busquedaObjetoService.obtenerDetalle(tipoObjeto, idObjeto));
            return "detalle-busqueda";
        } catch (RegistroObjetoException exception) {
            redirectAttributes.addFlashAttribute("accesoDenegado", exception.getMessage());
            return "redirect:/buscar";
        }
    }

    private boolean esEstudiante(Usuario usuario) {
        return usuario != null && usuario.getRol() == Rol.ESTUDIANTE;
    }

    private Usuario obtenerUsuario(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioAutenticado");
    }
}
