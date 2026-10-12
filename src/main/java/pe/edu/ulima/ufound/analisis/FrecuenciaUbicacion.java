package pe.edu.ulima.ufound.analisis;

public class FrecuenciaUbicacion {

    private final String etiqueta;
    private final long total;
    private final long totalAnterior;
    private final long variacion;
    private final int porcentaje;
    private final int ancho;

    public FrecuenciaUbicacion(String etiqueta, long total, long totalAnterior, int porcentaje, int ancho) {
        this.etiqueta = etiqueta;
        this.total = total;
        this.totalAnterior = totalAnterior;
        this.variacion = total - totalAnterior;
        this.porcentaje = porcentaje;
        this.ancho = ancho;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public long getTotal() {
        return total;
    }

    public long getTotalAnterior() {
        return totalAnterior;
    }

    public long getVariacion() {
        return variacion;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public int getAncho() {
        return ancho;
    }

    public String getVariacionTexto() {
        if (variacion > 0) {
            return "+" + variacion;
        }
        return Long.toString(variacion);
    }

    public String getVariacionClase() {
        if (variacion > 0) {
            return "sube";
        }
        if (variacion < 0) {
            return "baja";
        }
        return "igual";
    }
}
