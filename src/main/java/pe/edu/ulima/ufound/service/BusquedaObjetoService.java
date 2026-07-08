package pe.edu.ulima.ufound.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.TipoObjeto;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.repository.ObjetoEncontradoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class BusquedaObjetoService {

    private final ObjetoEncontradoRepository objetoEncontradoRepository;

    public BusquedaObjetoService(ObjetoEncontradoRepository objetoEncontradoRepository) {
        this.objetoEncontradoRepository = objetoEncontradoRepository;
    }

    @Transactional(readOnly = true)
    public List<BusquedaObjetoResultado> buscar(String texto, CategoriaObjeto categoria, UbicacionCampus ubicacion,
                                                LocalDate fechaDesde, LocalDate fechaHasta) {
        return objetoEncontradoRepository
                .findAll(specEncontrados(texto, categoria, ubicacion, fechaDesde, fechaHasta))
                .stream()
                .map(this::desdeEncontrado)
                .sorted(Comparator.comparing(BusquedaObjetoResultado::getFechaHora).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BusquedaObjetoResultado> listarRecientes() {
        return objetoEncontradoRepository.findAll().stream()
                .map(this::desdeEncontrado)
                .sorted(Comparator.comparing(BusquedaObjetoResultado::getFechaHora).reversed())
                .limit(8)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObjetoEncontrado> listarEncontradosRecientes() {
        return objetoEncontradoRepository.findTop6ByOrderByFechaHoraDesc();
    }

    @Transactional(readOnly = true)
    public BusquedaObjetoResultado obtenerDetalle(TipoObjeto tipoObjeto, Long idObjeto) {
        if (tipoObjeto != TipoObjeto.ENCONTRADO) {
            throw new RegistroObjetoException("La busqueda solo muestra objetos encontrados.");
        }
        return desdeEncontrado(objetoEncontradoRepository.findById(idObjeto)
                .orElseThrow(() -> new RegistroObjetoException("El objeto encontrado solicitado no existe.")));
    }

    private Specification<ObjetoEncontrado> specEncontrados(String texto, CategoriaObjeto categoria, UbicacionCampus ubicacion,
                                                            LocalDate fechaDesde, LocalDate fechaHasta) {
        return (root, query, cb) -> cb.and(
                filtroTexto(texto, root.get("nombre"), root.get("descripcion"), cb),
                categoria == null ? cb.conjunction() : cb.equal(root.get("categoria"), categoria),
                ubicacion == null ? cb.conjunction() : cb.equal(root.get("ubicacion"), ubicacion),
                rangoFecha(root.get("fechaHora"), fechaDesde, fechaHasta, cb)
        );
    }

    private jakarta.persistence.criteria.Predicate filtroTexto(String texto, jakarta.persistence.criteria.Path<String> nombre,
                                                               jakarta.persistence.criteria.Path<String> descripcion,
                                                               jakarta.persistence.criteria.CriteriaBuilder cb) {
        if (texto == null || texto.isBlank()) {
            return cb.conjunction();
        }
        String patron = "%" + texto.trim().toLowerCase(Locale.ROOT) + "%";
        return cb.or(cb.like(cb.lower(nombre), patron), cb.like(cb.lower(descripcion), patron));
    }

    private jakarta.persistence.criteria.Predicate rangoFecha(jakarta.persistence.criteria.Path<LocalDateTime> campo,
                                                              LocalDate fechaDesde, LocalDate fechaHasta,
                                                              jakarta.persistence.criteria.CriteriaBuilder cb) {
        if (fechaDesde == null && fechaHasta == null) {
            return cb.conjunction();
        }
        LocalDateTime desde = fechaDesde == null ? LocalDate.of(2000, 1, 1).atStartOfDay() : fechaDesde.atStartOfDay();
        LocalDateTime hasta = fechaHasta == null ? LocalDate.of(2100, 12, 31).atTime(LocalTime.MAX) : fechaHasta.atTime(LocalTime.MAX);
        return cb.between(campo, desde, hasta);
    }

    private BusquedaObjetoResultado desdeEncontrado(ObjetoEncontrado objeto) {
        return new BusquedaObjetoResultado(TipoObjeto.ENCONTRADO, objeto.getIdObjeto(), objeto.getNombre(),
                objeto.getCategoria(), objeto.getUbicacion(), objeto.getFechaHora(), objeto.getDescripcion(),
                objeto.getImagenUrl(), objeto.getEstado());
    }
}
