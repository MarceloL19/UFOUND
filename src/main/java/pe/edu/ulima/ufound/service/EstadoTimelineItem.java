package pe.edu.ulima.ufound.service;

import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.HistorialEstado;

public class EstadoTimelineItem {

    private final EstadoObjeto estado;
    private final HistorialEstado historial;
    private final boolean completado;
    private final boolean actual;

    public EstadoTimelineItem(EstadoObjeto estado, HistorialEstado historial, boolean completado, boolean actual) {
        this.estado = estado;
        this.historial = historial;
        this.completado = completado;
        this.actual = actual;
    }

    public EstadoObjeto getEstado() {
        return estado;
    }

    public HistorialEstado getHistorial() {
        return historial;
    }

    public boolean isCompletado() {
        return completado;
    }

    public boolean isActual() {
        return actual;
    }
}
