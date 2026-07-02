package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadService {

    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of(".png", ".jpg", ".jpeg", ".webp");
    private final Path carpetaObjetosPerdidos = Path.of("uploads", "objetos-perdidos");
    private final Path carpetaObjetosEncontrados = Path.of("uploads", "objetos-encontrados");

    public String guardarImagenObjetoPerdido(MultipartFile imagen) {
        return guardarImagen(imagen, carpetaObjetosPerdidos, "/uploads/objetos-perdidos/");
    }

    public String guardarImagenObjetoEncontrado(MultipartFile imagen) {
        return guardarImagen(imagen, carpetaObjetosEncontrados, "/uploads/objetos-encontrados/");
    }

    private String guardarImagen(MultipartFile imagen, Path carpeta, String rutaPublica) {
        if (imagen == null || imagen.isEmpty()) {
            return null;
        }

        String nombreOriginal = imagen.getOriginalFilename() == null ? "" : imagen.getOriginalFilename();
        String extension = obtenerExtension(nombreOriginal);
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new RegistroObjetoException("La imagen debe ser PNG, JPG, JPEG o WEBP.");
        }

        try {
            Files.createDirectories(carpeta);
            String nombreArchivo = UUID.randomUUID() + extension;
            Path destino = carpeta.resolve(nombreArchivo);
            Files.copy(imagen.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return rutaPublica + nombreArchivo;
        } catch (IOException exception) {
            throw new RegistroObjetoException("No se pudo guardar la imagen del objeto.");
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        int punto = nombreArchivo.lastIndexOf('.');
        if (punto < 0) {
            return "";
        }
        return nombreArchivo.substring(punto).toLowerCase();
    }
}

