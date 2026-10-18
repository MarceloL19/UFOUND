
package pe.edu.ulima.ufound.controller;

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

        @BeforeEach
        void configurar() {

                // Utilizamos el servicio real, sin Mockito.
                ImagenAnalisisService servicio = new ImagenAnalisisService();

                ImagenAnalisisController controlador = new ImagenAnalisisController(servicio);

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
                                                .value("LISTA_PARA_ANALISIS"))
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
}
