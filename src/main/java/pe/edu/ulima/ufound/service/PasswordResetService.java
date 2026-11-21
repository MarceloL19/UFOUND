package pe.edu.ulima.ufound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.PasswordResetToken;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.PasswordResetTokenRepository;
import pe.edu.ulima.ufound.repository.UsuarioRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private static final int DURACION_TOKEN_MINUTOS = 30;

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final String baseUrl;
    private final String correoOrigen;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository tokenRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder,
            @Value("${ufound.app.base-url:http://localhost:8080}") String baseUrl,
            @Value("${ufound.mail.from:soporte@ufound.local}") String correoOrigen) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.baseUrl = baseUrl;
        this.correoOrigen = correoOrigen;
    }

    @Transactional
    public void solicitarRecuperacion(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }

        usuarioRepository.findByCorreoAndActivoTrue(correo.trim().toLowerCase())
                .ifPresent(this::crearTokenYEnviarCorreo);
    }

    public boolean esTokenValido(String token) {
        try {
            obtenerTokenValido(token);
            return true;
        } catch (PasswordResetException exception) {
            return false;
        }
    }

    @Transactional
    public void restablecerContrasena(
            String token,
            String nuevaContrasena,
            String confirmacionContrasena) {
        validarNuevaContrasena(nuevaContrasena, confirmacionContrasena);

        PasswordResetToken tokenValido = obtenerTokenValido(token);
        Usuario usuario = tokenValido.getUsuario();

        usuario.setPassword(passwordEncoder.encode(nuevaContrasena));
        tokenValido.setUsado(true);

        usuarioRepository.save(usuario);
        tokenRepository.save(tokenValido);
    }

    private void crearTokenYEnviarCorreo(Usuario usuario) {
        tokenRepository.deleteByUsuario(usuario);

        String tokenPlano = generarTokenSeguro();

        PasswordResetToken token = new PasswordResetToken();
        token.setUsuario(usuario);
        token.setTokenHash(hashToken(tokenPlano));
        token.setFechaExpiracion(
                LocalDateTime.now().plusMinutes(DURACION_TOKEN_MINUTOS));

        tokenRepository.save(token);
        enviarCorreoRecuperacion(usuario, tokenPlano);
    }

    private PasswordResetToken obtenerTokenValido(String token) {
        if (token == null || token.isBlank()) {
            throw new PasswordResetException("El enlace de recuperación no es válido.");
        }

        return tokenRepository
                .findByTokenHashAndUsadoFalse(hashToken(token))
                .filter(item -> item.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new PasswordResetException(
                        "El enlace de recuperación no es válido o ha expirado."));
    }

    private void validarNuevaContrasena(
            String nuevaContrasena,
            String confirmacionContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.length() < 8) {
            throw new PasswordResetException(
                    "La nueva contraseña debe tener al menos 8 caracteres.");
        }

        if (!nuevaContrasena.equals(confirmacionContrasena)) {
            throw new PasswordResetException(
                    "Las contraseñas ingresadas no coinciden.");
        }
    }

    private void enviarCorreoRecuperacion(Usuario usuario, String tokenPlano) {
        String enlace = baseUrl.replaceAll("/+$", "")
                + "/restablecer-contrasena?token=" + tokenPlano;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(correoOrigen);
        mensaje.setTo(usuario.getCorreo());
        mensaje.setSubject("Recuperación de contraseña | U-Found");
        mensaje.setText(
                "Hola, " + usuario.getNombre() + ".\n\n"
                        + "Recibimos una solicitud para restablecer la contraseña "
                        + "de tu cuenta U-Found.\n\n"
                        + "Usa este enlace dentro de los próximos 30 minutos:\n"
                        + enlace + "\n\n"
                        + "Si no solicitaste este cambio, ignora este mensaje.");

        mailSender.send(mensaje);
    }

    private String generarTokenSeguro() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));

            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "No se pudo proteger el token de recuperación.",
                    exception);
        }
    }
}