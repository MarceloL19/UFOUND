package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.UUID;
import java.util.Objects;

@Controller
public class AyudaController {

    private static final String HISTORIAL = "ayudaHistorial";
    private static final String USUARIO_HISTORIAL = "ayudaUsuario";
    private static final String NAVEGACION = "ayudaNavegacion";
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
    public ResponseEntity<EstadoAyuda> responderEnVentana(@RequestParam String pregunta, HttpSession session, HttpServletRequest request) {
        Rol rol = ((Usuario) session.getAttribute("usuarioAutenticado")).getRol();
        String texto = pregunta.strip();
        if (texto.isEmpty() || texto.length() > MAX_PREGUNTA) {
            return ResponseEntity.badRequest().body(estado(session, rol, "", "Escribe una pregunta de 1 a 300 caracteres."));
        }
        synchronized (session) {
            AyudaService.Destino destino = agregarIntercambio(session, rol, texto);
            Navegacion navegacion = null;
            if (destino != null) {
                navegacion = new Navegacion(UUID.randomUUID().toString(), request.getContextPath() + destino.ruta());
                session.setAttribute(NAVEGACION, new Pendiente(navegacion, destino));
            }
            EstadoAyuda estado = estado(session, rol, "", "");
            return ResponseEntity.ok(new EstadoAyuda(estado.mensajes(), estado.temas(), "", "", navegacion));
        }
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

    @PostMapping("/ayuda/llegada")
    @ResponseBody
    public ResponseEntity<EstadoAyuda> confirmarLlegada(@RequestParam String token, @RequestParam String ruta,
                                                       HttpSession session) {
        Rol rol = ((Usuario) session.getAttribute("usuarioAutenticado")).getRol();
        synchronized (session) {
            historial(session, rol);
            Pendiente pendiente = (Pendiente) session.getAttribute(NAVEGACION);
            if (pendiente == null || !pendiente.navegacion().token().equals(token)
                    || !pendiente.navegacion().ruta().equals(ruta)) {
                return ResponseEntity.badRequest().body(estado(session, rol, "", "La navegación ya no está disponible. Puedes hacer otra consulta."));
            }
            List<Mensaje> mensajes = new ArrayList<>(historial(session, rol));
            mensajes.add(new Mensaje("asistente", "Abrí el apartado " + pendiente.destino().titulo()
                    + ". " + pendiente.destino().descripcion()));
            guardarHistorial(session, mensajes);
            session.removeAttribute(NAVEGACION);
            return ResponseEntity.ok(estado(session, rol, "", ""));
        }
    }

    private AyudaService.Destino agregarIntercambio(HttpSession session, Rol rol, String texto) {
        AyudaService.Respuesta respuesta = ayudaService.orientar(rol, texto);
        synchronized (session) {
            List<Mensaje> mensajes = new ArrayList<>(historial(session, rol));
            session.removeAttribute(NAVEGACION);
            mensajes.add(new Mensaje("usuario", texto));
            mensajes.add(new Mensaje("asistente", respuesta.texto()));
            guardarHistorial(session, mensajes);
            return respuesta.destino();
        }
    }

    private void guardarHistorial(HttpSession session, List<Mensaje> mensajes) {
        while (mensajes.stream().filter(m -> m.emisor().equals("usuario")).count() > MAX_INTERCAMBIOS) {
            mensajes.remove(1);
            while (mensajes.size() > 1 && !mensajes.get(1).emisor().equals("usuario")) {
                mensajes.remove(1);
            }
        }
        session.setAttribute(HISTORIAL, List.copyOf(mensajes));
    }

    private EstadoAyuda estado(HttpSession session, Rol rol, String tema, String error) {
        List<Opcion> opciones = ayudaService.temasPara(rol).stream()
                .map(t -> new Opcion(t.id(), t.titulo(), t.pregunta())).toList();
        return new EstadoAyuda(historial(session, rol), opciones, ayudaService.preguntaSugerida(rol, tema), error, null);
    }

    @SuppressWarnings("unchecked")
    private List<Mensaje> historial(HttpSession session, Rol rol) {
        synchronized (session) {
            Long usuarioId = ((Usuario) session.getAttribute("usuarioAutenticado")).getIdUsuario();
            if (session.getAttribute(ROL_HISTORIAL) != rol
                    || !Objects.equals(session.getAttribute(USUARIO_HISTORIAL), usuarioId)) {
                List<Mensaje> inicial = List.of(new Mensaje("asistente", ayudaService.saludo(rol)));
                session.removeAttribute(NAVEGACION);
                session.setAttribute(ROL_HISTORIAL, rol);
                session.setAttribute(USUARIO_HISTORIAL, usuarioId);
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

    private record Pendiente(Navegacion navegacion, AyudaService.Destino destino) {
    }

    public record Navegacion(String token, String ruta) {
    }

    public record EstadoAyuda(List<Mensaje> mensajes, List<Opcion> temas, String preguntaSugerida, String error,
                              Navegacion navegacion) {
    }
}
