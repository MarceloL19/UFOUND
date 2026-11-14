package pe.edu.ulima.ufound.hu35;

import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.ObjetoPerdido;

import java.util.List;

/** Datos necesarios para mostrar el detalle de un reporte y sus sugerencias HU-35. */
public record DetalleObjetoPerdidoHu35(
        ObjetoPerdido objeto,
        List<Coincidencia> coincidencias) {

    public DetalleObjetoPerdidoHu35 {
        coincidencias = coincidencias == null ? List.of() : List.copyOf(coincidencias);
    }
}
