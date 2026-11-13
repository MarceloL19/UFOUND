package pe.edu.ulima.ufound.hu35;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Hu35ServiceTest {

    private ObjetoPerdidoRepository objetoPerdidoRepository;
    private CoincidenciaRepository coincidenciaRepository;
    private Hu35Service service;

    @BeforeEach
    void setUp() {
        objetoPerdidoRepository = mock(ObjetoPerdidoRepository.class);
        coincidenciaRepository = mock(CoincidenciaRepository.class);

        service = new Hu35Service(
                objetoPerdidoRepository,
                coincidenciaRepository
        );
    }

    @Test
    void devuelveElReporteYLasCoincidenciasOrdenadasPorElRepositorio() {
        Usuario estudiante = usuario(7L);
        ObjetoPerdido perdido = objetoPerdido(21L, estudiante);
        Coincidencia coincidencia = new Coincidencia();

        when(objetoPerdidoRepository.findById(21L))
                .thenReturn(Optional.of(perdido));

        when(coincidenciaRepository
                .findByObjetoPerdidoOrderByPorcentajeSimilitudDescFechaDeteccionDesc(perdido))
                .thenReturn(List.of(coincidencia));

        DetalleObjetoPerdidoHu35 resultado =
                service.obtenerDetalleConSugerencias(21L, estudiante);

        assertSame(perdido, resultado.objeto());
        assertEquals(1, resultado.coincidencias().size());
        assertSame(coincidencia, resultado.coincidencias().get(0));

        verify(objetoPerdidoRepository).findById(21L);

        verify(coincidenciaRepository)
                .findByObjetoPerdidoOrderByPorcentajeSimilitudDescFechaDeteccionDesc(
                        perdido
                );

        verifyNoMoreInteractions(objetoPerdidoRepository);
        verifyNoMoreInteractions(coincidenciaRepository);
    }

    @Test
    void devuelveListaVaciaCuandoElReporteNoTieneCoincidencias() {
        Usuario estudiante = usuario(7L);
        ObjetoPerdido perdido = objetoPerdido(21L, estudiante);

        when(objetoPerdidoRepository.findById(21L))
                .thenReturn(Optional.of(perdido));

        when(coincidenciaRepository
                .findByObjetoPerdidoOrderByPorcentajeSimilitudDescFechaDeteccionDesc(perdido))
                .thenReturn(List.of());

        DetalleObjetoPerdidoHu35 resultado =
                service.obtenerDetalleConSugerencias(21L, estudiante);

        assertNotNull(resultado.coincidencias());
        assertTrue(resultado.coincidencias().isEmpty());

        verify(coincidenciaRepository)
                .findByObjetoPerdidoOrderByPorcentajeSimilitudDescFechaDeteccionDesc(
                        perdido
                );
    }

    @Test
    void impideConsultarElReporteDeOtroEstudiante() {
        Usuario propietario = usuario(7L);
        Usuario otroEstudiante = usuario(8L);

        ObjetoPerdido perdido = objetoPerdido(21L, propietario);

        when(objetoPerdidoRepository.findById(21L))
                .thenReturn(Optional.of(perdido));

        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(21L, otroEstudiante)
        );

        verify(objetoPerdidoRepository).findById(21L);
        verifyNoInteractions(coincidenciaRepository);
    }

    @Test
    void rechazaUnReporteInexistente() {
        Usuario estudiante = usuario(7L);

        when(objetoPerdidoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(99L, estudiante)
        );

        verify(objetoPerdidoRepository).findById(99L);
        verifyNoInteractions(coincidenciaRepository);
    }

    @Test
    void rechazaReporteSinPropietario() {
        Usuario estudiante = usuario(7L);
        ObjetoPerdido perdido = objetoPerdido(21L, null);

        when(objetoPerdidoRepository.findById(21L))
                .thenReturn(Optional.of(perdido));

        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(21L, estudiante)
        );

        verify(objetoPerdidoRepository).findById(21L);
        verifyNoInteractions(coincidenciaRepository);
    }

    @Test
    void rechazaUsuarioNulo() {
        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(21L, null)
        );

        verifyNoInteractions(objetoPerdidoRepository);
        verifyNoInteractions(coincidenciaRepository);
    }

    @Test
    void rechazaIdNulo() {
        Usuario estudiante = usuario(7L);

        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(null, estudiante)
        );

        verifyNoInteractions(objetoPerdidoRepository);
        verifyNoInteractions(coincidenciaRepository);
    }

    @Test
    void rechazaUsuarioSinId() {
        Usuario estudiante = usuario(null);

        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(21L, estudiante)
        );

        verifyNoInteractions(objetoPerdidoRepository);
        verifyNoInteractions(coincidenciaRepository);
    }

    @Test
    void rechazaUsuarioQueNoEsEstudiante() {
        Usuario usuario = usuario(7L);
        usuario.setRol(Rol.SEGURIDAD);

        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(21L, usuario)
        );

        verifyNoInteractions(objetoPerdidoRepository);
        verifyNoInteractions(coincidenciaRepository);
    }

    @Test
    void rechazaIdMenorOIgualACero() {
        Usuario estudiante = usuario(7L);

        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(0L, estudiante)
        );

        assertThrows(
                RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(-1L, estudiante)
        );

        verifyNoInteractions(objetoPerdidoRepository);
        verifyNoInteractions(coincidenciaRepository);
    }

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setRol(Rol.ESTUDIANTE);
        return usuario;
    }

    private ObjetoPerdido objetoPerdido(Long id, Usuario propietario) {
        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setIdObjeto(id);
        objeto.setUsuario(propietario);
        return objeto;
    }
}

