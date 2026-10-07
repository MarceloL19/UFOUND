package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.AyudaService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AyudaController {

    private static final String HISTORIAL = "ayudaHistorial";
    private static final String ROL_HISTORIAL = "ayudaRol";
    private static final int MAX_PREGUNTA = 300;
    private static final int MAX_INTERCAMBIOS = 10;

    private final AyudaService ayudaService;

    public AyudaController(AyudaService ayudaService) {
        this.ayudaService = ayudaService;
    }

    @GetMapping("/ayuda")
    public String mostrar(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        Rol rol = usuario.getRol();
        model.addAttribute("usuario", usuario);
        historial(session, rol);
        return "ayuda";
    }

    @GetMapping("/ayuda/conversacion")
    @ResponseBody
    public EstadoAyuda conversacion(@RequestParam(required = false) String tema, HttpSession session) {
        Rol rol = ((Usuario) session.getAttribute("usuarioAutenticado")).getRol();
        return estado(session, rol, tema, "");
    }

    @PostMapping("/ayuda/conversacion")
    @ResponseBody
    public ResponseEntity<EstadoAyuda> responderEnVentana(@RequestParam String pregunta, HttpSession session) {
        Rol rol = ((Usuario) session.getAttribute("usuarioAutenticado")).getRol();
        String texto = pregunta.strip();
        if (texto.isEmpty() || texto.length() > MAX_PREGUNTA) {
            return ResponseEntity.badRequest().body(estado(session, rol, "", "Escribe una pregunta de 1 a 300 caracteres."));
        }
        agregarIntercambio(session, rol, texto);
        return ResponseEntity.ok(estado(session, rol, "", ""));
    }

    @PostMapping("/ayuda")
    public String responder(@RequestParam String pregunta, HttpSession session, RedirectAttributes redirectAttributes) {
        String texto = pregunta.strip();
        if (texto.isEmpty() || texto.length() > MAX_PREGUNTA) {
            redirectAttributes.addFlashAttribute("error", "Escribe una pregunta de 1 a 300 caracteres.");
            return "redirect:/ayuda";
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        Rol rol = usuario.getRol();
        agregarIntercambio(session, rol, texto);
        return "redirect:/ayuda";
    }

    private void agregarIntercambio(HttpSession session, Rol rol, String texto) {
        String respuesta = ayudaService.responder(rol, texto);
        synchronized (session) {
            List<Mensaje> mensajes = new ArrayList<>(historial(session, rol));
            mensajes.add(new Mensaje("usuario", texto));
            mensajes.add(new Mensaje("asistente", respuesta));
            if (mensajes.size() > 1 + 2 * MAX_INTERCAMBIOS) {
                mensajes.subList(1, 3).clear();
            }
            session.setAttribute(HISTORIAL, List.copyOf(mensajes));
        }
    }

    private EstadoAyuda estado(HttpSession session, Rol rol, String tema, String error) {
        List<Opcion> opciones = ayudaService.temasPara(rol).stream()
                .map(t -> new Opcion(t.id(), t.titulo(), t.pregunta())).toList();
        return new EstadoAyuda(historial(session, rol), opciones, ayudaService.preguntaSugerida(rol, tema), error);
    }

    @SuppressWarnings("unchecked")
    private List<Mensaje> historial(HttpSession session, Rol rol) {
        synchronized (session) {
            if (session.getAttribute(ROL_HISTORIAL) != rol) {
                List<Mensaje> inicial = List.of(new Mensaje("asistente", ayudaService.saludo(rol)));
                session.setAttribute(ROL_HISTORIAL, rol);
                session.setAttribute(HISTORIAL, inicial);
                return inicial;
            }
            return (List<Mensaje>) session.getAttribute(HISTORIAL);
        }
    }

    public record Mensaje(String emisor, String texto) {
    }

    public record Opcion(String id, String titulo, String pregunta) {
    }

    public record EstadoAyuda(List<Mensaje> mensajes, List<Opcion> temas, String preguntaSugerida, String error) {
    }
}
