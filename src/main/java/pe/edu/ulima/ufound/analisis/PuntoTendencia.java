package pe.edu.ulima.ufound.analisis;

public class PuntoTendencia {

    private final String etiqueta;
    private final long total;
    private final int altura;

    public PuntoTendencia(String etiqueta, long total, int altura) {
        this.etiqueta = etiqueta;
        this.total = total;
        this.altura = altura;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public long getTotal() {
        return total;
    }

    public int getAltura() {
        return altura;
    }
}
