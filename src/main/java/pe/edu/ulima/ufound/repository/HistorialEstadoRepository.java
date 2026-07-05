package pe.edu.ulima.ufound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.ulima.ufound.model.HistorialEstado;
import pe.edu.ulima.ufound.model.TipoObjeto;

import java.util.List;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    List<HistorialEstado> findByTipoObjetoAndIdObjetoOrderByFechaCambioAsc(TipoObjeto tipoObjeto, Long idObjeto);
}
