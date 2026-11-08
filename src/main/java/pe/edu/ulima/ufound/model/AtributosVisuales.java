package pe.edu.ulima.ufound.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AtributosVisuales {

    @Column(name = "tipo_visual", length = 40)
    private String tipo;

    @Column(name = "color_visual", length = 30)
    private String color;

    @Column(name = "confianza_visual")
    private Double confianza;

    public AtributosVisuales() {
    }

    public AtributosVisuales(
            String tipo,
            String color,
            Double confianza) {
        this.tipo = tipo;
        this.color = color;
        this.confianza = confianza;
    }

    public String getTipo() {
        return tipo;
    }

    public String getColor() {
        return color;
    }

    public Double getConfianza() {
        return confianza;
    }
}