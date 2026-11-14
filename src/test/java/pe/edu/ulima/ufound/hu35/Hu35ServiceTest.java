package pe.edu.ulima.ufound.hu35;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;
import pe.edu.ulima.ufound.service.RegistroObjetoException;

import java.time.LocalDateTime;
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
        service = new Hu35Service(objetoPerdidoRepository, coincidenciaRepository);
    }

    @Test
    void devuelveElReporteYLasCoincidencias() {
        Usuario estudiante = usuario(7L);
        ObjetoPerdido perdido = objetoPerdido(21L, estudiante);
        Coincidencia coincidencia = new Coincidencia();

        when(objetoPerdidoRepository.findById(21L)).thenReturn(Optional.of(perdido));
        when(coincidenciaRepository.findByObjetoPerdido(perdido))
                .thenReturn(List.of(coincidencia));

        DetalleObjetoPerdidoHu35 resultado = service.obtenerDetalleConSugerencias(21L, estudiante);

        assertSame(perdido, resultado.objeto());
        assertEquals(1, resultado.coincidencias().size());
        assertSame(coincidencia, resultado.coincidencias().get(0));
        verify(coincidenciaRepository).findByObjetoPerdido(perdido);
    }

    @Test
    void ordenaPorPuntajeFechaEIdAunqueElRepositorioDevuelvaOtroOrden() {
        Usuario estudiante = usuario(7L);
        ObjetoPerdido perdido = objetoPerdido(21L, estudiante);
        LocalDateTime ayer = LocalDateTime.of(2026, 11, 9, 10, 0);
        LocalDateTime hoy = LocalDateTime.of(2026, 11, 10, 10, 0);
        Coincidencia baja = coincidencia(5L, 40, hoy);
        Coincidencia empateIdMenor = coincidencia(2L, 80, hoy);
        Coincidencia masReciente = coincidencia(4L, 80, hoy);
        Coincidencia masAntigua = coincidencia(3L, 80, ayer);
        Coincidencia mayorPuntaje = coincidencia(1L, 95, ayer);

        when(objetoPerdidoRepository.findById(21L)).thenReturn(Optional.of(perdido));
        when(coincidenciaRepository.findByObjetoPerdido(perdido))
                .thenReturn(List.of(baja, empateIdMenor, masAntigua, masReciente, mayorPuntaje));

        List<Long> ids = service.obtenerDetalleConSugerencias(21L, estudiante).coincidencias().stream()
                .map(Coincidencia::getIdCoincidencia).toList();

        assertEquals(List.of(1L, 4L, 2L, 3L, 5L), ids);
    }

    @Test
    void devuelveListaVaciaCuandoElReporteNoTieneCoincidencias() {
        Usuario estudiante = usuario(7L);
        ObjetoPerdido perdido = objetoPerdido(21L, estudiante);

        when(objetoPerdidoRepository.findById(21L)).thenReturn(Optional.of(perdido));
        when(coincidenciaRepository.findByObjetoPerdido(perdido))
                .thenReturn(List.of());

        DetalleObjetoPerdidoHu35 resultado = service.obtenerDetalleConSugerencias(21L, estudiante);

        assertNotNull(resultado.coincidencias());
        assertTrue(resultado.coincidencias().isEmpty());
    }

    @Test
    void impideConsultarElReporteDeOtroEstudiante() {
        Usuario propietario = usuario(7L);
        Usuario otroEstudiante = usuario(8L);
        ObjetoPerdido perdido = objetoPerdido(21L, propietario);
        when(objetoPerdidoRepository.findById(21L)).thenReturn(Optional.of(perdido));

        assertThrows(RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(21L, otroEstudiante));

        verifyNoInteractions(coincidenciaRepository);
    }

    @Test
    void rechazaUnReporteInexistente() {
        Usuario estudiante = usuario(7L);
        when(objetoPerdidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RegistroObjetoException.class,
                () -> service.obtenerDetalleConSugerencias(99L, estudiante));

        verifyNoInteractions(coincidenciaRepository);
    }

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        return usuario;
    }

    private ObjetoPerdido objetoPerdido(Long id, Usuario propietario) {
        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setIdObjeto(id);
        objeto.setUsuario(propietario);
        return objeto;
    }

    private Coincidencia coincidencia(Long id, int puntaje, LocalDateTime fecha) {
        Coincidencia coincidencia = new Coincidencia();
        coincidencia.setIdCoincidencia(id);
        coincidencia.setPorcentajeSimilitud(puntaje);
        coincidencia.setFechaDeteccion(fecha);
        return coincidencia;
    }
}
