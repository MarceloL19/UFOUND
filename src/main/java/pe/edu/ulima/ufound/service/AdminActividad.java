package pe.edu.ulima.ufound.service;

import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.TipoObjeto;

import java.time.LocalDateTime;

public class AdminActividad {

    private final String codigo;
    private final String objeto;
    private final TipoObjeto tipoObjeto;
    private final EstadoObjeto estado;
    private final String responsable;
    private final LocalDateTime fechaHora;
    private final String imagenUrl;

    public AdminActividad(String codigo, String objeto, TipoObjeto tipoObjeto, EstadoObjeto estado,
                          String responsable, LocalDateTime fechaHora, String imagenUrl) {
        this.codigo = codigo;
        this.objeto = objeto;
        this.tipoObjeto = tipoObjeto;
        this.estado = estado;
        this.responsable = responsable;
        this.fechaHora = fechaHora;
        this.imagenUrl = imagenUrl;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getObjeto() {
        return objeto;
    }

    public TipoObjeto getTipoObjeto() {
        return tipoObjeto;
    }

    public EstadoObjeto getEstado() {
        return estado;
    }

    public String getResponsable() {
        return responsable;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }
}
