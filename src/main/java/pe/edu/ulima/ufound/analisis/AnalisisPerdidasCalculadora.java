package pe.edu.ulima.ufound.analisis;

import org.springframework.stereotype.Component;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Component
public class AnalisisPerdidasCalculadora {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DIA_CORTO = DateTimeFormatter.ofPattern("dd/MM");
    private static final Locale LOCALE = Locale.forLanguageTag("es-PE");
    private static final int ALTURA_MAXIMA = 120;

    public AnalisisPerdidasVista analizar(List<ObjetoPerdido> perdidos, String codigoPeriodo,
                                          LocalDate desde, LocalDate hasta, LocalDate hoy) {
        List<ObjetoPerdido> reportes = perdidos == null ? List.of() : perdidos.stream()
                .filter(this::reporteCompleto)
                .toList();
        LocalDate referencia = hoy == null ? LocalDate.now() : hoy;
        LocalDate primeraFecha = reportes.stream()
                .map(objeto -> objeto.getFechaHora().toLocalDate())
                .min(Comparator.naturalOrder())
                .orElse(referencia);

        ResolucionPeriodo resolucion = resolverPeriodo(codigoPeriodo, desde, hasta, referencia, primeraFecha);
        RangoPeriodo periodo = resolucion.rango();

        List<ObjetoPerdido> enPeriodo = reportes.stream()
                .filter(objeto -> contiene(objeto.getFechaHora().toLocalDate(), periodo.getLimiteInferior(), periodo.getFin()))
                .toList();
        List<ObjetoPerdido> enAnterior = List.of();
        if (periodo.isComparable()) {
            enAnterior = reportes.stream()
                    .filter(objeto -> contiene(objeto.getFechaHora().toLocalDate(), periodo.getInicioAnterior(), periodo.getFinAnterior()))
                    .toList();
        }

        List<FrecuenciaCategoria> categorias = agruparCategorias(enPeriodo);
        List<FrecuenciaUbicacion> ubicaciones = agruparUbicaciones(enPeriodo, enAnterior);
        List<PuntoTendencia> tendencia = construirTendencia(enPeriodo, periodo.getInicio(), periodo.getFin());

        String categoriaLider = categorias.stream()
                .filter(categoria -> categoria.getTotal() > 0)
                .map(FrecuenciaCategoria::getEtiqueta)
                .findFirst()
                .orElse("Sin reportes");
        String ubicacionLider = ubicaciones.stream()
                .filter(ubicacion -> ubicacion.getTotal() > 0)
                .map(FrecuenciaUbicacion::getEtiqueta)
                .findFirst()
                .orElse("Sin reportes");

        return new AnalisisPerdidasVista(periodo, categorias, ubicaciones, tendencia,
                enPeriodo.size(), enAnterior.size(), categoriaLider, ubicacionLider, resolucion.mensaje());
    }

    private List<FrecuenciaCategoria> agruparCategorias(List<ObjetoPerdido> enPeriodo) {
        Map<CategoriaObjeto, Long> conteos = new EnumMap<>(CategoriaObjeto.class);
        for (CategoriaObjeto categoria : CategoriaObjeto.values()) {
            conteos.put(categoria, enPeriodo.stream().filter(objeto -> objeto.getCategoria() == categoria).count());
        }
        long maximo = conteos.values().stream().mapToLong(Long::longValue).max().orElse(0);
        long total = enPeriodo.size();
        List<FrecuenciaCategoria> frecuencias = new ArrayList<>();
        for (Map.Entry<CategoriaObjeto, Long> entrada : conteos.entrySet()) {
            long cantidad = entrada.getValue();
            frecuencias.add(new FrecuenciaCategoria(
                    entrada.getKey().getEtiqueta(),
                    cantidad,
                    porcentaje(cantidad, total),
                    ancho(cantidad, maximo)
            ));
        }
        frecuencias.sort(Comparator.comparingLong(FrecuenciaCategoria::getTotal).reversed()
                .thenComparing(FrecuenciaCategoria::getEtiqueta));
        return List.copyOf(frecuencias);
    }

