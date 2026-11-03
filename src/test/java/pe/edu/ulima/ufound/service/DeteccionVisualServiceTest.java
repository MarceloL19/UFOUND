package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class DeteccionVisualServiceTest {

    private byte[] foto(String nombre) throws Exception {
        try (InputStream archivo = getClass().getResourceAsStream("/static/fotos/" + nombre)) {
            assertNotNull(archivo, nombre);
            return archivo.readAllBytes();
        }
    }

    @Test
    void identificaLaptopRealDelRepositorioComoSugerencia() throws Exception {
        DeteccionVisualService servicio = new DeteccionVisualService();
        try {
            var resultado = servicio.analizar(foto("laptop-gris.jpg"));
            assertEquals("SUGERENCIA", resultado.estado());
            assertEquals("Laptop", resultado.tipoProbable());
            assertEquals("Gris", resultado.colorProbable());
            assertTrue(resultado.confianza() >= .35);
            assertTrue(resultado.requiereConfirmacion());
        } finally {
            servicio.cerrar();
        }
    }

    @Test
    void identificaBotellaRealDelRepositorioComoSugerencia() throws Exception {
        DeteccionVisualService servicio = new DeteccionVisualService();
        try {
            var resultado = servicio.analizar(foto("botella-verde.jpg"));
            assertEquals("SUGERENCIA", resultado.estado());
            assertEquals("Botella", resultado.tipoProbable());
            assertEquals("Verde", resultado.colorProbable());
            assertTrue(resultado.requiereConfirmacion());
        } finally {
            servicio.cerrar();
        }
    }

    @Test
    void imagenSinObjetoReconocibleNoInventaCaracteristicas() throws Exception {
        BufferedImage imagen = new BufferedImage(416, 416, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D grafico = imagen.createGraphics();
        try {
            grafico.setColor(java.awt.Color.WHITE);
            grafico.fillRect(0, 0, 416, 416);
        } finally {
            grafico.dispose();
        }
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        assertTrue(ImageIO.write(imagen, "png", salida));

        DeteccionVisualService servicio = new DeteccionVisualService();
        try {
            var resultado = servicio.analizar(salida.toByteArray());
            assertEquals("SIN_DETECCION", resultado.estado());
            assertNull(resultado.tipoProbable());
            assertNull(resultado.colorProbable());
        } finally {
            servicio.cerrar();
        }
    }
}
