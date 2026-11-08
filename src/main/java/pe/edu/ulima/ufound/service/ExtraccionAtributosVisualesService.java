package pe.edu.ulima.ufound.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import pe.edu.ulima.ufound.model.AtributosVisuales;

@Service
public class ExtraccionAtributosVisualesService {

    private static final Logger LOG = LoggerFactory.getLogger(ExtraccionAtributosVisualesService.class);

    private final ImagenAnalisisService imagenAnalisisService;
    private final DeteccionVisualService deteccionVisualService;

    public ExtraccionAtributosVisualesService(
            ImagenAnalisisService imagenAnalisisService,
            DeteccionVisualService deteccionVisualService) {

        this.imagenAnalisisService = imagenAnalisisService;
        this.deteccionVisualService = deteccionVisualService;
    }

    public AtributosVisuales extraer(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            return null;
        }

        // Un archivo inválido debe rechazarse antes de registrar.
        ImagenAnalisisService.ResultadoImagen validada = imagenAnalisisService.validarImagen(imagen);

        try {
            DeteccionVisualService.Resultado resultado = deteccionVisualService.analizar(validada.datos());

            if (resultado == null
                    || !"SUGERENCIA".equals(resultado.estado())
                    || resultado.tipoProbable() == null
                    || resultado.tipoProbable().isBlank()
                    || !Double.isFinite(resultado.confianza())
                    || resultado.confianza() < 0.35
                    || resultado.confianza() > 1.0) {
                return null;
            }

            String color = resultado.colorProbable();

            return new AtributosVisuales(
                    resultado.tipoProbable().trim(),
                    color == null || color.isBlank() ? null : color.trim(),
                    resultado.confianza());

        } catch (Exception exception) {
            // El fallo del detector permite continuar con los datos manuales.
            LOG.warn(
                    "No se obtuvieron atributos visuales; "
                            + "el registro continuará con los datos del formulario.",
                    exception);

            return null;
        }
    }
}