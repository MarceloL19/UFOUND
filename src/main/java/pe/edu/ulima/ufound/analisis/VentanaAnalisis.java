package pe.edu.ulima.ufound.analisis;

public enum VentanaAnalisis {
    ULTIMOS_7_DIAS("7", "Ultimos 7 dias"),
    ULTIMOS_30_DIAS("30", "Ultimos 30 dias"),
    ULTIMOS_90_DIAS("90", "Ultimos 90 dias"),
    MES_ACTUAL("mes", "Mes actual"),
    HISTORICO("historico", "Historico completo"),
    PERSONALIZADO("personalizado", "Periodo personalizado");

    private final String codigo;
    private final String nombre;

    VentanaAnalisis(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public static VentanaAnalisis desdeCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return ULTIMOS_30_DIAS;
        }
        for (VentanaAnalisis ventana : values()) {
            if (ventana.codigo.equalsIgnoreCase(codigo.trim())) {
                return ventana;
            }
        }
        return ULTIMOS_30_DIAS;
    }
}
