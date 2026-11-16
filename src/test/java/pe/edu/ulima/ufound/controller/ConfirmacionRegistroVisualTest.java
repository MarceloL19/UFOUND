package pe.edu.ulima.ufound.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.ulima.ufound.model.*;
import pe.edu.ulima.ufound.service.*;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ObjetoPerdidoController.class, ObjetoEncontradoController.class})
class ConfirmacionRegistroVisualTest {
    @Autowired private MockMvc mvc;
    @MockBean private ObjetoPerdidoService perdidos;
    @MockBean private ObjetoEncontradoService encontrados;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void muestraResumenPropioConFotoOConAusenciaDeFoto(boolean foto) throws Exception {
        Usuario usuario = usuario(Rol.ESTUDIANTE);
        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setIdObjeto(5L);
        objeto.setNombre("Laptop <personal>");
        objeto.setDescripcion("Descripcion de prueba");
        objeto.setCategoria(CategoriaObjeto.TECNOLOGIA);
        objeto.setUbicacion(UbicacionCampus.BIBLIOTECA);
        objeto.setFechaHora(LocalDateTime.of(2026, 11, 16, 10, 30));
        objeto.setImagenUrl(foto ? "/fotos/laptop-negra.jpg" : null);
        when(perdidos.obtenerPropio(5L, usuario)).thenReturn(objeto);
        mvc.perform(get("/objetos-perdidos/confirmacion/5").session(sesion(usuario)))
                .andExpect(status().isOk()).andExpect(view().name("registro-perdido-confirmacion"))
                .andExpect(content().string(containsString("UF-PER-5")))
                .andExpect(content().string(containsString("Laptop &lt;personal&gt;")))
                .andExpect(content().string(containsString("Biblioteca")))
                .andExpect(content().string(containsString(foto ? "/fotos/laptop-negra.jpg" : "Sin foto adjunta")))
                .andExpect(content().string(not(containsString("<personal>"))));
    }

    @Test
    void reporteAjenoOInexistenteNoExponeResumen() throws Exception {
        Usuario usuario = usuario(Rol.ESTUDIANTE);
        when(perdidos.obtenerPropio(9L, usuario)).thenThrow(new RegistroObjetoException("No se pudo acceder al reporte solicitado."));
        mvc.perform(get("/objetos-perdidos/confirmacion/9").session(sesion(usuario)))
                .andExpect(redirectedUrl("/objetos-perdidos/mis-reportes"))
                .andExpect(flash().attributeExists("accesoDenegado"));
    }

    @Test
    void otroRolNoConsultaElReporte() throws Exception {
        mvc.perform(get("/objetos-perdidos/confirmacion/5").session(sesion(usuario(Rol.OFICINA))))
                .andExpect(redirectedUrl("/home"));
        verifyNoInteractions(perdidos);
    }

    @Test
    void registroDeHallazgoConservaDestinoYAnadeAvisoDeExito() throws Exception {
        ObjetoEncontrado objeto = new ObjetoEncontrado();
        objeto.setIdObjeto(8L);
        when(encontrados.registrar(any(), any(), any())).thenReturn(objeto);
        mvc.perform(post("/objetos-encontrados/registrar").session(sesion(usuario(Rol.SEGURIDAD)))
                        .param("nombre", "Hallazgo").param("categoria", "TECNOLOGIA")
                        .param("ubicacion", "A1").param("descripcion", "Prueba"))
                .andExpect(redirectedUrl("/objetos-encontrados/detalle/8"))
                .andExpect(flash().attribute("registroCompletado", true));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void detalleDeHallazgoPresentaConfirmacionSoloTrasRegistrar(boolean recienRegistrado) throws Exception {
        Usuario usuario = usuario(Rol.SEGURIDAD);
        ObjetoEncontrado objeto = new ObjetoEncontrado();
        objeto.setIdObjeto(8L);
        objeto.setNombre("Tomatodo azul");
        objeto.setCategoria(CategoriaObjeto.BOTELLAS);
        objeto.setUbicacion(UbicacionCampus.A1);
        objeto.setFechaHora(LocalDateTime.of(2026, 11, 16, 10, 30));
        objeto.setUsuario(usuario);
        when(encontrados.obtenerPorId(8L)).thenReturn(objeto);
        var solicitud = get("/objetos-encontrados/detalle/8").session(sesion(usuario));
        if (recienRegistrado) solicitud.flashAttr("registroCompletado", true);
        var resultado = mvc.perform(solicitud).andExpect(status().isOk())
                .andExpect(content().string(containsString("Tomatodo azul")))
                .andExpect(content().string(containsString("Sin foto adjunta")));
        if (recienRegistrado) resultado.andExpect(content().string(containsString("Objeto encontrado registrado")));
        else resultado.andExpect(content().string(not(containsString("Objeto encontrado registrado"))));
    }

    private Usuario usuario(Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);
        usuario.setRol(rol);
        usuario.setNombre("Prueba visual");
        return usuario;
    }

    private MockHttpSession sesion(Usuario usuario) {
        MockHttpSession sesion = new MockHttpSession();
        sesion.setAttribute("usuarioAutenticado", usuario);
        return sesion;
    }
}
