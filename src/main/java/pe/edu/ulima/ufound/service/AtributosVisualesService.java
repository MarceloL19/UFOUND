package pe.edu.ulima.ufound.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class AtributosVisualesService {

    public static final double CONFIANZA_MINIMA = 0.35;
    private static final Logger LOGGER = LoggerFactory.getLogger(AtributosVisualesService.class);
    private static final Set<String> TIPOS = Set.of(
            "mochila", "bolso", "maleta", "botella", "laptop", "mouse", "celular", "libro");

    private final DeteccionVisualService detector;

    public AtributosVisualesService(DeteccionVisualService detector) {
        this.detector = detector;
    }

    public Optional<AtributosVisuales> extraer(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            return Optional.empty();
        }
        try {
            DeteccionVisualService.Resultado resultado = detector.analizar(imagen.getBytes());
            if (!"SUGERENCIA".equals(resultado.estado())
                    || resultado.tipoProbable() == null
                    || !TIPOS.contains(resultado.tipoProbable().trim().toLowerCase(Locale.ROOT))
                    || !Double.isFinite(resultado.confianza())
                    || resultado.confianza() < CONFIANZA_MINIMA
                    || resultado.confianza() > 1) {
                return Optional.empty();
            }
            return Optional.of(new AtributosVisuales(
                    resultado.tipoProbable().trim(), resultado.colorProbable(), resultado.confianza()));
        } catch (Exception exception) {
            // Una falla del detector no debe impedir el registro manual.
            LOGGER.warn("No se obtuvieron atributos visuales para el registro: {}", exception.toString());
            return Optional.empty();
        }
    }

    public record AtributosVisuales(String tipo, String color, double confianza) {}
}
