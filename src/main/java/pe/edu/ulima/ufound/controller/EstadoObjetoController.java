package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.EstadoObjetoResumen;
import pe.edu.ulima.ufound.service.EstadoObjetoService;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

@Controller
public class EstadoObjetoController {

    private final EstadoObjetoService estadoObjetoService;

    public EstadoObjetoController(EstadoObjetoService estadoObjetoService) {
        this.estadoObjetoService = estadoObjetoService;
    }

    @GetMapping("/estados/{tipoObjeto}/{idObjeto}")
    public String verEstado(@PathVariable TipoObjeto tipoObjeto,
                            @PathVariable Long idObjeto,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        EstadoObjetoResumen resumen = obtenerResumen(tipoObjeto, idObjeto);
        if (!puedeVisualizar(usuario, tipoObjeto, idObjeto)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "No tienes permisos para visualizar este seguimiento.");
            return "redirect:/home";
        }
        estadoObjetoService.asegurarHistorialInicial(tipoObjeto, idObjeto);
        resumen = obtenerResumen(tipoObjeto, idObjeto);

        model.addAttribute("usuario", usuario);
        model.addAttribute("resumen", resumen);
        model.addAttribute("lineaTiempo", estadoObjetoService.construirLineaTiempo(tipoObjeto, idObjeto, resumen.getEstado()));
        return "estado-objeto";
    }

    @GetMapping("/oficina/estados")
    public String listarGestionEstados(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esOficina(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo oficina puede gestionar estados.");
            return "redirect:/home";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("objetos", estadoObjetoService.listarActividadReciente());
        return "gestion-estados";
    }

    @GetMapping("/oficina/estados/{tipoObjeto}/{idObjeto}")
    public String mostrarGestion(@PathVariable TipoObjeto tipoObjeto,
                                 @PathVariable Long idObjeto,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esOficina(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo oficina puede gestionar estados.");
            return "redirect:/home";
        }

        EstadoObjetoResumen resumen = obtenerResumen(tipoObjeto, idObjeto);
        estadoObjetoService.asegurarHistorialInicial(tipoObjeto, idObjeto);
        resumen = obtenerResumen(tipoObjeto, idObjeto);
        model.addAttribute("usuario", usuario);
        model.addAttribute("resumen", resumen);
        model.addAttribute("estados", estadoObjetoService.estadosGestionables());
        model.addAttribute("lineaTiempo", estadoObjetoService.construirLineaTiempo(tipoObjeto, idObjeto, resumen.getEstado()));
        return "gestion-estado-detalle";
    }

    @PostMapping("/oficina/estados/{tipoObjeto}/{idObjeto}")
    public String cambiarEstado(@PathVariable TipoObjeto tipoObjeto,
                                @PathVariable Long idObjeto,
                                @RequestParam EstadoObjeto estado,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Usuario usuario = obtenerUsuario(session);
        if (!esOficina(usuario)) {
            redirectAttributes.addFlashAttribute("accesoDenegado", "Solo oficina puede gestionar estados.");
            return "redirect:/home";
        }

        try {
            estadoObjetoService.cambiarEstado(tipoObjeto, idObjeto, estado, usuario);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado correctamente.");
        } catch (RegistroObjetoException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/oficina/estados/" + tipoObjeto + "/" + idObjeto;
    }

    private EstadoObjetoResumen obtenerResumen(TipoObjeto tipoObjeto, Long idObjeto) {
        if (tipoObjeto == TipoObjeto.PERDIDO) {
            ObjetoPerdido objeto = estadoObjetoService.obtenerObjetoPerdido(idObjeto);
            return new EstadoObjetoResumen(tipoObjeto, objeto.getIdObjeto(), objeto.getNombre(), objeto.getCategoria(),
                    objeto.getUbicacion(), objeto.getFechaHora(), objeto.getImagenUrl(), objeto.getEstado());
        }
        ObjetoEncontrado objeto = estadoObjetoService.obtenerObjetoEncontrado(idObjeto);
        return new EstadoObjetoResumen(tipoObjeto, objeto.getIdObjeto(), objeto.getNombre(), objeto.getCategoria(),
                objeto.getUbicacion(), objeto.getFechaHora(), objeto.getImagenUrl(), objeto.getEstado());
    }

    private boolean puedeVisualizar(Usuario usuario, TipoObjeto tipoObjeto, Long idObjeto) {
        if (usuario == null) {
            return false;
        }
        if (usuario.getRol() == Rol.OFICINA) {
            return true;
        }
        if (usuario.getRol() == Rol.ESTUDIANTE && tipoObjeto == TipoObjeto.PERDIDO) {
            return estadoObjetoService.objetoPerdidoPerteneceA(idObjeto, usuario);
        }
        return usuario.getRol() == Rol.SEGURIDAD && tipoObjeto == TipoObjeto.ENCONTRADO;
    }

    private boolean esOficina(Usuario usuario) {
        return usuario != null && usuario.getRol() == Rol.OFICINA;
    }

    private Usuario obtenerUsuario(HttpSession session) {
        return (Usuario) session.getAttribute("usuarioAutenticado");
    }
}
