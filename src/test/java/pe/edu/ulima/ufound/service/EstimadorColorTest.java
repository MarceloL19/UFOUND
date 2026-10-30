package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class EstimadorColorTest {
    private String colorDe(Color color) {
        BufferedImage imagen = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < 40; y++) {
            for (int x = 0; x < 40; x++) {
                imagen.setRGB(x, y, color.getRGB());
            }
        }
        return EstimadorColor.estimar(imagen, 0, 0, 40, 40);
    }

    @Test
    void distingueColoresBasicosEnLaRegionDetectada() {
        assertEquals("Rojo", colorDe(Color.RED));
        assertEquals("Verde", colorDe(Color.GREEN));
        assertEquals("Negro", colorDe(Color.BLACK));
        assertEquals("Gris", colorDe(Color.GRAY));
    }

    @Test
    void noInventaColorCuandoLaCajaEstaFueraDeLaImagen() {
        BufferedImage imagen = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        assertNull(EstimadorColor.estimar(imagen, 20, 20, 30, 30));
    }
}
