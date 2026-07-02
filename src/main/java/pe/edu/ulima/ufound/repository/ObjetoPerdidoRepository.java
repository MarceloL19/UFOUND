package pe.edu.ulima.ufound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Usuario;

import java.util.List;

public interface ObjetoPerdidoRepository extends JpaRepository<ObjetoPerdido, Long> {

    List<ObjetoPerdido> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);

    long countByUsuario(Usuario usuario);
}

