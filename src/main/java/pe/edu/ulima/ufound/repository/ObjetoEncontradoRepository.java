package pe.edu.ulima.ufound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.Usuario;

import java.util.List;

public interface ObjetoEncontradoRepository extends JpaRepository<ObjetoEncontrado, Long> {

    List<ObjetoEncontrado> findAllByOrderByFechaHoraDesc();

    List<ObjetoEncontrado> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);

    long countByUsuario(Usuario usuario);
}

