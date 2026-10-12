
package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class EstadisticasPerdidasService {

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Lima");

    private static final Locale IDIOMA = Locale.forLanguageTag("es-PE");

    private final ObjetoPerdidoRepository repositorio;

    public EstadisticasPerdidasService(
            ObjetoPerdidoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public record DatoMensual(
            String mes,
            long total) {
    }

    public record DatoRanking(
            String nombre,
            long total,
            int porcentaje) {
    }

    public record ResumenEstadistico(
            long totalHistorico,
            long totalMesActual,
            long totalMesAnterior,
            String variacionMensual,
            List<DatoMensual> tendenciaMensual,
            List<DatoRanking> categorias,
            List<DatoRanking> ubicaciones,
            List<String> hallazgos,
            boolean sinDatos) {
    }

    @Transactional(readOnly = true)
    public ResumenEstadistico obtenerResumen() {

        LocalDate hoy = LocalDate.now(ZONA_HORARIA);
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        long totalHistorico = repositorio.count();

        long mesActual = contarMes(inicioMes);
        long mesAnterior = contarMes(inicioMes.minusMonths(1));

        List<DatoMensual> tendencia = construirTendencia(inicioMes);

        List<DatoRanking> categorias = construirCategorias(totalHistorico);

        List<DatoRanking> ubicaciones = construirUbicaciones(totalHistorico);

        String variacion = calcularVariacion(mesActual, mesAnterior);

        List<String> hallazgos = construirHallazgos(
                totalHistorico,
                mesActual,
                mesAnterior,
                categorias,
                ubicaciones);

        return new ResumenEstadistico(
                totalHistorico,
                mesActual,
                mesAnterior,
                variacion,
                tendencia,
                categorias,
                ubicaciones,
                hallazgos,
                totalHistorico == 0);
    }

    private long contarMes(LocalDate inicio) {

        LocalDateTime desde = inicio.atStartOfDay();
        LocalDateTime hasta = inicio.plusMonths(1).atStartOfDay();

        return repositorio
                .countByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                        desde, hasta);
    }

    private List<DatoMensual> construirTendencia(
            LocalDate inicioMes) {

        List<DatoMensual> datos = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {

            LocalDate mes = inicioMes.minusMonths(i);

            String nombre = mes.getMonth()
                    .getDisplayName(TextStyle.SHORT, IDIOMA)
                    .replace(".", "");

            String etiqueta = nombre.substring(0, 1)
                    .toUpperCase(IDIOMA)
                    + nombre.substring(1)
                    + " "
                    + mes.getYear();

            datos.add(new DatoMensual(
                    etiqueta,
                    contarMes(mes)));
        }

        return List.copyOf(datos);
    }

    private List<DatoRanking> construirCategorias(long total) {

        return repositorio.contarPorCategoria()
                .stream()
                .filter(fila -> fila[0] != null)
                .limit(5)
                .map(fila -> {

                    CategoriaObjeto categoria = (CategoriaObjeto) fila[0];

                    long cantidad = ((Number) fila[1]).longValue();

                    return new DatoRanking(
                            categoria.getEtiqueta(),
                            cantidad,
                            calcularPorcentaje(cantidad, total));
                })
                .toList();
    }

    private List<DatoRanking> construirUbicaciones(long total) {

        return repositorio.contarPorUbicacion()
                .stream()
                .filter(fila -> fila[0] != null)
                .limit(5)
                .map(fila -> {

                    UbicacionCampus ubicacion = (UbicacionCampus) fila[0];

                    long cantidad = ((Number) fila[1]).longValue();

                    return new DatoRanking(
                            ubicacion.getEtiqueta(),
                            cantidad,
                            calcularPorcentaje(cantidad, total));
                })
                .toList();
    }

    private int calcularPorcentaje(long cantidad, long total) {

        if (total == 0) {
            return 0;
        }

        return (int) Math.round(
                cantidad * 100.0 / total);
    }

    private String calcularVariacion(long actual, long anterior) {

        if (anterior == 0) {
            return actual == 0
                    ? "Sin cambios"
                    : "Sin base comparable";
        }

        double porcentaje = (actual - anterior) * 100.0 / anterior;

        return String.format(
                IDIOMA,
                "%+.1f%%",
                porcentaje);
    }

    private List<String> construirHallazgos(
            long totalHistorico,
            long actual,
            long anterior,
            List<DatoRanking> categorias,
            List<DatoRanking> ubicaciones) {

        List<String> hallazgos = new ArrayList<>();

        if (totalHistorico == 0) {
            hallazgos.add(
                    "Aún no existen reportes de objetos perdidos.");
            return List.copyOf(hallazgos);
        }

        if (!ubicaciones.isEmpty()) {

            DatoRanking principal = ubicaciones.get(0);

            hallazgos.add(
                    "La ubicación con más reportes históricos es "
                            + principal.nombre()
                            + ", con "
                            + principal.total()
                            + " registros.");
        }

        if (!categorias.isEmpty()) {

            DatoRanking principal = categorias.get(0);

            hallazgos.add(
                    "La categoría con mayor frecuencia histórica es "
                            + principal.nombre()
                            + ", que representa aproximadamente el "
                            + principal.porcentaje()
                            + "% de los reportes.");
        }

        if (actual > anterior) {

            hallazgos.add(
                    "En el mes actual se registran más pérdidas "
                            + "que en el mes anterior.");

        } else if (actual < anterior) {

            hallazgos.add(
                    "En el mes actual se registran menos pérdidas "
                            + "que en el mes anterior.");

        } else {

            hallazgos.add(
                    "El número de reportes del mes actual "
                            + "coincide con el del mes anterior.");
        }

        return List.copyOf(hallazgos);
    }
}
