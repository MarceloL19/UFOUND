
package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.DeteccionVisualService;
import pe.edu.ulima.ufound.service.ImagenAnalisisService;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

@RestController
@RequestMapping("/imagenes")
public class ImagenAnalisisController {

    private static final Logger LOG = LoggerFactory.getLogger(ImagenAnalisisController.class);
    private final ImagenAnalisisService imagenAnalisisService;
    private final DeteccionVisualService deteccionVisualService;

    public ImagenAnalisisController(
            ImagenAnalisisService imagenAnalisisService,
            DeteccionVisualService deteccionVisualService) {

        this.imagenAnalisisService = imagenAnalisisService;
        this.deteccionVisualService = deteccionVisualService;
    }

    @PostMapping(value = "/analizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RespuestaAnalisis> analizar(
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioAutenticado");

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new RespuestaAnalisis(
                            "ERROR",
                            "Debes iniciar sesión para analizar imágenes.",
                            null,
                            0L,
                            null, null, 0, false));
        }

        try {
            // La validación de HU-29 siempre precede a la inferencia.
            ImagenAnalisisService.ResultadoImagen resultado = imagenAnalisisService.validarImagen(imagen);
            DeteccionVisualService.Resultado visual = deteccionVisualService.analizar(resultado.datos());

            String mensaje = "SUGERENCIA".equals(visual.estado())
                    ? "Se encontraron características probables. Confirma o corrige los datos antes de registrar."
                    : "La imagen es válida, pero no se identificó un objeto con suficiente confianza. Continúa manualmente.";
            return ResponseEntity
                    .ok(new RespuestaAnalisis(
                            visual.estado(),
                            mensaje,
                            resultado.formato(),
                            resultado.tamanoBytes(),
                            visual.tipoProbable(),
                            visual.colorProbable(),
                            visual.confianza(),
                            visual.requiereConfirmacion()));

        } catch (RegistroObjetoException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(new RespuestaAnalisis(
                            "ERROR",
                            exception.getMessage(),
                            null,
                            0L,
                            null, null, 0, false));
        } catch (Exception exception) {
            LOG.error("No se pudo completar el análisis visual", exception);
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new RespuestaAnalisis(
                            "ERROR",
                            "No se pudo analizar la imagen. Puedes continuar con el registro manual.",
                            null, 0L, null, null, 0, false));
        }
    }

    public record RespuestaAnalisis(
            String estado,
            String mensaje,
            String formato,
            long tamanoBytes,
            String tipoProbable,
            String colorProbable,
            double confianza,
            boolean requiereConfirmacion) {
    }
}
