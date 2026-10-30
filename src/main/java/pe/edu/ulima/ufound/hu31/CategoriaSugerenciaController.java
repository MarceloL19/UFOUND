package pe.edu.ulima.ufound.hu31;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.Usuario;

import java.util.Optional;

@RestController
@RequestMapping("/hu31")
public class CategoriaSugerenciaController {

    private final CategoriaSugerenciaService service;

    public CategoriaSugerenciaController(CategoriaSugerenciaService service) {
        this.service = service;
    }

    @PostMapping("/sugerir-categoria")
    public ResponseEntity<RespuestaCategoria> sugerir(@RequestBody(required = false) ResultadoVisual datos,
                                                       HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RespuestaCategoria("ERROR", null, null,
                            "Debes iniciar sesión para solicitar sugerencias.", true));
        }
        if (datos == null) {
            return ResponseEntity.badRequest()
                    .body(new RespuestaCategoria("ERROR", null, null,
                            "No se recibió el resultado del análisis.", true));
        }

        Optional<CategoriaObjeto> categoria = service.sugerir(datos.estado(), datos.tipoProbable());
        if (categoria.isEmpty()) {
            return ResponseEntity.ok(new RespuestaCategoria("SIN_SUGERENCIA", null, null,
                    "No hay una categoría sugerida. Selecciona una manualmente.", true));
        }
        CategoriaObjeto valor = categoria.get();
        return ResponseEntity.ok(new RespuestaCategoria("SUGERENCIA", valor.name(), valor.getEtiqueta(),
                "Categoría sugerida: " + valor.getEtiqueta() + ". Confirma o elige otra categoría.", true));
    }

    public record ResultadoVisual(String estado, String tipoProbable) {}
    public record RespuestaCategoria(String estado, String categoria, String etiqueta,
                                     String mensaje, boolean requiereConfirmacion) {}
}
