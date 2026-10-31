package pe.edu.ulima.ufound.hu32;

import org.springframework.stereotype.Service;
import pe.edu.ulima.ufound.hu31.CategoriaSugerenciaService;
import pe.edu.ulima.ufound.model.CategoriaObjeto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * HU-32: convierte las características de HU-30 en valores para el formulario de registro.
 * Solo devuelve los campos que el análisis respalda; la ubicación nunca se infiere de la foto.
 */
@Service
public class AutocompletadoFormularioService {

    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_CATEGORIA = "categoria";
    public static final String CAMPO_DESCRIPCION = "descripcion";

    // Sustantivos femeninos entre los tipos que reconoce HU-30 (la laptop, la mochila...).
    private static final Set<String> TIPOS_FEMENINOS = Set.of("mochila", "maleta", "botella", "laptop");

    // Mismas etiquetas que produce EstimadorColor; cualquier otro valor se descarta.
    private static final Set<String> COLORES_VALIDOS = Set.of(
            "negro", "blanco", "gris", "rojo", "naranja", "amarillo",
            "verde", "celeste", "azul", "morado", "rosado", "marrón");

    private final CategoriaSugerenciaService categoriaSugerenciaService;

    public AutocompletadoFormularioService(CategoriaSugerenciaService categoriaSugerenciaService) {
        this.categoriaSugerenciaService = categoriaSugerenciaService;
    }

    public Map<String, String> sugerirCampos(String estado, String tipoProbable, String colorProbable) {
        // El tipo debe existir en el catálogo de HU-31; así no se copia texto arbitrario al formulario.
        Optional<CategoriaObjeto> categoria = categoriaSugerenciaService.sugerir(estado, tipoProbable);
        if (categoria.isEmpty()) {
            return Collections.emptyMap();
        }

        String tipo = capitalizar(tipoProbable.trim());
        String color = normalizarColor(colorProbable);

        Map<String, String> campos = new LinkedHashMap<>();
        campos.put(CAMPO_NOMBRE, color == null ? tipo : tipo + " " + concordar(color, tipo));
        campos.put(CAMPO_CATEGORIA, categoria.get().name());
        if (color != null) {
            // Sin color la descripción solo repetiría el título, así que se deja al usuario.
            campos.put(CAMPO_DESCRIPCION, tipo + " de color " + color + ".");
        }
        return campos;
    }

    private String normalizarColor(String color) {
        if (color == null) {
            return null;
        }
        String valor = color.trim().toLowerCase(Locale.ROOT);
        return COLORES_VALIDOS.contains(valor) ? valor : null;
    }

    private String concordar(String color, String tipo) {
        boolean femenino = TIPOS_FEMENINOS.contains(tipo.toLowerCase(Locale.ROOT));
        if (femenino && color.endsWith("o")) {
            return color.substring(0, color.length() - 1) + "a";
        }
        return color;
    }

    private String capitalizar(String texto) {
        String minusculas = texto.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(minusculas.charAt(0)) + minusculas.substring(1);
    }
}
