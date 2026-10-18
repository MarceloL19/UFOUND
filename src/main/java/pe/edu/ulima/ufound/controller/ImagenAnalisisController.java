
package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.ImagenAnalisisService;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

@RestController
@RequestMapping("/imagenes")
public class ImagenAnalisisController {

    private final ImagenAnalisisService imagenAnalisisService;

    public ImagenAnalisisController(
            ImagenAnalisisService imagenAnalisisService) {

        this.imagenAnalisisService = imagenAnalisisService;
    }

    @PostMapping(value = "/analizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RespuestaAnalisis> analizar(
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            HttpSession session) {

        // 1. Obtener el usuario autenticado
        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");

        // 2. Verificar que exista una sesión válida
        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new RespuestaAnalisis(
                            "ERROR",
                            "Debes iniciar sesión para analizar imágenes.",
                            null,
                            0L));
        }

        try {
            // 3. Validar la imagen mediante el servicio HU-29
            ImagenAnalisisService.ResultadoImagen resultado = imagenAnalisisService.validarImagen(imagen);

            // 4. Responder cuando la validación sea satisfactoria
            return ResponseEntity
                    .ok(new RespuestaAnalisis(
                            resultado.estado(),
                            "Imagen validada y lista para el análisis.",
                            resultado.formato(),
                            resultado.tamanoBytes()));

        } catch (RegistroObjetoException exception) {

            // 5. Responder ante un archivo inválido
            return ResponseEntity
                    .badRequest()
                    .body(new RespuestaAnalisis(
                            "ERROR",
                            exception.getMessage(),
                            null,
                            0L));
        }
    }

    // Estructura JSON que recibirá el navegador
    public record RespuestaAnalisis(
            String estado,
            String mensaje,
            String formato,
            long tamanoBytes) {
    }
}
