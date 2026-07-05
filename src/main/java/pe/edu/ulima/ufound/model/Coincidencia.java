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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "coincidencias", uniqueConstraints = {
        @UniqueConstraint(name = "uk_coincidencia_objetos", columnNames = {"id_objeto_perdido", "id_objeto_encontrado"})
})
public class Coincidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_coincidencia")
    private Long idCoincidencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_objeto_perdido", nullable = false)
    private ObjetoPerdido objetoPerdido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_objeto_encontrado", nullable = false)
    private ObjetoEncontrado objetoEncontrado;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_coincidencia", nullable = false, length = 20)
    private NivelCoincidencia nivelCoincidencia;

    @Column(name = "porcentaje_similitud", nullable = false)
    private Integer porcentajeSimilitud;

    @Column(name = "fecha_deteccion", nullable = false)
    private LocalDateTime fechaDeteccion;

    @PrePersist
    public void prePersist() {
        if (fechaDeteccion == null) {
            fechaDeteccion = LocalDateTime.now();
        }
    }

    public Long getIdCoincidencia() {
        return idCoincidencia;
    }

    public void setIdCoincidencia(Long idCoincidencia) {
        this.idCoincidencia = idCoincidencia;
    }

    public ObjetoPerdido getObjetoPerdido() {
        return objetoPerdido;
    }

    public void setObjetoPerdido(ObjetoPerdido objetoPerdido) {
        this.objetoPerdido = objetoPerdido;
    }

    public ObjetoEncontrado getObjetoEncontrado() {
        return objetoEncontrado;
    }

    public void setObjetoEncontrado(ObjetoEncontrado objetoEncontrado) {
        this.objetoEncontrado = objetoEncontrado;
    }

    public NivelCoincidencia getNivelCoincidencia() {
        return nivelCoincidencia;
    }

    public void setNivelCoincidencia(NivelCoincidencia nivelCoincidencia) {
        this.nivelCoincidencia = nivelCoincidencia;
    }

    public Integer getPorcentajeSimilitud() {
        return porcentajeSimilitud;
    }

    public void setPorcentajeSimilitud(Integer porcentajeSimilitud) {
        this.porcentajeSimilitud = porcentajeSimilitud;
    }

    public LocalDateTime getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDateTime fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }
}
