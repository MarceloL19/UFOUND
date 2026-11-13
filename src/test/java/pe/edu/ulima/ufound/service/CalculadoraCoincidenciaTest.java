package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraCoincidenciaTest {

    private final CalculadoraCoincidencia calculadora = new CalculadoraCoincidencia();

    private ObjetoPerdido perdido() {
        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setNombre("Mochila negra");
        objeto.setDescripcion("Tira azul");
        objeto.setCategoria(CategoriaObjeto.ACCESORIOS);
        objeto.setUbicacion(UbicacionCampus.A2);
        objeto.setImagenUrl("/uploads/perdido.jpg");
        objeto.setTipoVisual("Mochila");
        objeto.setColorVisual("Negro");
        objeto.setConfianzaVisual(.8);
        return objeto;
    }

    private ObjetoEncontrado encontrado() {
        ObjetoEncontrado objeto = new ObjetoEncontrado();
        objeto.setNombre("Bolso rojo");
        objeto.setDescripcion("Cierre dorado");
        objeto.setCategoria(CategoriaObjeto.ACCESORIOS);
        objeto.setUbicacion(UbicacionCampus.A2);
        objeto.setImagenUrl("/uploads/encontrado.jpg");
        objeto.setTipoVisual("Mochila");
        objeto.setColorVisual("Negro");
        objeto.setConfianzaVisual(.7);
        return objeto;
    }

    @Test
    void conservaLaFormulaBaseYAcotaElResultado() {
        ObjetoPerdido perdido = perdido();
        ObjetoEncontrado encontrado = encontrado();
        assertEquals(50, calculadora.calcularBase(perdido, encontrado));
        assertEquals(70, calculadora.calcular(perdido, encontrado));
        encontrado.setNombre(perdido.getNombre());
        encontrado.setDescripcion(perdido.getDescripcion());
        assertEquals(100, calculadora.calcularBase(perdido, encontrado));
        assertEquals(100, calculadora.calcular(perdido, encontrado));
    }

    @Test
    void tiposYColoresDiscordantesReducenLaSimilitud() {
        ObjetoEncontrado encontrado = encontrado();
        encontrado.setTipoVisual("Bolso");
        encontrado.setColorVisual("Rojo");
        assertEquals(25, calculadora.calcular(perdido(), encontrado));
        encontrado.setCategoria(CategoriaObjeto.BOTELLAS);
        encontrado.setUbicacion(UbicacionCampus.BIBLIOTECA);
        assertEquals(0, calculadora.calcular(perdido(), encontrado));
    }

    @Test
    void faltaDeFotoEnUnoOEnAmbosConservaExactamenteLaBase() {
        ObjetoPerdido perdido = perdido();
        ObjetoEncontrado encontrado = encontrado();
        encontrado.setImagenUrl(null);
        assertEquals(50, calculadora.calcular(perdido, encontrado));
        perdido.setImagenUrl(null);
        assertEquals(50, calculadora.calcular(perdido, encontrado));
    }

    @Test
    void deteccionInciertaOInvalidaNoInterviene() {
        ObjetoEncontrado encontrado = encontrado();
        encontrado.setConfianzaVisual(.349);
        assertEquals(50, calculadora.calcular(perdido(), encontrado));
        encontrado.setConfianzaVisual(Double.NaN);
        assertEquals(50, calculadora.calcular(perdido(), encontrado));
        encontrado.setConfianzaVisual(.8);
        encontrado.setTipoVisual("Tipo inventado");
        assertEquals(50, calculadora.calcular(perdido(), encontrado));
    }

    @Test
    void colorAusenteNoSeInterpretaComoDesacuerdo() {
        ObjetoEncontrado encontrado = encontrado();
        encontrado.setColorVisual(null);
        assertEquals(65, calculadora.calcular(perdido(), encontrado));
        encontrado.setColorVisual(" ");
        assertEquals(65, calculadora.calcular(perdido(), encontrado));
    }
}
