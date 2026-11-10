package pe.edu.ulima.ufound.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.RegistroObjetoEncontradoForm;
import pe.edu.ulima.ufound.model.RegistroObjetoPerdidoForm;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.NotificacionRepository;
import pe.edu.ulima.ufound.repository.ObjetoEncontradoRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;
import pe.edu.ulima.ufound.repository.UsuarioRepository;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "UFOUND_HU33_MYSQL_TEST", matches = "true")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/proyecto_ufound_hu33_test?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=never",
        "ufound.upload-dir=target/hu33-test-uploads"
})
class RegistroCoincidenciaMySqlTest {

    @Autowired private ObjetoPerdidoService perdidosService;
    @Autowired private ObjetoEncontradoService encontradosService;
    @Autowired private ObjetoPerdidoRepository perdidos;
    @Autowired private ObjetoEncontradoRepository encontrados;
    @Autowired private CoincidenciaRepository coincidencias;
    @Autowired private NotificacionRepository notificaciones;
    @Autowired private UsuarioRepository usuarios;
    @Autowired private EntityManager entityManager;

    @Test
    void registraPrimeroPerdidoYLuegoEncontradoConFotoYPersisteAtributos() throws Exception {
        Usuario estudiante = usuario("estudiante-hu33@prueba.local", Rol.ESTUDIANTE);
        Usuario seguridad = usuario("seguridad-hu33@prueba.local", Rol.SEGURIDAD);
        ObjetoPerdido perdido = perdidosService.registrar(formPerdido(), fotoLaptop(), estudiante);
        ObjetoEncontrado encontrado = encontradosService.registrar(formEncontrado(), fotoLaptop(), seguridad);
        entityManager.flush();
        entityManager.clear();

        ObjetoPerdido perdidoPersistido = perdidos.findById(perdido.getIdObjeto()).orElseThrow();
        ObjetoEncontrado encontradoPersistido = encontrados.findById(encontrado.getIdObjeto()).orElseThrow();
        assertEquals("Laptop", perdidoPersistido.getTipoVisual());
        assertEquals("Laptop", encontradoPersistido.getTipoVisual());
        assertTrue(perdidoPersistido.getConfianzaVisual() >= .35);
        assertTrue(encontradoPersistido.getConfianzaVisual() >= .35);
        assertEquals(1, coincidencias.count());
        assertEquals(1, notificaciones.count());
        assertEquals(EstadoObjeto.COINCIDENCIA_DETECTADA, perdidoPersistido.getEstado());
    }

    @Test
    void registraPrimeroEncontradoYLuegoPerdidoSinFotoYUsaLaBase() {
        Usuario estudiante = usuario("estudiante2-hu33@prueba.local", Rol.ESTUDIANTE);
        Usuario seguridad = usuario("seguridad2-hu33@prueba.local", Rol.SEGURIDAD);
        ObjetoEncontrado encontrado = encontradosService.registrar(formEncontrado(), null, seguridad);
        ObjetoPerdido perdido = perdidosService.registrar(formPerdido(), null, estudiante);
        entityManager.flush();
        entityManager.clear();

        ObjetoPerdido perdidoPersistido = perdidos.findById(perdido.getIdObjeto()).orElseThrow();
        ObjetoEncontrado encontradoPersistido = encontrados.findById(encontrado.getIdObjeto()).orElseThrow();
        assertNull(perdidoPersistido.getTipoVisual());
        assertNull(encontradoPersistido.getTipoVisual());
        assertEquals(1, coincidencias.count());
        assertEquals(1, notificaciones.count());
        assertEquals(EstadoObjeto.COINCIDENCIA_DETECTADA, perdidoPersistido.getEstado());
    }

    private Usuario usuario(String correo, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Prueba HU33");
        usuario.setCorreo(correo);
        usuario.setPassword("solo-prueba");
        usuario.setRol(rol);
        return usuarios.save(usuario);
    }

    private RegistroObjetoPerdidoForm formPerdido() {
        RegistroObjetoPerdidoForm form = new RegistroObjetoPerdidoForm();
        form.setNombre("Laptop gris");
        form.setCategoria(CategoriaObjeto.TECNOLOGIA);
        form.setUbicacion(UbicacionCampus.A2);
        form.setDescripcion("Laptop gris con funda negra");
        return form;
    }

    private RegistroObjetoEncontradoForm formEncontrado() {
        RegistroObjetoEncontradoForm form = new RegistroObjetoEncontradoForm();
        form.setNombre("Laptop gris");
        form.setCategoria(CategoriaObjeto.TECNOLOGIA);
        form.setUbicacion(UbicacionCampus.A2);
        form.setDescripcion("Laptop gris con funda negra");
        return form;
    }

    private MockMultipartFile fotoLaptop() throws Exception {
        try (InputStream archivo = getClass().getResourceAsStream("/static/fotos/laptop-gris.jpg")) {
            assertNotNull(archivo);
            return new MockMultipartFile("imagen", "laptop-gris.jpg", "image/jpeg", archivo.readAllBytes());
        }
    }
}
