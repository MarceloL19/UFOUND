package pe.edu.ulima.ufound.service;

public class AdminMesDato {

    private final String mes;
    private final long perdidos;
    private final long encontrados;
    private final int alturaPerdidos;
    private final int alturaEncontrados;

    public AdminMesDato(String mes, long perdidos, long encontrados, long maximo) {
        this.mes = mes;
        this.perdidos = perdidos;
        this.encontrados = encontrados;
        long base = Math.max(maximo, 1);
        this.alturaPerdidos = (int) Math.max(8, Math.round((perdidos * 160.0) / base));
        this.alturaEncontrados = (int) Math.max(8, Math.round((encontrados * 160.0) / base));
    }

    public String getMes() {
        return mes;
    }

    public long getPerdidos() {
        return perdidos;
    }

    public long getEncontrados() {
        return encontrados;
    }

    public int getAlturaPerdidos() {
        return alturaPerdidos;
    }

    public int getAlturaEncontrados() {
        return alturaEncontrados;
    }
}
