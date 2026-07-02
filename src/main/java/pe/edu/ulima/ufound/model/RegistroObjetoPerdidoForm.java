package pe.edu.ulima.ufound.model;

public class RegistroObjetoPerdidoForm {

    private String nombre;
    private CategoriaObjeto categoria;
    private UbicacionCampus ubicacion;
    private String descripcion;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

