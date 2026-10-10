package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.Rol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AyudaServiceTest {

    private final AyudaService ayuda = new AyudaService();

    @Test
    void ofreceSoloTemasDelRolYNoSugiereTemasAjenos() {
        assertEquals(4, ayuda.temasPara(Rol.ESTUDIANTE).size());
        assertEquals(3, ayuda.temasPara(Rol.SEGURIDAD).size());
        assertEquals(2, ayuda.temasPara(Rol.OFICINA).size());
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
}
