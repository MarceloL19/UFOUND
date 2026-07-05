package pe.edu.ulima.ufound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface CoincidenciaRepository extends JpaRepository<Coincidencia, Long> {

    Optional<Coincidencia> findByObjetoPerdidoAndObjetoEncontrado(ObjetoPerdido objetoPerdido,
                                                                  ObjetoEncontrado objetoEncontrado);

    List<Coincidencia> findByObjetoPerdidoUsuarioOrderByPorcentajeSimilitudDescFechaDeteccionDesc(Usuario usuario);

    long countByObjetoPerdidoUsuario(Usuario usuario);
}
