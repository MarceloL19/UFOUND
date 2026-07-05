package pe.edu.ulima.ufound.service;

import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.model.UbicacionCampus;

import java.time.LocalDateTime;

public class EstadoObjetoResumen {

    private final TipoObjeto tipoObjeto;
    private final Long idObjeto;
    private final String nombre;
    private final CategoriaObjeto categoria;
    private final UbicacionCampus ubicacion;
    private final LocalDateTime fechaHora;
    private final String imagenUrl;
    private final EstadoObjeto estado;

    public EstadoObjetoResumen(TipoObjeto tipoObjeto, Long idObjeto, String nombre, CategoriaObjeto categoria,
                               UbicacionCampus ubicacion, LocalDateTime fechaHora, String imagenUrl,
                               EstadoObjeto estado) {
        this.tipoObjeto = tipoObjeto;
        this.idObjeto = idObjeto;
        this.nombre = nombre;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fechaHora = fechaHora;
        this.imagenUrl = imagenUrl;
        this.estado = estado;
    }

    public TipoObjeto getTipoObjeto() {
        return tipoObjeto;
    }

    public Long getIdObjeto() {
        return idObjeto;
    }

    public String getNombre() {
        return nombre;
    }

    public CategoriaObjeto getCategoria() {
        return categoria;
    }

    public UbicacionCampus getUbicacion() {
        return ubicacion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public EstadoObjeto getEstado() {
        return estado;
    }
}
