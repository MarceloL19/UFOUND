package pe.edu.ulima.ufound.service;

public class AdminKpi {

    private final String titulo;
    private final long valor;
    private final String detalle;
    private final String tipo;

    public AdminKpi(String titulo, long valor, String detalle, String tipo) {
        this.titulo = titulo;
        this.valor = valor;
        this.detalle = detalle;
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public long getValor() {
        return valor;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getTipo() {
        return tipo;
    }
}
