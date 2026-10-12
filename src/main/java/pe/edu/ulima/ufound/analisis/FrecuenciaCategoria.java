package pe.edu.ulima.ufound.analisis;

public class FrecuenciaCategoria {

    private final String etiqueta;
    private final long total;
    private final int porcentaje;
    private final int ancho;

    public FrecuenciaCategoria(String etiqueta, long total, int porcentaje, int ancho) {
        this.etiqueta = etiqueta;
        this.total = total;
        this.porcentaje = porcentaje;
        this.ancho = ancho;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public long getTotal() {
        return total;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public int getAncho() {
        return ancho;
    }
}
