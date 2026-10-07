package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UploadServiceTest {
    @TempDir
    Path carpeta;

    @Test
    void guardaLaFotoEnDirectorioConfiguradoYDevuelveLaRutaPublicaExistente() throws Exception {
        byte[] contenido = new byte[]{1, 2, 3};
        UploadService service = new UploadService(carpeta.toString());
        MockMultipartFile foto = new MockMultipartFile("imagen", "objeto.png", "image/png", contenido);

        String url = service.guardarImagenObjetoPerdido(foto);

        assertTrue(url.startsWith("/uploads/objetos-perdidos/"));
        assertArrayEquals(contenido, Files.readAllBytes(carpeta.resolve(url.substring("/uploads/".length()))));
    }
}
