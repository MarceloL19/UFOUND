
package pe.edu.ulima.ufound.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.DeteccionVisualService;
import pe.edu.ulima.ufound.service.ImagenAnalisisService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ImagenAnalisisControllerTest {

        private MockMvc mockMvc;
        private MockHttpSession sesion;
        private DeteccionVisualService detector;

        private byte[] png() {
                return Base64.getDecoder().decode(
                                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4X8gAAAPjAXFY9S0dAAAAAElFTkSuQmCC");
        }

        private MockMultipartFile imagenValida() {
                return new MockMultipartFile("imagen", "mochila.png", "image/png", png());
        }

        @BeforeEach
        void configurar() {

                ImagenAnalisisService servicio = new ImagenAnalisisService();
                detector = mock(DeteccionVisualService.class);

                ImagenAnalisisController controlador = new ImagenAnalisisController(servicio, detector);

                mockMvc = MockMvcBuilders
                                .standaloneSetup(controlador)
                                .build();

                // Simulamos una sesión autenticada.
                sesion = new MockHttpSession();
                sesion.setAttribute(
                                "usuarioAutenticado",
                                new Usuario());
        }

        @Test
        void debePresentarSugerenciaSinConfirmarla() throws Exception {
                when(detector.analizar(any())).thenReturn(
                                new DeteccionVisualService.Resultado("SUGERENCIA", "Laptop", "Gris", .937, true));

                mockMvc.perform(
                                multipart("/imagenes/analizar")
                                                .file(imagenValida())
                                                .session(sesion))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estado")
                                                .value("SUGERENCIA"))
                                .andExpect(jsonPath("$.formato")
                                                .value("PNG"))
                                .andExpect(jsonPath("$.tamanoBytes")
                                                .value(png().length))
                                .andExpect(jsonPath("$.tipoProbable").value("Laptop"))
                                .andExpect(jsonPath("$.colorProbable").value("Gris"))
                                .andExpect(jsonPath("$.requiereConfirmacion").value(true));
        }

        @Test
        void integraValidacionYModeloConFotoReal() throws Exception {
                DeteccionVisualService detectorReal = new DeteccionVisualService();
                try (InputStream archivo = getClass().getResourceAsStream("/static/fotos/laptop-gris.jpg")) {
                        assertNotNull(archivo);
                        MockMvc integrado = MockMvcBuilders.standaloneSetup(
                                        new ImagenAnalisisController(new ImagenAnalisisService(), detectorReal)).build();
                        integrado.perform(multipart("/imagenes/analizar")
                                        .file(new MockMultipartFile("imagen", "laptop-gris.jpg", "image/jpeg",
                                                        archivo.readAllBytes()))
                                        .session(sesion))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.estado").value("SUGERENCIA"))
                                        .andExpect(jsonPath("$.tipoProbable").value("Laptop"))
                                        .andExpect(jsonPath("$.colorProbable").value("Gris"))
                                        .andExpect(jsonPath("$.requiereConfirmacion").value(true));
                } finally {
                        detectorReal.cerrar();
                }
        }

        @Test
        void analizaWebpValidadoSinErrorDeDecodificacion() throws Exception {
                byte[] webp = Base64.getDecoder().decode(
                                "UklGRjwAAABXRUJQVlA4IDAAAAAQAgCdASoBAAEAAUAmJaACdLoB+AH4AAPIAP7gJX/3ebAGEv+L3/7nAWE0X+pAAAA=");
                DeteccionVisualService detectorReal = new DeteccionVisualService();
                try {
                        MockMvc integrado = MockMvcBuilders.standaloneSetup(
                                        new ImagenAnalisisController(new ImagenAnalisisService(), detectorReal)).build();
                        integrado.perform(multipart("/imagenes/analizar")
                                        .file(new MockMultipartFile("imagen", "objeto.webp", "image/webp", webp))
                                        .session(sesion))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.formato").value("WEBP"));
                } finally {
                        detectorReal.cerrar();
                }
        }

        @Test
        void debePermitirRegistroManualSiNoHayDeteccion() throws Exception {
                when(detector.analizar(any())).thenReturn(
                                new DeteccionVisualService.Resultado("SIN_DETECCION", null, null, 0, true));

                mockMvc.perform(multipart("/imagenes/analizar").file(imagenValida()).session(sesion))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estado").value("SIN_DETECCION"))
                                .andExpect(jsonPath("$.tipoProbable").isEmpty());
        }

        @Test
        void debePermitirRegistroManualSiFallaElDetector() throws Exception {
                when(detector.analizar(any())).thenThrow(new IllegalStateException("modelo no disponible"));

                mockMvc.perform(multipart("/imagenes/analizar").file(imagenValida()).session(sesion))
                                .andExpect(status().isServiceUnavailable())
                                .andExpect(jsonPath("$.mensaje").value(
                                                "No se pudo analizar la imagen. Puedes continuar con el registro manual."));
        }

        @Test
        void debeRechazarImagenInvalida() throws Exception {

                MockMultipartFile imagen = new MockMultipartFile(
                                "imagen",
                                "archivo.txt",
                                "text/plain",
                                "Archivo invalido".getBytes(
                                                StandardCharsets.UTF_8));

                mockMvc.perform(
                                multipart("/imagenes/analizar")
                                                .file(imagen)
                                                .session(sesion))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.estado")
                                                .value("ERROR"))
                                .andExpect(jsonPath("$.mensaje")
                                                .exists());
                verifyNoInteractions(detector);
        }

        @Test
        void debeRechazarUsuarioNoAutenticado() throws Exception {

                MockMultipartFile imagen = new MockMultipartFile(
                                "imagen",
                                "mochila.png",
                                "image/png",
                                new byte[] { 1, 2, 3 });

                mockMvc.perform(
                                multipart("/imagenes/analizar")
                                                .file(imagen))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.estado")
                                                .value("ERROR"));
                verifyNoInteractions(detector);
        }

        @Test
        void debeRechazarSolicitudSinImagen() throws Exception {

                mockMvc.perform(
                                multipart("/imagenes/analizar")
                                                .session(sesion))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.estado")
                                                .value("ERROR"))
                                .andExpect(jsonPath("$.mensaje")
                                                .value("Debes seleccionar una imagen."));
                verifyNoInteractions(detector);
        }
}
