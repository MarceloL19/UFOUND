package pe.edu.ulima.ufound.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long idHistorial;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_objeto", nullable = false, length = 20)
    private TipoObjeto tipoObjeto;

    @Column(name = "id_objeto", nullable = false)
    private Long idObjeto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 40)
    private EstadoObjeto estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 40)
    private EstadoObjeto estadoNuevo;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_responsable", nullable = false)
    private Usuario usuarioResponsable;

    @PrePersist
    public void prePersist() {
        if (fechaCambio == null) {
            fechaCambio = LocalDateTime.now();
        }
    }

    public Long getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Long idHistorial) {
        this.idHistorial = idHistorial;
    }

    public TipoObjeto getTipoObjeto() {
        return tipoObjeto;
    }

    public void setTipoObjeto(TipoObjeto tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }

    public Long getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(Long idObjeto) {
        this.idObjeto = idObjeto;
    }

    public EstadoObjeto getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoObjeto estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoObjeto getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoObjeto estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }
}
