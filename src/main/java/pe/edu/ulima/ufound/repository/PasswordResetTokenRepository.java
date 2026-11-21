package pe.edu.ulima.ufound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.ulima.ufound.model.PasswordResetToken;
import pe.edu.ulima.ufound.model.Usuario;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHashAndUsadoFalse(String tokenHash);

    long deleteByUsuario(Usuario usuario);
}