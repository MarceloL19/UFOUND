package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Component;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CalculadoraCoincidencia {

    private static final Set<String> TIPOS = Set.of(
            "mochila", "bolso", "maleta", "botella", "laptop", "mouse", "celular", "libro");

    public int calcularBase(ObjetoPerdido perdido, ObjetoEncontrado encontrado) {
        int puntaje = 0;
        if (perdido.getCategoria() == encontrado.getCategoria()) {
            puntaje += 30;
        }
        if (perdido.getUbicacion() == encontrado.getUbicacion()) {
            puntaje += 20;
        }
        puntaje += similitudTexto(perdido.getNombre(), encontrado.getNombre(), 25);
        puntaje += similitudTexto(perdido.getDescripcion(), encontrado.getDescripcion(), 25);
        return Math.min(100, puntaje);
    }

    public int calcular(ObjetoPerdido perdido, ObjetoEncontrado encontrado) {
        int base = calcularBase(perdido, encontrado);
        if (!visualValido(perdido.getImagenUrl(), perdido.getTipoVisual(), perdido.getConfianzaVisual())
                || !visualValido(encontrado.getImagenUrl(), encontrado.getTipoVisual(),
                        encontrado.getConfianzaVisual())) {
            return base;
        }
        int ajuste = normalizar(perdido.getTipoVisual()).equals(normalizar(encontrado.getTipoVisual()))
                ? 15 : -20;
        if (perdido.getColorVisual() != null && !perdido.getColorVisual().isBlank()
                && encontrado.getColorVisual() != null && !encontrado.getColorVisual().isBlank()) {
            ajuste += normalizar(perdido.getColorVisual()).equals(normalizar(encontrado.getColorVisual()))
                    ? 5 : -5;
        }
        return Math.max(0, Math.min(100, base + ajuste));
    }

    private boolean visualValido(String imagenUrl, String tipo, Double confianza) {
        return imagenUrl != null && !imagenUrl.isBlank()
                && tipo != null && TIPOS.contains(normalizar(tipo))
                && confianza != null && Double.isFinite(confianza)
                && confianza >= AtributosVisualesService.CONFIANZA_MINIMA && confianza <= 1;
    }

    private String normalizar(String valor) {
        return Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
    }

    private int similitudTexto(String origen, String destino, int maximo) {
        Set<String> palabrasOrigen = extraerPalabras(origen);
        Set<String> palabrasDestino = extraerPalabras(destino);
        if (palabrasOrigen.isEmpty() || palabrasDestino.isEmpty()) {
            return 0;
        }
        Set<String> interseccion = new HashSet<>(palabrasOrigen);
        interseccion.retainAll(palabrasDestino);
        double proporcion = (double) interseccion.size() / Math.max(palabrasOrigen.size(), palabrasDestino.size());
        return (int) Math.round(proporcion * maximo);
    }

    private Set<String> extraerPalabras(String texto) {
        if (texto == null || texto.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(texto.toLowerCase(Locale.ROOT).split("[^a-z0-9áéíóúñ]+"))
                .filter(palabra -> palabra.length() > 2)
                .collect(Collectors.toSet());
    }
}
