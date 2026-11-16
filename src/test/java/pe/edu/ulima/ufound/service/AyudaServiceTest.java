package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pe.edu.ulima.ufound.model.Rol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AyudaServiceTest {

    private final AyudaService ayuda = new AyudaService();

    @Test
    void ofreceSoloTemasDelRolYNoSugiereTemasAjenos() {
        assertEquals(7, ayuda.temasPara(Rol.ESTUDIANTE).size());
        assertEquals(4, ayuda.temasPara(Rol.SEGURIDAD).size());
        assertEquals(4, ayuda.temasPara(Rol.OFICINA).size());
        assertEquals("", ayuda.preguntaSugerida(Rol.SEGURIDAD, "reportar-perdida"));
        assertEquals("¿Cómo registro un objeto perdido?", ayuda.preguntaSugerida(Rol.ESTUDIANTE, "reportar-perdida"));
    }

    @Test
    void respondeRegistroBusquedaYSeguimientoDelEstudianteConRutasReales() {
        assertTrue(ayuda.responder(Rol.ESTUDIANTE, "¿Cómo registro un objeto perdido?")
                .contains("/objetos-perdidos/registrar"));
        assertTrue(ayuda.responder(Rol.ESTUDIANTE, "¿Dónde busco un objeto encontrado?")
                .contains("/buscar"));
        assertTrue(ayuda.responder(Rol.ESTUDIANTE, "¿Cómo hago seguimiento de mi objeto?")
                .contains("/objetos-perdidos/mis-reportes"));
        assertTrue(ayuda.responder(Rol.ESTUDIANTE, "¿Cómo hago seguimiento de mi reporte?")
                .contains("Ver estado"));
        assertTrue(ayuda.responder(Rol.ESTUDIANTE, "¿Dónde veo mis reportes?")
                .contains("/objetos-perdidos/mis-reportes"));
    }

    @Test
    void respondeSoloFuncionesDeSeguridadYOficina() {
        assertTrue(ayuda.responder(Rol.SEGURIDAD, "¿Cómo registro un hallazgo?")
                .contains("/objetos-encontrados/registrar"));
        assertTrue(ayuda.responder(Rol.SEGURIDAD, "¿Dónde consulto los hallazgos?")
                .contains("/objetos-encontrados"));
        assertTrue(ayuda.responder(Rol.SEGURIDAD, "¿Cómo veo el estado de un hallazgo?")
                .contains("Ver estado"));
        assertTrue(ayuda.responder(Rol.OFICINA, "¿Dónde consulto los estados?")
                .contains("/oficina/estados"));
        assertTrue(ayuda.responder(Rol.OFICINA, "¿Cómo cambio el estado de un objeto?")
                .contains("guarda el cambio"));
    }

    @Test
    void declaraLimiteAnteConsultaAjenaAmbiguaODeOtroRol() {
        String ajena = ayuda.responder(Rol.ESTUDIANTE, "¿Cuál es el horario de la biblioteca?");
        assertTrue(ajena.contains("No tengo una respuesta segura"));
        assertTrue(ajena.contains("Buscar hallazgos"));
        assertFalse(ajena.contains("Gestionar estados"));
        assertTrue(ayuda.responder(Rol.ESTUDIANTE, "¿Cómo busco y hago seguimiento?")
                .contains("No tengo una respuesta segura"));
        assertTrue(ayuda.responder(Rol.ESTUDIANTE, "¿Cómo registro un usuario?")
                .contains("No tengo una respuesta segura"));
        assertTrue(ayuda.responder(Rol.ESTUDIANTE, "¿Cómo registro un hallazgo?")
                .contains("No tengo una respuesta segura"));
    }

    @ParameterizedTest
    @CsvSource({
            "ESTUDIANTE,Quiero reportar algo que perdí,/objetos-perdidos/registrar",
            "ESTUDIANTE,Quiero reportar un objeto perdido,/objetos-perdidos/registrar",
            "ESTUDIANTE,Llevame a buscar hallazgos,/buscar",
            "ESTUDIANTE,Quiero ver mis reportes,/objetos-perdidos/mis-reportes",
            "ESTUDIANTE,Como hago seguimiento de mi reporte,/objetos-perdidos/mis-reportes",
            "ESTUDIANTE,Quiero ver mis coincidencias,/coincidencias",
            "ESTUDIANTE,Abre mis notificaciones,/notificaciones",
            "SEGURIDAD,Quiero registrar un hallazgo,/objetos-encontrados/registrar",
            "SEGURIDAD,Quiero consultar hallazgos,/objetos-encontrados",
            "SEGURIDAD,Como veo el estado de un hallazgo,/objetos-encontrados",
            "OFICINA,Quiero consultar estados,/oficina/estados",
            "OFICINA,Quiero cambiar el estado de un objeto,/oficina/estados",
            "OFICINA,Abre las estadísticas,/analisis-perdidas",
            "ESTUDIANTE,Llévame al inicio,/home",
            "SEGURIDAD,Llévame al inicio,/home",
            "OFICINA,Llévame al inicio,/home"
    })
    void identificaDestinoYExplicacionParaLosTresRoles(Rol rol, String pregunta, String ruta) {
        AyudaService.Respuesta respuesta = ayuda.orientar(rol, pregunta);
        assertNotNull(respuesta.destino());
        assertEquals(ruta, respuesta.destino().ruta());
        assertFalse(respuesta.destino().descripcion().isBlank());
        assertTrue(respuesta.texto().contains(ruta));
    }

    @ParameterizedTest
    @CsvSource({
            "ESTUDIANTE,Quiero registrar un objeto encontrado",
            "ESTUDIANTE,Quiero cambiar el estado de mi reporte",
            "ESTUDIANTE,Quiero buscar y ver mis notificaciones",
            "ESTUDIANTE,Abre https://example.com",
            "SEGURIDAD,Quiero ver mis reportes",
            "SEGURIDAD,Quiero cambiar el estado de un hallazgo",
            "OFICINA,Quiero registrar un hallazgo",
            "OFICINA,Quiero consultar estados y las estadísticas"
    })
    void noNavegaConConsultasAmbiguasExternasODeOtroRol(Rol rol, String pregunta) {
        AyudaService.Respuesta respuesta = ayuda.orientar(rol, pregunta);
        assertNull(respuesta.destino());
        assertTrue(respuesta.texto().contains("¿A qué apartado quieres ir?"));
    }

    @Test
    void todasLasOpcionesOfrecidasTienenDestinoSinAmbiguedad() {
        for (Rol rol : Rol.values()) {
            for (AyudaService.Tema tema : ayuda.temasPara(rol)) {
                assertNotNull(ayuda.orientar(rol, tema.pregunta()).destino(), rol + ": " + tema.id());
            }
        }
    }
}
