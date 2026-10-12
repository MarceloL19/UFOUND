package pe.edu.ulima.ufound.analisis;

import java.time.LocalDate;

public class RangoPeriodo {

    private final String codigo;
    private final String nombre;
    private final LocalDate inicio;
    private final LocalDate fin;
    private final LocalDate limiteInferior;
    private final LocalDate inicioAnterior;
    private final LocalDate finAnterior;
    private final boolean comparable;
    private final String descripcion;
    private final String descripcionAnterior;

    public RangoPeriodo(String codigo, String nombre, LocalDate inicio, LocalDate fin, LocalDate limiteInferior,
                        LocalDate inicioAnterior, LocalDate finAnterior, boolean comparable,
                        String descripcion, String descripcionAnterior) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.inicio = inicio;
        this.fin = fin;
        this.limiteInferior = limiteInferior;
        this.inicioAnterior = inicioAnterior;
        this.finAnterior = finAnterior;
        this.comparable = comparable;
        this.descripcion = descripcion;
        this.descripcionAnterior = descripcionAnterior;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public LocalDate getLimiteInferior() {
        return limiteInferior;
    }

    public LocalDate getInicioAnterior() {
        return inicioAnterior;
    }

    public LocalDate getFinAnterior() {
        return finAnterior;
    }

    public boolean isComparable() {
        return comparable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcionAnterior() {
        return descripcionAnterior;
    }
}
