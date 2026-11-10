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
@Table(name = "objetos_perdidos")
public class ObjetoPerdido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_objeto")
    private Long idObjeto;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CategoriaObjeto categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UbicacionCampus ubicacion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 700)
    private String descripcion;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "tipo_visual", length = 30)
    private String tipoVisual;

    @Column(name = "color_visual", length = 30)
    private String colorVisual;

    @Column(name = "confianza_visual")
    private Double confianzaVisual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoObjeto estado = EstadoObjeto.REPORTADO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoObjeto.REPORTADO;
        }
    }

    public Long getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(Long idObjeto) {
        this.idObjeto = idObjeto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaObjeto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaObjeto categoria) {
        this.categoria = categoria;
    }

    public UbicacionCampus getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(UbicacionCampus ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getTipoVisual() {
        return tipoVisual;
    }

    public void setTipoVisual(String tipoVisual) {
        this.tipoVisual = tipoVisual;
    }

    public String getColorVisual() {
        return colorVisual;
    }

    public void setColorVisual(String colorVisual) {
        this.colorVisual = colorVisual;
    }

    public Double getConfianzaVisual() {
        return confianzaVisual;
    }

    public void setConfianzaVisual(Double confianzaVisual) {
        this.confianzaVisual = confianzaVisual;
    }

    public EstadoObjeto getEstado() {
        return estado;
    }

    public void setEstado(EstadoObjeto estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

