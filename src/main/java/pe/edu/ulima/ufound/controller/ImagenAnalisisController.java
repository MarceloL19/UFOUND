
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
import pe.edu.ulima.ufound.service.ImagenAnalisisService;
import pe.edu.ulima.ufound.service.DeteccionVisualService;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

@RestController
@RequestMapping("/imagenes")
public class ImagenAnalisisController {

        private static final Logger logger = LoggerFactory.getLogger(ImagenAnalisisController.class);

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
                                                        null, 0L,
                                                        null, null, null, false));
                }

                try {
                        // HU-29: primero validar la fotografía.
                        ImagenAnalisisService.ResultadoImagen validacion = imagenAnalisisService.validarImagen(imagen);

                        // HU-30: analizar únicamente archivos válidos.
                        try {
                                DeteccionVisualService.Resultado deteccion = deteccionVisualService
                                                .analizar(imagen.getBytes());

                                String mensaje = "SUGERENCIA".equals(deteccion.estado())
                                                ? "Análisis completado. Confirma o corrige "
                                                                + "la sugerencia antes de registrar."
                                                : "Imagen válida. No se identificó un objeto "
                                                                + "con suficiente confianza. "
                                                                + "Puedes completar el registro manualmente.";

                                return ResponseEntity.ok(
                                                new RespuestaAnalisis(
                                                                deteccion.estado(),
                                                                mensaje,
                                                                validacion.formato(),
                                                                validacion.tamanoBytes(),
                                                                deteccion.tipoProbable(),
                                                                deteccion.colorProbable(),
                                                                deteccion.confianza(),
                                                                deteccion.requiereConfirmacion()));

                        } catch (Exception exception) {

                                logger.warn(
                                                "No se pudo completar la inferencia visual",
                                                exception);

                                // La fotografía fue validada por HU-29.
                                // Una falla del modelo no debe impedir el registro manual.
                                return ResponseEntity.ok(
                                                new RespuestaAnalisis(
                                                                "ANALISIS_NO_DISPONIBLE",
                                                                "Imagen válida, pero el análisis visual "
                                                                                + "no está disponible. Puedes completar "
                                                                                + "el registro manualmente.",
                                                                validacion.formato(),
                                                                validacion.tamanoBytes(),
                                                                null, null, null, false));
                        }

                } catch (RegistroObjetoException exception) {

                        // HU-29: preservar el rechazo de archivos inválidos.
                        return ResponseEntity
                                        .badRequest()
                                        .body(new RespuestaAnalisis(
                                                        "ERROR",
                                                        exception.getMessage(),
                                                        null, 0L,
                                                        null, null, null, false));
                }
        }

        public record RespuestaAnalisis(
                        String estado,
                        String mensaje,
                        String formato,
                        long tamanoBytes,
                        String tipoProbable,
                        String colorProbable,
                        Float confianza,
                        boolean requiereConfirmacion) {
        }
}