    private List<FrecuenciaUbicacion> agruparUbicaciones(List<ObjetoPerdido> enPeriodo, List<ObjetoPerdido> enAnterior) {
        Map<UbicacionCampus, Long> actual = contarUbicaciones(enPeriodo);
        Map<UbicacionCampus, Long> anterior = contarUbicaciones(enAnterior);
        long maximo = actual.values().stream().mapToLong(Long::longValue).max().orElse(0);
        long total = enPeriodo.size();
        List<FrecuenciaUbicacion> frecuencias = new ArrayList<>();
        for (UbicacionCampus ubicacion : UbicacionCampus.values()) {
            long cantidad = actual.getOrDefault(ubicacion, 0L);
            long cantidadAnterior = anterior.getOrDefault(ubicacion, 0L);
            if (cantidad == 0 && cantidadAnterior == 0) {
                continue;
            }
            frecuencias.add(new FrecuenciaUbicacion(
                    ubicacion.getEtiqueta(),
                    cantidad,
                    cantidadAnterior,
                    porcentaje(cantidad, total),
                    ancho(cantidad, maximo)
            ));
        }
        frecuencias.sort(Comparator.comparingLong(FrecuenciaUbicacion::getTotal).reversed()
                .thenComparing(Comparator.comparingLong(FrecuenciaUbicacion::getTotalAnterior).reversed())
                .thenComparing(FrecuenciaUbicacion::getEtiqueta));
        return List.copyOf(frecuencias);
    }

    private Map<UbicacionCampus, Long> contarUbicaciones(List<ObjetoPerdido> reportes) {
        Map<UbicacionCampus, Long> conteos = new EnumMap<>(UbicacionCampus.class);
        for (ObjetoPerdido reporte : reportes) {
            conteos.merge(reporte.getUbicacion(), 1L, Long::sum);
        }
        return conteos;
    }

