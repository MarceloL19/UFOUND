
package pe.edu.ulima.ufound.controller;

import pe.edu.ulima.ufound.service.DeteccionVisualService;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.ImagenAnalisisService;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ImagenAnalisisControllerTest {

        private MockMvc mockMvc;
        private MockHttpSession sesion;
        private DeteccionVisualService detector;

        @BeforeEach
        void configurar() throws Exception {

                // Utilizamos el servicio real, sin Mockito.
                ImagenAnalisisService servicio = new ImagenAnalisisService();

                detector = mock(DeteccionVisualService.class);

                // Simulamos una detección exitosa de HU-30.
                when(detector.analizar(any(byte[].class)))
                                .thenReturn(
                                                new DeteccionVisualService.Resultado(
                                                                "SUGERENCIA",
                                                                "Laptop",
                                                                "NEGRO / MUY OSCURO",
                                                                0.4821f,
                                                                true));

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
        void debeAceptarImagenValida() throws Exception {

                byte[] png = java.util.Base64.getDecoder().decode(
                                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4X8gAAAPjAXFY9S0dAAAAAElFTkSuQmCC");

                MockMultipartFile imagen = new MockMultipartFile(
                                "imagen",
                                "mochila.png",
                                "image/png",
                                png);

                mockMvc.perform(
                                multipart("/imagenes/analizar")
                                                .file(imagen)
                                                .session(sesion))
                                .andExpect(status().isOk())

                                .andExpect(jsonPath("$.estado")
                                                .value("SUGERENCIA"))
                                .andExpect(jsonPath("$.tipoProbable")
                                                .value("Laptop"))
                                .andExpect(jsonPath("$.colorProbable")
                                                .value("NEGRO / MUY OSCURO"))
                                .andExpect(jsonPath("$.requiereConfirmacion")
                                                .value(true))
                                .andExpect(jsonPath("$.formato")
                                                .value("PNG"))
                                .andExpect(jsonPath("$.tamanoBytes")
                                                .value(png.length));
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
        }

        @Test
        void debePermitirRegistroManualSinDeteccion() throws Exception {

                byte[] png = java.util.Base64.getDecoder().decode(
                                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4X8gAAAPjAXFY9S0dAAAAAElFTkSuQmCC");

                MockMultipartFile imagen = new MockMultipartFile(
                                "imagen",
                                "objeto.png",
                                "image/png",
                                png);

                // El modelo no reconoce ningún objeto.
                when(detector.analizar(any(byte[].class)))
                                .thenReturn(new DeteccionVisualService.Resultado(
                                                "SIN_DETECCION",
                                                null,
                                                "NO DETERMINADO",
                                                0f,
                                                true));

                mockMvc.perform(
                                multipart("/imagenes/analizar")
                                                .file(imagen)
                                                .session(sesion))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estado")
                                                .value("SIN_DETECCION"))
                                .andExpect(jsonPath("$.tipoProbable")
                                                .doesNotExist())
                                .andExpect(jsonPath("$.requiereConfirmacion")
                                                .value(true));
        }

        @Test
        void debePermitirRegistroManualSiFallaLaIA() throws Exception {

                byte[] png = java.util.Base64.getDecoder().decode(
                                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAADElEQVR4nGP4X8gAAAPjAXFY9S0dAAAAAElFTkSuQmCC");

                MockMultipartFile imagen = new MockMultipartFile(
                                "imagen",
                                "objeto.png",
                                "image/png",
                                png);

                // Simulamos un fallo interno de ONNX.
                when(detector.analizar(any(byte[].class)))
                                .thenThrow(new RuntimeException(
                                                "Fallo simulado de inferencia"));

                mockMvc.perform(
                                multipart("/imagenes/analizar")
                                                .file(imagen)
                                                .session(sesion))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.estado")
                                                .value("ANALISIS_NO_DISPONIBLE"))
                                .andExpect(jsonPath("$.formato")
                                                .value("PNG"))
                                .andExpect(jsonPath("$.requiereConfirmacion")
                                                .value(false));
        }

}
