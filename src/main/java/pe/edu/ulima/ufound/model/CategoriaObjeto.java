package pe.edu.ulima.ufound.model;

public enum CategoriaObjeto {
    TECNOLOGIA("Tecnologia"),
    DOCUMENTOS("Documentos"),
    ACCESORIOS("Accesorios"),
    ROPA("Ropa"),
    UTILES("Utiles"),
    BOTELLAS("Botellas / tomatodos"),
    OTROS("Otros");

    private final String etiqueta;

    CategoriaObjeto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

