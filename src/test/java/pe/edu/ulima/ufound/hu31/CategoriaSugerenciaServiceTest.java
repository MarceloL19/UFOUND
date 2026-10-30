package pe.edu.ulima.ufound.hu31;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.CategoriaObjeto;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaSugerenciaServiceTest {
    private final CategoriaSugerenciaService service = new CategoriaSugerenciaService();

    @Test
    void convierteTodosLosTiposDeHu30ACategoriasReales() {
        assertEquals(CategoriaObjeto.ACCESORIOS, service.sugerir("SUGERENCIA", "Mochila").orElseThrow());
        assertEquals(CategoriaObjeto.ACCESORIOS, service.sugerir("SUGERENCIA", "Bolso").orElseThrow());
        assertEquals(CategoriaObjeto.ACCESORIOS, service.sugerir("SUGERENCIA", "Maleta").orElseThrow());
        assertEquals(CategoriaObjeto.BOTELLAS, service.sugerir("SUGERENCIA", "Botella").orElseThrow());
        assertEquals(CategoriaObjeto.TECNOLOGIA, service.sugerir("SUGERENCIA", "Laptop").orElseThrow());
        assertEquals(CategoriaObjeto.TECNOLOGIA, service.sugerir("SUGERENCIA", "Mouse").orElseThrow());
        assertEquals(CategoriaObjeto.TECNOLOGIA, service.sugerir("SUGERENCIA", "Celular").orElseThrow());
        assertEquals(CategoriaObjeto.UTILES, service.sugerir("SUGERENCIA", "Libro").orElseThrow());
    }

    @Test
    void toleraMayusculasEspaciosYTildes() {
        assertEquals(CategoriaObjeto.TECNOLOGIA, service.sugerir("SUGERENCIA", "  LÁPTOP ").orElseThrow());
    }

    @Test
    void sinDeteccionNoInventaCategoria() {
        assertTrue(service.sugerir("SIN_DETECCION", "Mochila").isEmpty());
        assertTrue(service.sugerir("ERROR", "Mochila").isEmpty());
        assertTrue(service.sugerir("SUGERENCIA", "Desconocido").isEmpty());
        assertTrue(service.sugerir("SUGERENCIA", null).isEmpty());
    }

    @Test
    void siempreSugiereValoresDelCatalogo() {
        for (String tipo : new String[] {"Mochila", "Bolso", "Maleta", "Botella",
                "Laptop", "Mouse", "Celular", "Libro"}) {
            CategoriaObjeto sugerida = service.sugerir("SUGERENCIA", tipo).orElseThrow();
            assertNotNull(CategoriaObjeto.valueOf(sugerida.name()));
        }
    }
}
