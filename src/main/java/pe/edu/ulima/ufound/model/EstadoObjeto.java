package pe.edu.ulima.ufound.model;

public enum EstadoObjeto {
    REPORTADO("Reportado", "El objeto fue registrado en el sistema"),
    EN_REVISION("En revision", "El equipo esta verificando la informacion del objeto"),
    COINCIDENCIA_DETECTADA("Coincidencia detectada", "Se encontro un posible objeto relacionado"),
    DISPONIBLE_OFICINA("Disponible en oficina", "El objeto esta disponible para recojo validado"),
    ENTREGADO("Entregado", "El objeto fue entregado al propietario verificado"),
    ARCHIVADO("Archivado", "El caso fue cerrado y registrado en el historial"),
    REGISTRADO("Reportado", "El objeto fue registrado en el sistema"),
    EN_CUSTODIA("En revision", "El equipo esta verificando la informacion del objeto"),
    RECUPERADO("Entregado", "El objeto fue entregado al propietario verificado");

    private final String etiqueta;
    private final String descripcion;

    EstadoObjeto(String etiqueta, String descripcion) {
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esGestionable() {
        return this != REGISTRADO && this != EN_CUSTODIA && this != RECUPERADO;
    }
}
