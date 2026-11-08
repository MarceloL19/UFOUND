package pe.edu.ulima.ufound.coincidencias;

import org.springframework.stereotype.Service;

import pe.edu.ulima.ufound.model.AtributosVisuales;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;

@Service
public class MotorSimilitudVisual implements MotorSimilitud {

    private final MotorSimilitudBase motorBase = new MotorSimilitudBase();

    @Override
    public int calcular(
            ObjetoPerdido perdido,
            ObjetoEncontrado encontrado) {

        int base = motorBase.calcular(perdido, encontrado);

        AtributosVisuales origen = perdido.getAtributosVisuales();
        AtributosVisuales destino = encontrado.getAtributosVisuales();

        if (!esValido(origen) || !esValido(destino)) {
            return base;
        }

        int tipo = coinciden(origen.getTipo(), destino.getTipo())
                ? 100
                : 0;

        boolean compararColor = tieneTexto(origen.getColor())
                && tieneTexto(destino.getColor());

        if (!compararColor) {
            return (int) Math.round(base * 0.80 + tipo * 0.20);
        }

        int color = coinciden(origen.getColor(), destino.getColor())
                ? 100
                : 0;

        return (int) Math.round(
                base * 0.70 + tipo * 0.20 + color * 0.10);
    }

    private boolean esValido(AtributosVisuales atributos) {
        if (atributos == null || !tieneTexto(atributos.getTipo())) {
            return false;
        }

        Double confianza = atributos.getConfianza();

        return confianza != null
                && Double.isFinite(confianza)
                && confianza >= 0.35
                && confianza <= 1.0;
    }

    private boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }

    private boolean coinciden(String origen, String destino) {
        return origen.trim().equalsIgnoreCase(destino.trim());
    }
}