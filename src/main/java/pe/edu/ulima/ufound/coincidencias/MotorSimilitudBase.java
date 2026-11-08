package pe.edu.ulima.ufound.coincidencias;

import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class MotorSimilitudBase implements MotorSimilitud {

    @Override
    public int calcular(
            ObjetoPerdido perdido,
            ObjetoEncontrado encontrado) {

        int puntaje = 0;

        if (perdido.getCategoria() == encontrado.getCategoria()) {
            puntaje += 30;
        }

        if (perdido.getUbicacion() == encontrado.getUbicacion()) {
            puntaje += 20;
        }

        puntaje += similitudTexto(
                perdido.getNombre(),
                encontrado.getNombre(),
                25);

        puntaje += similitudTexto(
                perdido.getDescripcion(),
                encontrado.getDescripcion(),
                25);

        return Math.min(100, puntaje);
    }

    private int similitudTexto(
            String origen,
            String destino,
            int maximo) {

        Set<String> palabrasOrigen = extraerPalabras(origen);
        Set<String> palabrasDestino = extraerPalabras(destino);

        if (palabrasOrigen.isEmpty() || palabrasDestino.isEmpty()) {
            return 0;
        }

        Set<String> interseccion = new HashSet<>(palabrasOrigen);
        interseccion.retainAll(palabrasDestino);

        double proporcion = (double) interseccion.size()
                / Math.max(palabrasOrigen.size(), palabrasDestino.size());

        return (int) Math.round(proporcion * maximo);
    }

    private Set<String> extraerPalabras(String texto) {
        if (texto == null || texto.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(
                texto.toLowerCase(Locale.ROOT)
                        .split("[^a-z0-9áéíóúñ]+"))
                .filter(palabra -> palabra.length() > 2)
                .collect(Collectors.toSet());
    }
}