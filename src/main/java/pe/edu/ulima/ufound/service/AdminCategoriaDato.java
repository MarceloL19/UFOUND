package pe.edu.ulima.ufound.service;

public class AdminCategoriaDato {

    private final String categoria;
    private final long total;
    private final int porcentaje;
    private final String color;

    public AdminCategoriaDato(String categoria, long total, int porcentaje, String color) {
        this.categoria = categoria;
        this.total = total;
        this.porcentaje = porcentaje;
        this.color = color;
    }

    public String getCategoria() {
        return categoria;
    }

    public long getTotal() {
        return total;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public String getColor() {
        return color;
    }
}
