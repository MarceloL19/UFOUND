package pe.edu.ulima.ufound.model;

public enum TipoObjeto {
    PERDIDO("Objeto perdido"),
    ENCONTRADO("Objeto encontrado");

    private final String etiqueta;

    TipoObjeto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
