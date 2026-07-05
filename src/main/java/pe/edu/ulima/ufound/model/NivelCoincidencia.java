package pe.edu.ulima.ufound.model;

public enum NivelCoincidencia {
    BAJO("Nivel bajo"),
    MEDIO("Nivel medio"),
    ALTO("Nivel alto");

    private final String etiqueta;

    NivelCoincidencia(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
