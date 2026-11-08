package pe.edu.ulima.ufound.coincidencias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pe.edu.ulima.ufound.model.*;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.sql.init.mode=never"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class OrdenCoincidenciasHu34Test {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CoincidenciaRepository repositorio;

    private Usuario estudiante;
    private Usuario seguridad;
    private ObjetoPerdido perdido;

    private final LocalDateTime fecha = LocalDateTime.of(2026, 11, 8, 12, 0);

    @BeforeEach
    void preparar() {
        estudiante = usuario("estudiante", Rol.ESTUDIANTE);
        seguridad = usuario("seguridad", Rol.SEGURIDAD);
        perdido = objetoPerdido(estudiante);
    }

    @Test
    void ordenaPorPuntajeDescendenteAunqueLaFechaSeaDistinta() {
        Coincidencia media = coincidencia(
                perdido, 65, fecha.plusDays(1));
        Coincidencia alta = coincidencia(
                perdido, 95, fecha.minusDays(1));
        Coincidencia baja = coincidencia(
                perdido, 40, fecha.plusDays(2));

        assertThat(listarIds(estudiante))
                .containsExactly(
                        alta.getIdCoincidencia(),
                        media.getIdCoincidencia(),
                        baja.getIdCoincidencia());
    }

    @Test
    void igualPuntajePriorizaLaFechaMasReciente() {
        Coincidencia antigua = coincidencia(
                perdido, 80, fecha.minusDays(1));
        Coincidencia reciente = coincidencia(
                perdido, 80, fecha.plusDays(1));
        Coincidencia intermedia = coincidencia(
                perdido, 80, fecha);

        assertThat(listarIds(estudiante))
                .containsExactly(
                        reciente.getIdCoincidencia(),
                        intermedia.getIdCoincidencia(),
                        antigua.getIdCoincidencia());
    }

    @Test
    void igualPuntajeYFechaOrdenaPorIdAscendenteDeFormaRepetible() {
        Coincidencia primera = coincidencia(perdido, 80, fecha);
        Coincidencia segunda = coincidencia(perdido, 80, fecha);
        Coincidencia tercera = coincidencia(perdido, 80, fecha);

        List<Long> esperado = List.of(
                primera.getIdCoincidencia(),
                segunda.getIdCoincidencia(),
                tercera.getIdCoincidencia());

        assertThat(listarIds(estudiante)).isEqualTo(esperado);
        assertThat(listarIds(estudiante)).isEqualTo(esperado);
    }

    @Test
    void noIncluyeCoincidenciasDeOtroUsuario() {
        Usuario otro = usuario("otro", Rol.ESTUDIANTE);
        ObjetoPerdido perdidoAjeno = objetoPerdido(otro);

        Coincidencia propia = coincidencia(perdido, 60, fecha);
        coincidencia(perdidoAjeno, 100, fecha.plusDays(1));

        assertThat(listarIds(estudiante))
                .containsExactly(propia.getIdCoincidencia());
    }

    @Test
    void devuelveListaVaciaCuandoElUsuarioNoTieneCoincidencias() {
        Usuario otro = usuario("sinCoincidencias", Rol.ESTUDIANTE);
        coincidencia(perdido, 90, fecha);

        assertThat(listarIds(otro)).isEmpty();
    }

    private List<Long> listarIds(Usuario usuario) {
        Long idUsuario = usuario.getIdUsuario();

        em.flush();
        em.clear();

        Usuario referencia = em.getEntityManager()
                .getReference(Usuario.class, idUsuario);

        return repositorio
                .findByObjetoPerdidoUsuarioOrderByPorcentajeSimilitudDescFechaDeteccionDescIdCoincidenciaAsc(
                        referencia)
                .stream()
                .map(Coincidencia::getIdCoincidencia)
                .toList();
    }

    private Usuario usuario(String nombre, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(nombre + "@pruebas.invalid");
        usuario.setPassword("solo-pruebas");
        usuario.setRol(rol);

        return em.persist(usuario);
    }

    private ObjetoPerdido objetoPerdido(Usuario propietario) {
        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setNombre("Maleta perdida");
        objeto.setCategoria(CategoriaObjeto.ACCESORIOS);
        objeto.setUbicacion(UbicacionCampus.BIBLIOTECA);
        objeto.setDescripcion("Maleta negra de prueba.");
        objeto.setUsuario(propietario);

        return em.persist(objeto);
    }

    private Coincidencia coincidencia(
            ObjetoPerdido origen,
            int puntaje,
            LocalDateTime fechaDeteccion) {

        ObjetoEncontrado encontrado = new ObjetoEncontrado();
        encontrado.setNombre("Maleta encontrada");
        encontrado.setCategoria(CategoriaObjeto.ACCESORIOS);
        encontrado.setUbicacion(UbicacionCampus.BIBLIOTECA);
        encontrado.setDescripcion("Maleta negra de prueba.");
        encontrado.setUsuario(seguridad);
        em.persist(encontrado);

        Coincidencia coincidencia = new Coincidencia();
        coincidencia.setObjetoPerdido(origen);
        coincidencia.setObjetoEncontrado(encontrado);
        coincidencia.setPorcentajeSimilitud(puntaje);
        coincidencia.setFechaDeteccion(fechaDeteccion);
        coincidencia.setNivelCoincidencia(
                puntaje >= 80 ? NivelCoincidencia.ALTO
                        : puntaje >= 60 ? NivelCoincidencia.MEDIO
                                : NivelCoincidencia.BAJO);

        return em.persist(coincidencia);
    }
}