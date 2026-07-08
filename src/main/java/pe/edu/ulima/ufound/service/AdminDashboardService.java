package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.ObjetoEncontradoRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AdminDashboardService {

    private final ObjetoPerdidoRepository objetoPerdidoRepository;
    private final ObjetoEncontradoRepository objetoEncontradoRepository;
    private final CoincidenciaRepository coincidenciaRepository;

    public AdminDashboardService(ObjetoPerdidoRepository objetoPerdidoRepository,
                                 ObjetoEncontradoRepository objetoEncontradoRepository,
                                 CoincidenciaRepository coincidenciaRepository) {
        this.objetoPerdidoRepository = objetoPerdidoRepository;
        this.objetoEncontradoRepository = objetoEncontradoRepository;
        this.coincidenciaRepository = coincidenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminKpi> obtenerKpis() {
        List<ObjetoPerdido> perdidos = objetoPerdidoRepository.findAll();
        List<ObjetoEncontrado> encontrados = objetoEncontradoRepository.findAll();
        long entregados = contarEstado(perdidos, encontrados, EstadoObjeto.ENTREGADO);
        long archivados = contarEstado(perdidos, encontrados, EstadoObjeto.ARCHIVADO);

        return List.of(
                new AdminKpi("Objetos perdidos", perdidos.size(), "Total historico", "lost"),
                new AdminKpi("Objetos encontrados", encontrados.size(), "Total historico", "found"),
                new AdminKpi("Coincidencias", coincidenciaRepository.count(), "Detectadas por el sistema", "match"),
                new AdminKpi("Entregados", entregados, "Objetos entregados", "delivered"),
                new AdminKpi("Archivados", archivados, "Total historico", "archived")
        );
    }

    @Transactional(readOnly = true)
    public List<AdminMesDato> obtenerDatosMensuales() {
        List<ObjetoPerdido> perdidos = objetoPerdidoRepository.findAll();
        List<ObjetoEncontrado> encontrados = objetoEncontradoRepository.findAll();
        LocalDate hoy = LocalDate.now();
        List<AdminMesDato> preliminar = new ArrayList<>();
        long maximo = 1;

        for (int i = 5; i >= 0; i--) {
            LocalDate mesActual = hoy.minusMonths(i);
            Month mes = mesActual.getMonth();
            int anio = mesActual.getYear();
            long totalPerdidos = perdidos.stream()
                    .filter(objeto -> objeto.getFechaHora().getMonth() == mes && objeto.getFechaHora().getYear() == anio)
                    .count();
            long totalEncontrados = encontrados.stream()
                    .filter(objeto -> objeto.getFechaHora().getMonth() == mes && objeto.getFechaHora().getYear() == anio)
                    .count();
            maximo = Math.max(maximo, Math.max(totalPerdidos, totalEncontrados));
            preliminar.add(new AdminMesDato(nombreMes(mes), totalPerdidos, totalEncontrados, 1));
        }

        long maximoFinal = maximo;
        return preliminar.stream()
                .map(dato -> new AdminMesDato(dato.getMes(), dato.getPerdidos(), dato.getEncontrados(), maximoFinal))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminCategoriaDato> obtenerCategorias() {
        List<ObjetoPerdido> perdidos = objetoPerdidoRepository.findAll();
        List<ObjetoEncontrado> encontrados = objetoEncontradoRepository.findAll();
        Map<CategoriaObjeto, Long> conteos = new EnumMap<>(CategoriaObjeto.class);
        for (CategoriaObjeto categoria : CategoriaObjeto.values()) {
            long total = perdidos.stream().filter(objeto -> objeto.getCategoria() == categoria).count()
                    + encontrados.stream().filter(objeto -> objeto.getCategoria() == categoria).count();
            conteos.put(categoria, total);
        }
        long totalGeneral = conteos.values().stream().mapToLong(Long::longValue).sum();
        String[] colores = {"#173b70", "#2f65b3", "#c5392f", "#f59e0b", "#94a3b8", "#16a34a", "#7c3aed"};
        List<AdminCategoriaDato> datos = new ArrayList<>();
        int indice = 0;
        for (Map.Entry<CategoriaObjeto, Long> entry : conteos.entrySet()) {
            int porcentaje = totalGeneral == 0 ? 0 : (int) Math.round(entry.getValue() * 100.0 / totalGeneral);
            datos.add(new AdminCategoriaDato(entry.getKey().getEtiqueta(), entry.getValue(), porcentaje, colores[indice % colores.length]));
            indice++;
        }
        return datos.stream()
                .sorted(Comparator.comparing(AdminCategoriaDato::getTotal).reversed())
                .limit(5)
                .toList();
    }

    public String construirPieStyle(List<AdminCategoriaDato> categorias) {
        if (categorias == null || categorias.isEmpty() || categorias.stream().allMatch(categoria -> categoria.getPorcentaje() == 0)) {
            return "background: conic-gradient(#e2e8f0 0 100%)";
        }
        int inicio = 0;
        StringBuilder builder = new StringBuilder("background: conic-gradient(");
        for (int i = 0; i < categorias.size(); i++) {
            AdminCategoriaDato categoria = categorias.get(i);
            int fin = i == categorias.size() - 1 ? 100 : Math.min(100, inicio + categoria.getPorcentaje());
            builder.append(categoria.getColor()).append(" ").append(inicio).append("% ").append(fin).append("%");
            if (i < categorias.size() - 1) {
                builder.append(", ");
            }
            inicio = fin;
        }
        builder.append(")");
        return builder.toString();
    }

    @Transactional(readOnly = true)
    public List<AdminActividad> obtenerActividadReciente() {
        List<AdminActividad> actividad = new ArrayList<>();
        objetoPerdidoRepository.findAll().forEach(objeto -> actividad.add(new AdminActividad(
                "UF-PE-" + String.format("%03d", objeto.getIdObjeto()),
                objeto.getNombre(),
                TipoObjeto.PERDIDO,
                objeto.getEstado(),
                objeto.getUsuario().getNombre(),
                objeto.getFechaHora(),
                objeto.getImagenUrl()
        )));
        objetoEncontradoRepository.findAll().forEach(objeto -> actividad.add(new AdminActividad(
                "UF-EN-" + String.format("%03d", objeto.getIdObjeto()),
                objeto.getNombre(),
                TipoObjeto.ENCONTRADO,
                objeto.getEstado(),
                objeto.getUsuario().getNombre(),
                objeto.getFechaHora(),
                objeto.getImagenUrl()
        )));
        actividad.sort(Comparator.comparing(AdminActividad::getFechaHora).reversed());
        return actividad.stream().limit(8).toList();
    }

    private long contarEstado(List<ObjetoPerdido> perdidos, List<ObjetoEncontrado> encontrados, EstadoObjeto estado) {
        return perdidos.stream().filter(objeto -> objeto.getEstado() == estado).count()
                + encontrados.stream().filter(objeto -> objeto.getEstado() == estado).count();
    }

    private String nombreMes(Month month) {
        String nombre = month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es-PE"));
        return nombre.substring(0, 1).toUpperCase(Locale.ROOT) + nombre.substring(1).replace(".", "");
    }
}
