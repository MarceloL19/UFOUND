package pe.edu.ulima.ufound.hu31;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.ulima.ufound.model.Usuario;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoriaSugerenciaControllerTest {
    private MockMvc mvc;
    private MockHttpSession sesion;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new CategoriaSugerenciaController(
                new CategoriaSugerenciaService())).build();
        sesion = new MockHttpSession();
        sesion.setAttribute("usuarioAutenticado", new Usuario());
    }

    @Test
    void sugiereUnaCategoriaDelCatalogo() throws Exception {
        mvc.perform(post("/hu31/sugerir-categoria")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SUGERENCIA\",\"tipoProbable\":\"Mochila\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoria").value("ACCESORIOS"))
                .andExpect(jsonPath("$.etiqueta").value("Accesorios"))
                .andExpect(jsonPath("$.requiereConfirmacion").value(true));
    }

    @Test
    void permiteSeleccionManualSiNoReconoceElTipo() throws Exception {
        mvc.perform(post("/hu31/sugerir-categoria")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SIN_DETECCION\",\"tipoProbable\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SIN_SUGERENCIA"))
                .andExpect(jsonPath("$.categoria").isEmpty());
    }

    @Test
    void requiereSesionAutenticada() throws Exception {
        mvc.perform(post("/hu31/sugerir-categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SUGERENCIA\",\"tipoProbable\":\"Mochila\"}"))
                .andExpect(status().isUnauthorized());
    }
}
