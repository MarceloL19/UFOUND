package pe.edu.ulima.ufound.hu32;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.ulima.ufound.hu31.CategoriaSugerenciaService;
import pe.edu.ulima.ufound.model.Usuario;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AutocompletadoFormularioControllerTest {
    private MockMvc mvc;
    private MockHttpSession sesion;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new AutocompletadoFormularioController(
                new AutocompletadoFormularioService(new CategoriaSugerenciaService()))).build();
        sesion = new MockHttpSession();
        sesion.setAttribute("usuarioAutenticado", new Usuario());
    }

    @Test
    void devuelveSoloLosCamposRespaldadosPorElAnalisis() throws Exception {
        mvc.perform(post("/hu32/autocompletar")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SUGERENCIA\",\"tipoProbable\":\"Botella\",\"colorProbable\":\"Verde\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("AUTOCOMPLETADO"))
                .andExpect(jsonPath("$.campos.nombre").value("Botella verde"))
                .andExpect(jsonPath("$.campos.categoria").value("BOTELLAS"))
                .andExpect(jsonPath("$.campos.descripcion").value("Botella de color verde."))
                .andExpect(jsonPath("$.campos.ubicacion").doesNotExist());
    }

    @Test
    void sinDeteccionNoDevuelveCampos() throws Exception {
        mvc.perform(post("/hu32/autocompletar")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SIN_DETECCION\",\"tipoProbable\":null,\"colorProbable\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("SIN_DATOS"))
                .andExpect(jsonPath("$.campos").isEmpty());
    }

    @Test
    void requiereSesionAutenticada() throws Exception {
        mvc.perform(post("/hu32/autocompletar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"SUGERENCIA\",\"tipoProbable\":\"Mochila\",\"colorProbable\":\"Negro\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaPeticionSinCuerpo() throws Exception {
        mvc.perform(post("/hu32/autocompletar")
                        .session(sesion)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
