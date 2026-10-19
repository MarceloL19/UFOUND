package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeteccionVisualServiceTest {

    @Test
    void calculaIoUDeCajasIdenticas() {
        var caja = new DeteccionVisualService.Deteccion(
            63, 0.90f, 0, 0, 100, 100
        );

        assertEquals(
            1.0f,
            DeteccionVisualService.calcularIoU(caja, caja),
            0.0001f
        );
    }

    @Test
    void eliminaDeteccionesDuplicadasDeLaMismaClase() {
        var primera = new DeteccionVisualService.Deteccion(
            63, 0.90f, 0, 0, 100, 100
        );

        var duplicada = new DeteccionVisualService.Deteccion(
            63, 0.80f, 20, 20, 120, 120
        );

        var independiente = new DeteccionVisualService.Deteccion(
            63, 0.70f, 200, 200, 300, 300
        );

        var resultados = DeteccionVisualService.aplicarNms(
            List.of(primera, duplicada, independiente),
            0.45f
        );

        assertEquals(2, resultados.size());
        assertTrue(resultados.contains(primera));
        assertTrue(resultados.contains(independiente));
    }

    @Test
    void conservaDeteccionesDeDiferentesClases() {
        var laptop = new DeteccionVisualService.Deteccion(
            63, 0.90f, 0, 0, 100, 100
        );

        var mochila = new DeteccionVisualService.Deteccion(
            24, 0.85f, 0, 0, 100, 100
        );

        var resultados = DeteccionVisualService.aplicarNms(
            List.of(laptop, mochila),
            0.45f
        );

        assertEquals(2, resultados.size());
    }

    @Test
    void identificaSuperficieNegra() {
        var imagen = crearImagenColor(100, 100, 0x070608);

        var deteccion = new DeteccionVisualService.Deteccion(
            63, 0.90f, 0, 0, 100, 100
        );

        assertEquals(
            "NEGRO / MUY OSCURO",
            DeteccionVisualService.estimarColor(
                imagen, deteccion
            )
        );
    }

    @Test
    void identificaSuperficieBlanca() {
        var imagen = crearImagenColor(100, 100, 0xFFFFFF);

        var deteccion = new DeteccionVisualService.Deteccion(
            63, 0.90f, 0, 0, 100, 100
        );

        assertEquals(
            "BLANCO / MUY CLARO",
            DeteccionVisualService.estimarColor(
                imagen, deteccion
            )
        );
    }

    @Test
    void noInventaColorCuandoNoPuedeClasificarlo() {
        var imagen = crearImagenColor(100, 100, 0xFF0000);

        var deteccion = new DeteccionVisualService.Deteccion(
            63, 0.90f, 0, 0, 100, 100
        );

        assertEquals(
            "NO DETERMINADO",
            DeteccionVisualService.estimarColor(
                imagen, deteccion
            )
        );
    }

    private BufferedImage crearImagenColor(
            int ancho, int alto, int rgb) {

        var imagen = new BufferedImage(
            ancho, alto, BufferedImage.TYPE_INT_RGB
        );

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                imagen.setRGB(x, y, rgb);
            }
        }

        return imagen;
    }
}
