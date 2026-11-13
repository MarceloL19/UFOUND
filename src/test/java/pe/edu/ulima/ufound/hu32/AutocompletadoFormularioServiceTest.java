package pe.edu.ulima.ufound.hu32;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.hu31.CategoriaSugerenciaService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AutocompletadoFormularioServiceTest {

    private final AutocompletadoFormularioService service =
            new AutocompletadoFormularioService(new CategoriaSugerenciaService());

    @Test
    void completaTituloCategoriaYDescripcionConTipoYColor() {
        Map<String, String> campos = service.sugerirCampos("SUGERENCIA", "Mochila", "Negro");

        assertEquals("Mochila negra", campos.get("nombre"));
        assertEquals("ACCESORIOS", campos.get("categoria"));
        assertEquals("Mochila de color negro.", campos.get("descripcion"));
    }

    @Test
    void nuncaInfiereLaUbicacion() {
        Map<String, String> campos = service.sugerirCampos("SUGERENCIA", "Laptop", "Gris");

        assertFalse(campos.containsKey("ubicacion"));
        assertEquals(3, campos.size());
    }

    @Test
    void sinColorNoCompletaLaDescripcion() {
        Map<String, String> campos = service.sugerirCampos("SUGERENCIA", "Celular", null);

        assertEquals("Celular", campos.get("nombre"));
        assertEquals("TECNOLOGIA", campos.get("categoria"));
        assertFalse(campos.containsKey("descripcion"));
    }

    @Test
    void descartaColoresQueNoProduceElAnalisis() {
        Map<String, String> campos = service.sugerirCampos("SUGERENCIA", "Botella", "<b>fucsia</b>");

        assertEquals("Botella", campos.get("nombre"));
        assertFalse(campos.containsKey("descripcion"));
    }

    @Test
    void concuerdaElColorConElGeneroDelTipo() {
        assertEquals("Laptop blanca", service.sugerirCampos("SUGERENCIA", "Laptop", "Blanco").get("nombre"));
        assertEquals("Mouse blanco", service.sugerirCampos("SUGERENCIA", "Mouse", "Blanco").get("nombre"));
        assertEquals("Maleta azul", service.sugerirCampos("SUGERENCIA", "Maleta", "Azul").get("nombre"));
        assertEquals("Libro marrón", service.sugerirCampos("SUGERENCIA", "Libro", "Marrón").get("nombre"));
    }

    @Test
    void sinDeteccionOTipoNoSoportadoNoCompletaNada() {
        assertTrue(service.sugerirCampos("SIN_DETECCION", null, null).isEmpty());
        assertTrue(service.sugerirCampos("SUGERENCIA", "Paraguas", "Negro").isEmpty());
        assertTrue(service.sugerirCampos("SUGERENCIA", null, "Negro").isEmpty());
    }
}
