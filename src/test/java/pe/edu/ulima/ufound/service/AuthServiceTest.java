package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private AuthService authService;
    private Usuario usuarioDePrueba;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioRepository);

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

        assertEquals("Ingresa correo y contrasena.", exception.getMessage());
    }

    @Test
    void cp02PasswordVacioLanzaAuthException() {
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.autenticar("usuario@ulima.edu.pe", ""));

        assertEquals("Ingresa correo y contrasena.", exception.getMessage());
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
    void cp05AutenticacionCorrectaNormalizaCorreo() {
        when(usuarioRepository.findByCorreoAndActivoTrue("usuario@ulima.edu.pe"))
                .thenReturn(Optional.of(usuarioDePrueba));

        Usuario resultado = authService.autenticar("  USUARIO@ULIMA.EDU.PE  ", "123456");

        assertSame(usuarioDePrueba, resultado);
        verify(usuarioRepository).findByCorreoAndActivoTrue("usuario@ulima.edu.pe");
    }
}
