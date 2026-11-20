package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;
    private Usuario usuarioDePrueba;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioRepository, passwordEncoder);

        usuarioDePrueba = new Usuario();
        usuarioDePrueba.setNombre("Usuario de prueba");
        usuarioDePrueba.setCorreo("usuario@ulima.edu.pe");
        usuarioDePrueba.setPassword("123456");
        usuarioDePrueba.setRol(Rol.ESTUDIANTE);
        usuarioDePrueba.setActivo(true);
    }

    @Test
    void cp01CorreoVacioLanzaAuthException() {
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.autenticar("   ", "123456"));

        assertEquals("Ingresa correo y contraseña.", exception.getMessage());
    }

    @Test
    void cp02PasswordVacioLanzaAuthException() {
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.autenticar("usuario@ulima.edu.pe", ""));

        assertEquals("Ingresa correo y contraseña.", exception.getMessage());
    }

    @Test
    void cp03UsuarioInexistenteOInactivoLanzaAuthException() {
        when(usuarioRepository.findByCorreoAndActivoTrue("usuario@ulima.edu.pe"))
                .thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class,
                () -> authService.autenticar("usuario@ulima.edu.pe", "123456"));

        assertEquals("Credenciales incorrectas.", exception.getMessage());
    }

    @Test
    void cp04PasswordIncorrectoLanzaAuthException() {
        when(usuarioRepository.findByCorreoAndActivoTrue("usuario@ulima.edu.pe"))
                .thenReturn(Optional.of(usuarioDePrueba));

        AuthException exception = assertThrows(AuthException.class,
                () -> authService.autenticar("usuario@ulima.edu.pe", "otra-contrasena"));

        assertEquals("Credenciales incorrectas.", exception.getMessage());
    }

    @Test
    void cp05AutenticacionCorrectaNormalizaCorreoYMigraPassword() {
        when(usuarioRepository.findByCorreoAndActivoTrue("usuario@ulima.edu.pe"))
                .thenReturn(Optional.of(usuarioDePrueba));

        when(passwordEncoder.encode("123456"))
                .thenReturn("$2a$10$hashDePruebaParaUfound123456789012345678901234567890");

        Usuario resultado = authService.autenticar(
                "  USUARIO@ULIMA.EDU.PE  ",
                "123456");

        assertSame(usuarioDePrueba, resultado);
        assertEquals(
                "$2a$10$hashDePruebaParaUfound123456789012345678901234567890",
                usuarioDePrueba.getPassword());

        verify(usuarioRepository).findByCorreoAndActivoTrue("usuario@ulima.edu.pe");
        verify(usuarioRepository).save(usuarioDePrueba);
    }
}