    private List<PuntoTendencia> construirTendencia(List<ObjetoPerdido> enPeriodo, LocalDate inicio, LocalDate fin) {
        LocalDate desde = inicio == null ? fin : inicio;
        long dias = ChronoUnit.DAYS.between(desde, fin) + 1;
        List<PuntoTendencia> puntos = new ArrayList<>();
        if (dias <= 14) {
            for (LocalDate dia = desde; !dia.isAfter(fin); dia = dia.plusDays(1)) {
                puntos.add(new PuntoTendencia(dia.format(DIA_CORTO), contarEntre(enPeriodo, dia, dia), 0));
            }
        } else if (dias <= 62) {
            LocalDate cursor = desde;
            while (!cursor.isAfter(fin)) {
                LocalDate cierre = cursor.plusDays(6);
                if (cierre.isAfter(fin)) {
                    cierre = fin;
                }
                String etiqueta = cursor.format(DIA_CORTO) + "-" + cierre.format(DIA_CORTO);
                puntos.add(new PuntoTendencia(etiqueta, contarEntre(enPeriodo, cursor, cierre), 0));
                cursor = cierre.plusDays(1);
            }
        } else {
            LocalDate cursor = desde;
            while (!cursor.isAfter(fin)) {
                LocalDate cierre = cursor.withDayOfMonth(cursor.lengthOfMonth());
                if (cierre.isAfter(fin)) {
                    cierre = fin;
                }
                String mes = cursor.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, LOCALE);
                String etiqueta = capitalizar(mes) + " " + cursor.getYear();
                puntos.add(new PuntoTendencia(etiqueta, contarEntre(enPeriodo, cursor, cierre), 0));
                cursor = cierre.plusDays(1);
            }
        }
        return aplicarAlturas(puntos);
    }

    private List<PuntoTendencia> aplicarAlturas(List<PuntoTendencia> puntos) {
        long maximo = puntos.stream().mapToLong(PuntoTendencia::getTotal).max().orElse(0);
        List<PuntoTendencia> conAltura = new ArrayList<>();
        for (PuntoTendencia punto : puntos) {
            int altura = maximo == 0 || punto.getTotal() == 0
                    ? 0
                    : (int) Math.max(8, Math.round(punto.getTotal() * (double) ALTURA_MAXIMA / maximo));
            conAltura.add(new PuntoTendencia(punto.getEtiqueta(), punto.getTotal(), altura));
        }
        return List.copyOf(conAltura);
    }

    private long contarEntre(List<ObjetoPerdido> reportes, LocalDate inicio, LocalDate fin) {
        return reportes.stream()
                .filter(objeto -> contiene(objeto.getFechaHora().toLocalDate(), inicio, fin))
                .count();
    }

    private ResolucionPeriodo resolverPeriodo(String codigoPeriodo, LocalDate desde, LocalDate hasta,
                                              LocalDate hoy, LocalDate primeraFecha) {
        VentanaAnalisis ventana = VentanaAnalisis.desdeCodigo(codigoPeriodo);
        String mensaje = null;
        LocalDate fin = hoy;
        LocalDate inicio;
        LocalDate limiteInferior;
        boolean comparable = true;

        switch (ventana) {
            case ULTIMOS_7_DIAS -> inicio = hoy.minusDays(6);
            case ULTIMOS_90_DIAS -> inicio = hoy.minusDays(89);
            case MES_ACTUAL -> inicio = hoy.withDayOfMonth(1);
            case HISTORICO -> {
                inicio = primeraFecha;
                comparable = false;
            }
            case PERSONALIZADO -> {
                if (desde == null || hasta == null) {
                    ventana = VentanaAnalisis.ULTIMOS_30_DIAS;
                    inicio = hoy.minusDays(29);
                    mensaje = "Indica la fecha inicial y la final para un periodo personalizado. Se aplicaron los ultimos 30 dias.";
                } else {
                    inicio = desde;
                    fin = hasta;
                    if (inicio.isAfter(fin)) {
                        LocalDate temporal = inicio;
                        inicio = fin;
                        fin = temporal;
                        mensaje = "La fecha inicial era posterior a la final. Se ordenaron para identificar el periodo.";
                    }
                }
            }
            default -> inicio = hoy.minusDays(29);
        }
        limiteInferior = ventana == VentanaAnalisis.HISTORICO ? null : inicio;

        LocalDate inicioAnterior = null;
        LocalDate finAnterior = null;
        String descripcionAnterior = "El historico completo no tiene un periodo anterior comparable.";
        if (comparable) {
            long dias = ChronoUnit.DAYS.between(inicio, fin) + 1;
            finAnterior = inicio.minusDays(1);
            inicioAnterior = finAnterior.minusDays(dias - 1);
            descripcionAnterior = "Periodo anterior comparable: del " + inicioAnterior.format(FECHA)
                    + " al " + finAnterior.format(FECHA) + ".";
        }

        String descripcion = "Periodo analizado: del " + inicio.format(FECHA) + " al " + fin.format(FECHA)
                + " (" + ventana.getNombre() + ").";
        RangoPeriodo rango = new RangoPeriodo(ventana.getCodigo(), ventana.getNombre(), inicio, fin, limiteInferior,
                inicioAnterior, finAnterior, comparable, descripcion, descripcionAnterior);
        return new ResolucionPeriodo(rango, mensaje);
    }

    private boolean reporteCompleto(ObjetoPerdido objeto) {
        return objeto != null && objeto.getFechaHora() != null && objeto.getCategoria() != null && objeto.getUbicacion() != null;
    }

    private boolean contiene(LocalDate fecha, LocalDate inicio, LocalDate fin) {
        if (fecha == null) {
            return false;
        }
        if (inicio != null && fecha.isBefore(inicio)) {
            return false;
        }
        return fin == null || !fecha.isAfter(fin);
    }

    private int porcentaje(long cantidad, long total) {
        if (total == 0) {
            return 0;
        }
        return (int) Math.round(cantidad * 100.0 / total);
    }

    private int ancho(long cantidad, long maximo) {
        if (maximo == 0) {
            return 0;
        }
        return (int) Math.round(cantidad * 100.0 / maximo);
    }

    private String capitalizar(String texto) {
        String limpio = Objects.toString(texto, "").replace(".", "");
        if (limpio.isEmpty()) {
            return limpio;
        }
        return limpio.substring(0, 1).toUpperCase(LOCALE) + limpio.substring(1);
    }

    private record ResolucionPeriodo(RangoPeriodo rango, String mensaje) {
    }
}
