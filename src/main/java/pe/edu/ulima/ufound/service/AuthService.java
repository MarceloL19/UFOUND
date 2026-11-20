package pe.edu.ulima.ufound.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario autenticar(String correo, String password) {
        if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
            throw new AuthException("Ingresa correo y contraseña.");
        }

        Usuario usuario = usuarioRepository
                .findByCorreoAndActivoTrue(correo.trim().toLowerCase())
                .orElseThrow(() -> new AuthException("Credenciales incorrectas."));

        String passwordGuardada = usuario.getPassword();
        boolean esPasswordAntiguo = !passwordGuardada.startsWith("$2a$")
                && !passwordGuardada.startsWith("$2b$")
                && !passwordGuardada.startsWith("$2y$");

        boolean coincide = esPasswordAntiguo
                ? password.equals(passwordGuardada)
                : passwordEncoder.matches(password, passwordGuardada);

        if (!coincide) {
            throw new AuthException("Credenciales incorrectas.");
        }

        /*
         * Migra automáticamente las contraseñas antiguas de prueba
         * al formato BCrypt luego de un inicio de sesión válido.
         */
        if (esPasswordAntiguo) {
            usuario.setPassword(passwordEncoder.encode(password));
            usuarioRepository.save(usuario);
        }

        return usuario;
    }
}