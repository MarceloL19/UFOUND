package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.ulima.ufound.config.SesionInterceptor;
import pe.edu.ulima.ufound.config.WebConfig;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.AyudaService;

import java.util.List;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AyudaController.class)
@Import({AyudaService.class, SesionInterceptor.class, WebConfig.class})
class AyudaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void requiereAutenticacionParaAbrirYEnviar() throws Exception {
        mvc.perform(get("/ayuda"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mvc.perform(post("/ayuda").param("pregunta", "¿Cómo busco?"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mvc.perform(get("/ayuda/conversacion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void abreConversacionYTemasDelRol() throws Exception {
        MockHttpSession sesion = sesion(Rol.ESTUDIANTE);
        mvc.perform(get("/ayuda").session(sesion).param("tema", "reportar-perdida"))
                .andExpect(status().isOk())
                .andExpect(view().name("ayuda"))
                .andExpect(content().string(containsString("assistant-widget")))
                .andExpect(content().string(containsString("/img/ulises-asistente.png")))
                .andExpect(content().string(containsString("data-api=\"/ayuda/conversacion\"")));
        assertEquals(1, historial(sesion).size());
        mvc.perform(get("/ayuda/conversacion").session(sesion).param("tema", "reportar-perdida"))
                .andExpect(jsonPath("$.temas.length()").value(4))
                .andExpect(jsonPath("$.preguntaSugerida").value("¿Cómo registro un objeto perdido?"))
                .andExpect(content().string(not(containsString("Gestionar estados"))));
    }

    @Test
    void conservaIntercambioEnSesionYNoLoComparteConOtraSesion() throws Exception {
        MockHttpSession primera = sesion(Rol.ESTUDIANTE);
        MockHttpSession segunda = sesion(Rol.ESTUDIANTE);
        mvc.perform(post("/ayuda/conversacion").session(primera).param("pregunta", "¿Cómo busco hallazgos?"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensajes.length()").value(3))
                .andExpect(jsonPath("$.mensajes[1].texto").value("¿Cómo busco hallazgos?"))
                .andExpect(jsonPath("$.mensajes[2].texto").value(containsString("Abre Buscar (/buscar)")));
        mvc.perform(get("/ayuda/conversacion").session(primera))
                .andExpect(jsonPath("$.mensajes.length()").value(3));
        mvc.perform(get("/ayuda/conversacion").session(segunda))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensajes.length()").value(1));

        assertEquals(3, historial(primera).size());
        assertEquals("usuario", historial(primera).get(1).emisor());
        assertTrue(historial(primera).get(2).texto().contains("/buscar"));
        assertEquals(1, historial(segunda).size());
    }

    @Test
    void usaElRolActualYReiniciaHistorialSiCambia() throws Exception {
        MockHttpSession sesion = sesion(Rol.ESTUDIANTE);
        mvc.perform(post("/ayuda").session(sesion).param("pregunta", "¿Cómo busco?"));
        ((Usuario) sesion.getAttribute("usuarioAutenticado")).setRol(Rol.OFICINA);
        mvc.perform(get("/ayuda/conversacion").session(sesion).param("tema", "reportar-perdida"))
                .andExpect(jsonPath("$.preguntaSugerida").value(""))
                .andExpect(content().string(containsString("Gestionar estados")))
                .andExpect(content().string(not(containsString("Reportar una pérdida"))));
        assertEquals(1, historial(sesion).size());
        assertTrue(historial(sesion).get(0).texto().contains("Gestionar estados"));
    }

    @Test
    void rechazaPreguntaVaciaSinAgregarMensajes() throws Exception {
        MockHttpSession sesion = sesion(Rol.SEGURIDAD);
        mvc.perform(get("/ayuda").session(sesion)).andExpect(status().isOk());
        mvc.perform(post("/ayuda/conversacion").session(sesion).param("pregunta", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Escribe una pregunta de 1 a 300 caracteres."));
        assertEquals(1, historial(sesion).size());
    }

    @Test
    void imagenOriginalYJavascriptSeSirvenComoRecursosEstaticos() throws Exception {
        mvc.perform(get("/img/ulises-asistente.png")).andExpect(status().isOk());
        mvc.perform(get("/js/ayuda.js")).andExpect(status().isOk());
        mvc.perform(get("/fotos/mochila-negra.jpg")).andExpect(status().isOk());
        mvc.perform(get(URI.create("http://localhost/fotos/llavero%20negro.png")))
                .andExpect(status().isOk());
    }

    @Test
    void recursoDeUploadsConservaLaRutaGuardadaEnLaBaseDeDatos() throws Exception {
        Path carpeta = Path.of("uploads", "objetos-perdidos");
        Files.createDirectories(carpeta);
        Path archivo = carpeta.resolve("prueba-" + UUID.randomUUID() + ".png");
        Files.write(archivo, new byte[]{1, 2, 3});
        try {
            mvc.perform(get("/uploads/objetos-perdidos/" + archivo.getFileName()))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(new byte[]{1, 2, 3}));
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    @Test
    void fotoPredeterminadaTambienCargaDesdeRutaAntiguaDeUploads() throws Exception {
        for (String nombre : List.of("audifonos-blancos.jpg", "audifonos-bluetooth.jpg", "audifonos-negros.jpg")) {
            byte[] foto = Files.readAllBytes(Path.of("src", "main", "resources", "static", "fotos", nombre));
            mvc.perform(get("/uploads/objetos-encontrados/" + nombre))
                    .andExpect(status().isOk())
                    .andExpect(content().bytes(foto));
        }
        byte[] foto = Files.readAllBytes(Path.of("src", "main", "resources", "static", "fotos", "mochila-negra.jpg"));
        mvc.perform(get("/uploads/objetos-perdidos/mochila-negra.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(foto));
    }

    private MockHttpSession sesion(Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setRol(rol);
        usuario.setNombre("Persona de prueba");
        MockHttpSession sesion = new MockHttpSession();
        sesion.setAttribute("usuarioAutenticado", usuario);
        return sesion;
    }

    @SuppressWarnings("unchecked")
    private List<AyudaController.Mensaje> historial(HttpSession sesion) {
        return (List<AyudaController.Mensaje>) sesion.getAttribute("ayudaHistorial");
    }
}
