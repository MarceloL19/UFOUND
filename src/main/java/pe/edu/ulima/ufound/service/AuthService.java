package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticar(String correo, String password) {
        if (correo == null || correo.isBlank() || password == null || password.isBlank()) {
            throw new AuthException("Ingresa correo y contrasena.");
        }

        Usuario usuario = usuarioRepository.findByCorreoAndActivoTrue(correo.trim().toLowerCase())
                .orElseThrow(() -> new AuthException("Credenciales incorrectas."));

        if (!usuario.getPassword().equals(password)) {
            throw new AuthException("Credenciales incorrectas.");
        }

        return usuario;
    }
}

