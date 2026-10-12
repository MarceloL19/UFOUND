package pe.edu.ulima.ufound.analisis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnlaceAnalisisPerdidasFilterTest {

    @Test
    void insertaElEnlaceAntesDeCerrarElMenu() {
        String html = "<nav><a href=\"/home\">Dashboard</a></nav>";

        String resultado = EnlaceAnalisisPerdidasFilter.insertarEnlace(html,
                "<a data-analisis-perdidas href=\"/analisis-perdidas\">Analisis de perdidas</a>");

        assertTrue(resultado.contains("href=\"/analisis-perdidas\""));
        assertTrue(resultado.indexOf("href=\"/analisis-perdidas\"") < resultado.indexOf("</nav>"));
        assertTrue(resultado.contains("Dashboard"));
    }

    @Test
    void noDuplicaElEnlaceSiLaPaginaYaLoTiene() {
        String html = "<nav><a data-analisis-perdidas href=\"/analisis-perdidas\">Analisis de perdidas</a></nav>";

        String resultado = EnlaceAnalisisPerdidasFilter.insertarEnlace(html, "<a data-analisis-perdidas></a>");

        assertEquals(html, resultado);
        assertFalse(resultado.contains("</a><a"));
    }

    @Test
    void dejaIntactaLaPaginaSinMenu() {
        String html = "<main>Sin navegacion</main>";

        assertEquals(html, EnlaceAnalisisPerdidasFilter.insertarEnlace(html, "<a data-analisis-perdidas></a>"));
    }
}
