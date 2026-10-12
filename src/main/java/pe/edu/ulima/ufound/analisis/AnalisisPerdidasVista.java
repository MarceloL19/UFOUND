package pe.edu.ulima.ufound.analisis;

import java.util.List;

public class AnalisisPerdidasVista {

    private final RangoPeriodo periodo;
    private final List<FrecuenciaCategoria> categorias;
    private final List<FrecuenciaUbicacion> ubicaciones;
    private final List<PuntoTendencia> tendencia;
    private final long totalPeriodo;
    private final long totalPeriodoAnterior;
    private final String categoriaLider;
    private final String ubicacionLider;
    private final String mensaje;

    public AnalisisPerdidasVista(RangoPeriodo periodo, List<FrecuenciaCategoria> categorias,
                                 List<FrecuenciaUbicacion> ubicaciones, List<PuntoTendencia> tendencia,
                                 long totalPeriodo, long totalPeriodoAnterior, String categoriaLider,
                                 String ubicacionLider, String mensaje) {
        this.periodo = periodo;
        this.categorias = categorias;
        this.ubicaciones = ubicaciones;
        this.tendencia = tendencia;
        this.totalPeriodo = totalPeriodo;
        this.totalPeriodoAnterior = totalPeriodoAnterior;
        this.categoriaLider = categoriaLider;
        this.ubicacionLider = ubicacionLider;
        this.mensaje = mensaje;
    }

    public RangoPeriodo getPeriodo() {
        return periodo;
    }

    public List<FrecuenciaCategoria> getCategorias() {
        return categorias;
    }

    public List<FrecuenciaUbicacion> getUbicaciones() {
        return ubicaciones;
    }

    public List<PuntoTendencia> getTendencia() {
        return tendencia;
    }

    public long getTotalPeriodo() {
        return totalPeriodo;
    }

    public long getTotalPeriodoAnterior() {
        return totalPeriodoAnterior;
    }

    public long getVariacion() {
        return totalPeriodo - totalPeriodoAnterior;
    }

    public String getVariacionTexto() {
        long variacion = getVariacion();
        if (variacion > 0) {
            return "+" + variacion;
        }
        return Long.toString(variacion);
    }

    public String getCategoriaLider() {
        return categoriaLider;
    }

    public String getUbicacionLider() {
        return ubicacionLider;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isSinDatos() {
        return totalPeriodo == 0;
    }
}
