
package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Locale;

@Service
public class ImagenAnalisisService {

    private static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
    private static final long PIXELES_MAXIMOS = 20_000_000L;

    public ResultadoImagen validarImagen(MultipartFile imagen) {

        // 1. Comprobar que exista un archivo.
        if (imagen == null || imagen.isEmpty()) {
            throw new RegistroObjetoException(
                "Debes seleccionar una imagen."
            );
        }

        // 2. Comprobar el tamaño antes de leer su contenido.
        if (imagen.getSize() > TAMANO_MAXIMO) {
            throw new RegistroObjetoException(
                "La imagen no debe superar los 5 MB."
            );
        }

        byte[] datos;

        try {
            datos = imagen.getBytes();
        } catch (IOException exception) {
            throw new RegistroObjetoException(
                "No se pudo leer la imagen."
            );
        }

        if (datos.length == 0) {
            throw new RegistroObjetoException(
                "Debes seleccionar una imagen."
            );
        }

        if (datos.length > TAMANO_MAXIMO) {
            throw new RegistroObjetoException(
                "La imagen no debe superar los 5 MB."
            );
        }

        // 3. Identificar el formato mediante la firma binaria.
        String formato = detectarFormato(datos);

        String nombre = imagen.getOriginalFilename() == null
            ? ""
            : imagen.getOriginalFilename()
                    .toLowerCase(Locale.ROOT);

        boolean extensionValida = switch (formato) {
            case "PNG" -> nombre.endsWith(".png");

            case "JPEG" -> nombre.endsWith(".jpg")
                        || nombre.endsWith(".jpeg");

            case "WEBP" -> nombre.endsWith(".webp");

            default -> false;
        };

        if (!extensionValida) {
            throw new RegistroObjetoException(
                "Archivo inválido. Utiliza PNG, JPG o WEBP."
            );
        }

        // 4. Comprobar marcadores de terminación y estructura.
        validarEstructura(datos, formato);

        // 5. Decodificar la imagen y comprobar sus dimensiones.
        validarDecodificacion(datos, formato);

        // 6. Devolver únicamente el resultado de validación.
        return new ResultadoImagen(
            "LISTA_PARA_ANALISIS",
            formato,
            datos.length
        );
    }

    private String detectarFormato(byte[] datos) {

        // PNG: firma de ocho bytes.
        if (datos.length >= 8
                && (datos[0] & 0xFF) == 0x89
                && datos[1] == 0x50
                && datos[2] == 0x4E
                && datos[3] == 0x47
                && datos[4] == 0x0D
                && datos[5] == 0x0A
                && datos[6] == 0x1A
                && datos[7] == 0x0A) {
            return "PNG";
        }

        // JPEG: marcador SOI.
        if (datos.length >= 3
                && (datos[0] & 0xFF) == 0xFF
                && (datos[1] & 0xFF) == 0xD8
                && (datos[2] & 0xFF) == 0xFF) {
            return "JPEG";
        }

        // WebP: contenedor RIFF con identificador WEBP.
        if (datos.length >= 12
                && asciiCoincide(datos, 0, "RIFF")
                && asciiCoincide(datos, 8, "WEBP")) {
            return "WEBP";
        }

        return "DESCONOCIDO";
    }

    private boolean asciiCoincide(
            byte[] datos,
            int posicion,
            String texto) {

        if (datos.length < posicion + texto.length()) {
            return false;
        }

        for (int i = 0; i < texto.length(); i++) {
            if ((datos[posicion + i] & 0xFF)
                    != texto.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private void validarEstructura(
            byte[] datos,
            String formato) {

        boolean valido = false;

        switch (formato) {

            case "PNG" -> {
                byte[] fin = {
                    0, 0, 0, 0,
                    0x49, 0x45, 0x4E, 0x44,
                    (byte) 0xAE, 0x42,
                    0x60, (byte) 0x82
                };

                if (datos.length >= 20) {
                    valido = true;

                    for (int i = 0; i < fin.length; i++) {
                        if (datos[datos.length - fin.length + i]
                                != fin[i]) {
                            valido = false;
                            break;
                        }
                    }
                }
            }

            case "JPEG" -> {
                valido = datos.length > 4
                    && (datos[datos.length - 2] & 0xFF) == 0xFF
                    && (datos[datos.length - 1] & 0xFF) == 0xD9;
            }

            case "WEBP" -> {
                if (datos.length >= 20) {
                    long declarado = Integer.toUnsignedLong(
                        ByteBuffer.wrap(datos, 4, 4)
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .getInt()
                    );

                    valido = declarado == datos.length - 8L;
                }
            }

            default -> valido = false;
        }

        if (!valido) {
            throw new RegistroObjetoException(
                "La imagen está dañada o incompleta."
            );
        }
    }

    private void validarDecodificacion(
            byte[] datos,
            String formatoEsperado) {

        try (
            ImageInputStream flujo =
                new MemoryCacheImageInputStream(
                    new ByteArrayInputStream(datos)
                )
        ) {
            Iterator<ImageReader> lectores =
                ImageIO.getImageReaders(flujo);

            if (!lectores.hasNext()) {
                throw new RegistroObjetoException(
                    "No se pudo decodificar la imagen."
                );
            }

            ImageReader lector = lectores.next();

            try {
                lector.setInput(flujo, true, true);

                String formatoReal = lector.getFormatName()
                    .toUpperCase(Locale.ROOT);

                boolean coincide = formatoReal.equals(
                    formatoEsperado
                );

                if (!coincide) {
                    throw new RegistroObjetoException(
                        "El contenido no coincide con el formato."
                    );
                }

                int ancho = lector.getWidth(0);
                int alto = lector.getHeight(0);

                long pixeles = (long) ancho * alto;

                if (ancho <= 0 || alto <= 0
                        || pixeles > PIXELES_MAXIMOS) {
                    throw new RegistroObjetoException(
                        "La imagen tiene dimensiones no permitidas."
                    );
                }

                // Intentar leer los píxeles de la imagen.
                BufferedImage imagenDecodificada =
                    lector.read(0);

                if (imagenDecodificada == null) {
                    throw new RegistroObjetoException(
                        "No se pudo decodificar la imagen."
                    );
                }

            } finally {
                lector.dispose();
            }

        } catch (RegistroObjetoException exception) {
            throw exception;

        } catch (IOException | RuntimeException exception) {
            throw new RegistroObjetoException(
                "La imagen está dañada o incompleta."
            );
        }
    }

    public record ResultadoImagen(
        String estado,
        String formato,
        long tamanoBytes
    ) {}
}