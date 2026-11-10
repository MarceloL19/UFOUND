package pe.edu.ulima.ufound.hu32;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.ulima.ufound.model.Usuario;

import java.util.Map;

@RestController
@RequestMapping("/hu32")
public class AutocompletadoFormularioController {

    private final AutocompletadoFormularioService service;

    public AutocompletadoFormularioController(AutocompletadoFormularioService service) {
        this.service = service;
    }

    @PostMapping("/autocompletar")
    public ResponseEntity<RespuestaAutocompletado> autocompletar(
            @RequestBody(required = false) ResultadoVisual datos, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RespuestaAutocompletado("ERROR", Map.of(),
                            "Debes iniciar sesión para autocompletar el formulario."));
        }
        if (datos == null) {
            return ResponseEntity.badRequest()
                    .body(new RespuestaAutocompletado("ERROR", Map.of(),
                            "No se recibió el resultado del análisis."));
        }

        Map<String, String> campos = service.sugerirCampos(
                datos.estado(), datos.tipoProbable(), datos.colorProbable());
        if (campos.isEmpty()) {
            return ResponseEntity.ok(new RespuestaAutocompletado("SIN_DATOS", campos,
                    "El análisis no respalda ningún campo. Completa el formulario manualmente."));
        }
        return ResponseEntity.ok(new RespuestaAutocompletado("AUTOCOMPLETADO", campos,
                "Revisa los campos sugeridos y corrígelos si hace falta. La ubicación debes indicarla tú."));
    }

    public record ResultadoVisual(String estado, String tipoProbable, String colorProbable) {}
    public record RespuestaAutocompletado(String estado, Map<String, String> campos, String mensaje) {}
}
