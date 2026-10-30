package pe.edu.ulima.ufound.hu31;

import org.springframework.stereotype.Service;
import pe.edu.ulima.ufound.model.CategoriaObjeto;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** HU-31: traduce una detección de HU-30 a una categoría REAL del catálogo. */
@Service
public class CategoriaSugerenciaService {

    private static final Map<String, CategoriaObjeto> CATALOGO_POR_TIPO = Map.of(
            "mochila", CategoriaObjeto.ACCESORIOS,
            "bolso", CategoriaObjeto.ACCESORIOS,
            "maleta", CategoriaObjeto.ACCESORIOS,
            "botella", CategoriaObjeto.BOTELLAS,
            "laptop", CategoriaObjeto.TECNOLOGIA,
            "mouse", CategoriaObjeto.TECNOLOGIA,
            "celular", CategoriaObjeto.TECNOLOGIA,
            "libro", CategoriaObjeto.UTILES);

    public Optional<CategoriaObjeto> sugerir(String estado, String tipoProbable) {
        if (!"SUGERENCIA".equals(estado) || tipoProbable == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(CATALOGO_POR_TIPO.get(normalizar(tipoProbable)));
    }

    private String normalizar(String tipo) {
        String sinTildes = Normalizer.normalize(tipo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return sinTildes.trim().toLowerCase(Locale.ROOT);
    }
}
