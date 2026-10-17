
package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ImagenAnalisisServiceTest {

    private ImagenAnalisisService servicio;

    @BeforeEach
    void configurar() {
        servicio = new ImagenAnalisisService();
    }

    private byte[] generarImagenReal(String formato)
            throws IOException {

        BufferedImage imagen = new BufferedImage(
            1, 1, BufferedImage.TYPE_INT_RGB
        );

        imagen.setRGB(0, 0, 0xFF7100);

        ByteArrayOutputStream salida =
            new ByteArrayOutputStream();

        assertTrue(ImageIO.write(imagen, formato, salida));

        return salida.toByteArray();
    }

    private byte[] webpReal() {
        return Base64.getDecoder().decode(
            "UklGRjwAAABXRUJQVlA4IDAAAAAQAgCdASoBAAEAAUAmJaACdLoB+AH4AAPIAP7gJX/3ebAGEv+L3/7nAWE0X+pAAAA="
        );
    }

    @Test
    void debeAceptarImagenPng() throws IOException {

        byte[] datos = generarImagenReal("png");

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "mochila.png", "image/png", datos
        );

        var resultado = servicio.validarImagen(imagen);

        assertEquals("LISTA_PARA_ANALISIS", resultado.estado());
        assertEquals("PNG", resultado.formato());
        assertEquals(datos.length, resultado.tamanoBytes());
    }

    @Test
    void debeAceptarImagenJpeg() throws IOException {

        byte[] datos = generarImagenReal("jpg");

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "objeto.jpg", "image/jpeg", datos
        );

        var resultado = servicio.validarImagen(imagen);

        assertEquals("JPEG", resultado.formato());
    }

    @Test
    void debeAceptarImagenWebp() {

        byte[] datos = webpReal();

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "objeto.webp", "image/webp", datos
        );

        var resultado = servicio.validarImagen(imagen);

        assertEquals("WEBP", resultado.formato());
    }

    @Test
    void debeRechazarArchivoVacio() {

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "vacio.png", "image/png", new byte[0]
        );

        assertThrows(
            RegistroObjetoException.class,
            () -> servicio.validarImagen(imagen)
        );
    }

    @Test
    void debeRechazarArchivoDisfrazadoDeImagen() {

        MockMultipartFile archivo = new MockMultipartFile(
            "imagen",
            "falso.png",
            "image/png",
            "Esto no es una imagen".getBytes(
                StandardCharsets.UTF_8
            )
        );

        assertThrows(
            RegistroObjetoException.class,
            () -> servicio.validarImagen(archivo)
        );
    }

    @Test
    void debeRechazarExtensionIncompatible()
            throws IOException {

        byte[] png = generarImagenReal("png");

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "archivo.jpg", "image/jpeg", png
        );

        assertThrows(
            RegistroObjetoException.class,
            () -> servicio.validarImagen(imagen)
        );
    }

    @Test
    void debeRechazarImagenMayorACincoMB() {

        byte[] datos = new byte[5 * 1024 * 1024 + 1];

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "grande.png", "image/png", datos
        );

        assertThrows(
            RegistroObjetoException.class,
            () -> servicio.validarImagen(imagen)
        );
    }

    @Test
    void debeRechazarPngConCabeceraPeroSinImagenCompleta() {

        byte[] pngIncompleto = {
            (byte) 0x89, 0x50, 0x4E, 0x47,
            0x0D, 0x0A, 0x1A, 0x0A
        };

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen",
            "imagen-incompleta.png",
            "image/png",
            pngIncompleto
        );

        assertThrows(
            RegistroObjetoException.class,
            () -> servicio.validarImagen(imagen)
        );
    }

    @Test
    void debeRechazarJpegTruncado() throws IOException {

        byte[] jpeg = generarImagenReal("jpg");

        byte[] truncado = Arrays.copyOf(
            jpeg, jpeg.length - 2
        );

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "truncado.jpg", "image/jpeg", truncado
        );

        assertThrows(
            RegistroObjetoException.class,
            () -> servicio.validarImagen(imagen)
        );
    }

    @Test
    void debeRechazarWebpTruncado() {

        byte[] webp = webpReal();

        byte[] truncado = Arrays.copyOf(
            webp, webp.length - 3
        );

        MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "truncado.webp", "image/webp", truncado
        );
        assertThrows(
            RegistroObjetoException.class,
            () -> servicio.validarImagen(imagen)
        );
    }
}
