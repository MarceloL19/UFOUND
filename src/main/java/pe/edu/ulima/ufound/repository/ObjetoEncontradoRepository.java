package pe.edu.ulima.ufound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.Usuario;

import java.util.List;

public interface ObjetoEncontradoRepository extends JpaRepository<ObjetoEncontrado, Long>, JpaSpecificationExecutor<ObjetoEncontrado> {

    List<ObjetoEncontrado> findAllByOrderByFechaHoraDesc();

    List<ObjetoEncontrado> findTop6ByOrderByFechaHoraDesc();

    List<ObjetoEncontrado> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);

    long countByUsuario(Usuario usuario);
}

