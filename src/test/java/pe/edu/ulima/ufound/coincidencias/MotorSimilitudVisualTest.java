package pe.edu.ulima.ufound.coincidencias;

import org.junit.jupiter.api.Test;

import pe.edu.ulima.ufound.model.AtributosVisuales;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MotorSimilitudVisualTest {

    private final MotorSimilitud motor = new MotorSimilitudVisual();

    @Test
    void sinAtributosVisualesConservaMotorBase() {
        assertEquals(50, calcular(null, null));
    }

    @Test
    void atributosEnUnSoloObjetoConservanMotorBase() {
        assertEquals(50, calcular(visual("Maleta", "Negro"), null));
        assertEquals(50, calcular(null, visual("Maleta", "Negro")));
    }

    @Test
    void tipoYColorCoincidentesAumentanElPuntaje() {
        // 50 * 0.70 + 100 * 0.20 + 100 * 0.10 = 65.
        assertEquals(65, calcular(
                visual("Maleta", "Negro"),
                visual("Maleta", "Negro")));
    }

    @Test
    void tipoYColorDistintosReducenElPuntaje() {
        assertEquals(35, calcular(
                visual("Maleta", "Negro"),
                visual("Laptop", "Gris")));
    }

    @Test
    void colorCoincidenteNoEquivaleATipoCoincidente() {
        assertEquals(45, calcular(
                visual("Maleta", "Negro"),
                visual("Laptop", "Negro")));
    }

    @Test
    void sinColorUsaSoloElTipoYElMotorBase() {
        assertEquals(60, calcular(
                visual("Maleta", null),
                visual("Maleta", "Negro")));

        assertEquals(60, calcular(
                visual("Maleta", "Negro"),
                visual("Maleta", null)));
    }

    @Test
    void confianzaInsuficienteConservaMotorBase() {
        AtributosVisuales bajaConfianza = new AtributosVisuales("Maleta", "Negro", 0.20);

        assertEquals(50, calcular(
                bajaConfianza,
                visual("Maleta", "Negro")));
    }

    private AtributosVisuales visual(String tipo, String color) {
        return new AtributosVisuales(tipo, color, 0.90);
    }

    private int calcular(
            AtributosVisuales visualPerdido,
            AtributosVisuales visualEncontrado) {

        ObjetoPerdido perdido = new ObjetoPerdido();
        perdido.setNombre("Maleta");
        perdido.setCategoria(CategoriaObjeto.values()[0]);
        perdido.setUbicacion(UbicacionCampus.values()[0]);
        perdido.setAtributosVisuales(visualPerdido);

        ObjetoEncontrado encontrado = new ObjetoEncontrado();
        encontrado.setNombre("Bolso");
        encontrado.setCategoria(CategoriaObjeto.values()[0]);
        encontrado.setUbicacion(UbicacionCampus.values()[0]);
        encontrado.setAtributosVisuales(visualEncontrado);

        // Categoría y ubicación aportan 50 puntos al motor base.
        // Los nombres no comparten palabras y no hay descripciones.
        return motor.calcular(perdido, encontrado);
    }
}