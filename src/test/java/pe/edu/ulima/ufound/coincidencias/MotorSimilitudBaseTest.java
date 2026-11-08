package pe.edu.ulima.ufound.coincidencias;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MotorSimilitudBaseTest {

    private final MotorSimilitud motor = new MotorSimilitudBase();

    @Test
    void atributosIdenticosObtienenCienPuntos() {
        ObjetoPerdido perdido = perdido("Maleta negra", "Maleta con cierre");
        ObjetoEncontrado encontrado = encontrado("Maleta negra", "Maleta con cierre");

        assertEquals(100, motor.calcular(perdido, encontrado));
    }

    @Test
    void categoriaYUbicacionCoincidentesAportanCincuentaPuntos() {
        ObjetoPerdido perdido = perdido("Maleta", "Negra");
        ObjetoEncontrado encontrado = encontrado("Laptop", "Gris");

        assertEquals(50, motor.calcular(perdido, encontrado));
    }

    @Test
    void coincidenciaParcialDeNombreUsaProporcionYRedondeo() {
        ObjetoPerdido perdido = perdido("Maleta negra", null);
        ObjetoEncontrado encontrado = encontrado("Maleta roja", null);

        // Categoría 30 + ubicación 20 + nombre round(25 / 2).
        assertEquals(63, motor.calcular(perdido, encontrado));
    }

    @Test
    void textosVaciosNoAportanPuntos() {
        ObjetoPerdido perdido = perdido(null, " ");
        ObjetoEncontrado encontrado = encontrado("", null);

        assertEquals(50, motor.calcular(perdido, encontrado));
    }

    @Test
    void textoIgnoraMayusculasYPuntuacion() {
        ObjetoPerdido perdido = perdido("MALETA NEGRA!", "CIERRE METALICO.");
        ObjetoEncontrado encontrado = encontrado("maleta negra", "cierre metalico");

        assertEquals(100, motor.calcular(perdido, encontrado));
    }

    private ObjetoPerdido perdido(String nombre, String descripcion) {
        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setNombre(nombre);
        objeto.setDescripcion(descripcion);
        objeto.setCategoria(CategoriaObjeto.values()[0]);
        objeto.setUbicacion(UbicacionCampus.values()[0]);
        return objeto;
    }

    private ObjetoEncontrado encontrado(
            String nombre,
            String descripcion) {

        ObjetoEncontrado objeto = new ObjetoEncontrado();
        objeto.setNombre(nombre);
        objeto.setDescripcion(descripcion);
        objeto.setCategoria(CategoriaObjeto.values()[0]);
        objeto.setUbicacion(UbicacionCampus.values()[0]);
        return objeto;
    }
}