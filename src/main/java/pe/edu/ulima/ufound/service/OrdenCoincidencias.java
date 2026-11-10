package pe.edu.ulima.ufound.service;

import pe.edu.ulima.ufound.model.Coincidencia;

import java.util.Comparator;

/**
 * HU-34: orden de las coincidencias que ve la persona.
 * <ol>
 *     <li>Puntaje del motor mejorado (HU-33), de mayor a menor.</li>
 *     <li>Desempate: fecha de detección más reciente primero.</li>
 *     <li>Desempate final: id de coincidencia más alto primero (único, por lo que el orden es reproducible).</li>
 * </ol>
 * Los valores nulos (registros aún no guardados) van al final.
 */
public final class OrdenCoincidencias {

    public static final Comparator<Coincidencia> POR_RELEVANCIA = Comparator
            .comparing(Coincidencia::getPorcentajeSimilitud, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Coincidencia::getFechaDeteccion, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Coincidencia::getIdCoincidencia, Comparator.nullsLast(Comparator.reverseOrder()));

    private OrdenCoincidencias() {
    }
}
