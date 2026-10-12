
package pe.edu.ulima.ufound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public interface ObjetoPerdidoRepository
        extends JpaRepository<ObjetoPerdido, Long>,
        JpaSpecificationExecutor<ObjetoPerdido> {

    List<ObjetoPerdido> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);

    long countByUsuario(Usuario usuario);

    long countByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
            LocalDateTime inicio,
            LocalDateTime fin);

    @Query("""
            SELECT o.categoria, COUNT(o)
            FROM ObjetoPerdido o
            GROUP BY o.categoria
            ORDER BY COUNT(o) DESC
            """)
    List<Object[]> contarPorCategoria();

    @Query("""
            SELECT o.ubicacion, COUNT(o)
            FROM ObjetoPerdido o
            GROUP BY o.ubicacion
            ORDER BY COUNT(o) DESC
            """)
    List<Object[]> contarPorUbicacion();

    @Query("""
            SELECT COUNT(o)
            FROM ObjetoPerdido o
            WHERE o.fechaHora >= :inicio
              AND o.fechaHora < :fin
              AND o.categoria = :categoria
            """)
    long contarPorCategoriaYPeriodo(
            @Param("categoria") CategoriaObjeto categoria,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("""
            SELECT COUNT(o)
            FROM ObjetoPerdido o
            WHERE o.ubicacion = :ubicacion
            """)
    long contarPorUbicacion(
            @Param("ubicacion") UbicacionCampus ubicacion);
}
