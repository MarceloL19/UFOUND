package pe.edu.ulima.ufound.hu35;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class Hu35ControllerTest {

    private Hu35Service hu35Service;
    private MockMvc mvc;
    private MockHttpSession session;
    private Usuario estudiante;

    @BeforeEach
    void setUp() {
        hu35Service = mock(Hu35Service.class);
        mvc = MockMvcBuilders.standaloneSetup(new Hu35Controller(hu35Service)).build();
        estudiante = new Usuario();
        estudiante.setIdUsuario(7L);
        estudiante.setRol(Rol.ESTUDIANTE);
        session = new MockHttpSession();
        session.setAttribute("usuarioAutenticado", estudiante);
    }

    @Test
    void muestraElDetalleYLasSugerenciasDelReportePropio() throws Exception {
        ObjetoPerdido perdido = new ObjetoPerdido();
        perdido.setIdObjeto(21L);
        DetalleObjetoPerdidoHu35 detalle = new DetalleObjetoPerdidoHu35(perdido, List.of());
        when(hu35Service.obtenerDetalleConSugerencias(21L, estudiante)).thenReturn(detalle);

        mvc.perform(get("/objetos-perdidos/detalle/21").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("detalle-objeto-perdido"))
                .andExpect(model().attribute("objeto", perdido))
                .andExpect(model().attribute("sugerencias", List.of()));
    }

    @Test
    void rechazaRolesDistintosDeEstudiante() throws Exception {
        Usuario seguridad = new Usuario();
        seguridad.setIdUsuario(9L);
        seguridad.setRol(Rol.SEGURIDAD);
        MockHttpSession sesionSeguridad = new MockHttpSession();
        sesionSeguridad.setAttribute("usuarioAutenticado", seguridad);

        mvc.perform(get("/objetos-perdidos/detalle/21").session(sesionSeguridad))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attributeExists("accesoDenegado"));

        verifyNoInteractions(hu35Service);
    }

    @Test
    void redirigeCuandoElReporteNoExisteONoPerteneceAlEstudiante() throws Exception {
        when(hu35Service.obtenerDetalleConSugerencias(eq(21L), eq(estudiante)))
                .thenThrow(new RegistroObjetoException("No se encontro el reporte o no tienes permisos para consultarlo."));

        mvc.perform(get("/objetos-perdidos/detalle/21").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/objetos-perdidos/mis-reportes"))
                .andExpect(flash().attributeExists("accesoDenegado"));
    }
}
