package pe.edu.ulima.ufound.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

/** Estima un color visible dentro de la caja detectada; no modifica datos del formulario. */
final class EstimadorColor {

    private enum Tono {
        NEGRO("Negro"), BLANCO("Blanco"), GRIS("Gris"),
        ROJO("Rojo"), NARANJA("Naranja"), AMARILLO("Amarillo"),
        VERDE("Verde"), CELESTE("Celeste"), AZUL("Azul"),
        MORADO("Morado"), ROSADO("Rosado"), MARRON("Marrón");

        private final String etiqueta;
        Tono(String etiqueta) { this.etiqueta = etiqueta; }
    }

    private EstimadorColor() {}

    static String estimar(BufferedImage imagen, float izquierda, float arriba,
                          float derecha, float abajo) {
        int x1 = limitar(Math.round(izquierda), 0, imagen.getWidth());
        int y1 = limitar(Math.round(arriba), 0, imagen.getHeight());
        int x2 = limitar(Math.round(derecha), 0, imagen.getWidth());
        int y2 = limitar(Math.round(abajo), 0, imagen.getHeight());
        int margenX = Math.round((x2 - x1) * .08f);
        int margenY = Math.round((y2 - y1) * .08f);
        x1 += margenX;
        x2 -= margenX;
        y1 += margenY;
        y2 -= margenY;
        if (x2 <= x1 || y2 <= y1) {
            return null;
        }

        int paso = Math.max(1, (int) Math.sqrt((double) (x2 - x1)
                * (y2 - y1) / 4096));
        Map<Tono, Integer> frecuencias = new EnumMap<>(Tono.class);
        for (int y = y1; y < y2; y += paso) {
            for (int x = x1; x < x2; x += paso) {
                Tono tono = clasificar(imagen.getRGB(x, y));
                frecuencias.merge(tono, 1, Integer::sum);
            }
        }
        return frecuencias.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entrada -> entrada.getKey().etiqueta)
                .orElse(null);
    }

    private static Tono clasificar(int rgb) {
        int rojo = (rgb >>> 16) & 0xFF;
        int verde = (rgb >>> 8) & 0xFF;
        int azul = rgb & 0xFF;
        float[] hsb = Color.RGBtoHSB(rojo, verde, azul, null);
        float matiz = hsb[0] * 360;
        float saturacion = hsb[1];
        float brillo = hsb[2];

        if (brillo < .18f) return Tono.NEGRO;
        if (saturacion < .20f) {
            if (brillo < .23f) return Tono.NEGRO;
            if (brillo > .84f) return Tono.BLANCO;
            return Tono.GRIS;
        }
        if (matiz < 15 || matiz >= 345) return Tono.ROJO;
        if (matiz < 45) return brillo < .60f ? Tono.MARRON : Tono.NARANJA;
        if (matiz < 70) return Tono.AMARILLO;
        if (matiz < 160) return Tono.VERDE;
        if (matiz < 200) return Tono.CELESTE;
        if (matiz < 260) return Tono.AZUL;
        if (matiz < 320) return Tono.MORADO;
        return Tono.ROSADO;
    }

    private static int limitar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }
}
