
package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadisticasPerdidasServiceTest {

    @Mock
    private ObjetoPerdidoRepository repositorio;

    @InjectMocks
    private EstadisticasPerdidasService servicio;

    /**
     * CA2: Si no existen objetos perdidos,
     * debe retornar un estado vacío sin errores.
     */
    @Test
    void debeMostrarEstadoVacioCuandoNoExistenReportes() {

        when(repositorio.count()).thenReturn(0L);

        EstadisticasPerdidasService.ResumenEstadistico resultado = servicio.obtenerResumen();

        assertTrue(resultado.sinDatos());

        assertEquals(0L, resultado.totalHistorico());
        assertEquals(0L, resultado.totalMesActual());
        assertEquals(0L, resultado.totalMesAnterior());

        assertEquals("Sin cambios",
                resultado.variacionMensual());

        assertEquals(6, resultado.tendenciaMensual().size());

        assertTrue(resultado.tendenciaMensual()
                .stream()
                .allMatch(mes -> mes.total() == 0L));

        assertTrue(resultado.categorias().isEmpty());
        assertTrue(resultado.ubicaciones().isEmpty());

        assertFalse(resultado.hallazgos().isEmpty());

        verify(repositorio, never()).findAll();
    }

    /**
     * CA1: Las estadísticas deben calcularse
     * exclusivamente con reportes de objetos perdidos.
     *
     * Datos simulados:
     * 10 perdidos, 6 de Tecnología y 4 Documentos.
     * 7 reportados en Biblioteca y 3 en A1.
     */
    @Test
    void debeCalcularEstadisticasConDatosDePerdidas() {

        when(repositorio.count()).thenReturn(10L);

        when(repositorio.contarPorCategoria())
                .thenReturn(List.of(
                        new Object[] { CategoriaObjeto.TECNOLOGIA, 6L },
                        new Object[] { CategoriaObjeto.DOCUMENTOS, 4L }));

        when(repositorio.contarPorUbicacion())
                .thenReturn(List.of(
                        new Object[] { UbicacionCampus.BIBLIOTECA, 7L },
                        new Object[] { UbicacionCampus.A1, 3L }));

        LocalDate inicioMes = LocalDate
                .now(ZoneId.of("America/Lima"))
                .withDayOfMonth(1);

        LocalDateTime actual = inicioMes.atStartOfDay();
        LocalDateTime anterior = inicioMes
                .minusMonths(1)
                .atStartOfDay();

        when(repositorio
                .countByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)))
                .thenAnswer(invocacion -> {

                    LocalDateTime desde = invocacion.getArgument(0);

                    if (desde.equals(actual)) {
                        return 4L;
                    }

                    if (desde.equals(anterior)) {
                        return 2L;
                    }

                    return 0L;
                });

        EstadisticasPerdidasService.ResumenEstadistico resultado = servicio.obtenerResumen();

        assertFalse(resultado.sinDatos());

        assertEquals(10L, resultado.totalHistorico());
        assertEquals(4L, resultado.totalMesActual());
        assertEquals(2L, resultado.totalMesAnterior());

        assertTrue(resultado.variacionMensual()
                .startsWith("+100"));

        assertEquals(6, resultado.tendenciaMensual().size());

        assertEquals(4L, resultado.tendenciaMensual()
                .get(5).total());

        assertEquals(2L, resultado.tendenciaMensual()
                .get(4).total());

        assertEquals("Tecnologia",
                resultado.categorias().get(0).nombre());

        assertEquals(6L,
                resultado.categorias().get(0).total());

        assertEquals(60,
                resultado.categorias().get(0).porcentaje());

        assertEquals("Biblioteca",
                resultado.ubicaciones().get(0).nombre());

        assertEquals(7L,
                resultado.ubicaciones().get(0).total());

        assertEquals(70,
                resultado.ubicaciones().get(0).porcentaje());

        assertEquals(3, resultado.hallazgos().size());

        verify(repositorio, never()).findAll();
    }

    /**
     * Verifica que no se genere una división entre cero
     * cuando el mes anterior no tiene reportes.
     */
    @Test
    void debeManejarMesAnteriorSinRegistros() {

        when(repositorio.count()).thenReturn(3L);

        LocalDateTime inicioActual = LocalDate
                .now(ZoneId.of("America/Lima"))
                .withDayOfMonth(1)
                .atStartOfDay();

        when(repositorio
                .countByFechaHoraGreaterThanEqualAndFechaHoraLessThan(
                        any(LocalDateTime.class),
                        any(LocalDateTime.class)))
                .thenAnswer(invocacion -> {

                    LocalDateTime desde = invocacion.getArgument(0);

                    return desde.equals(inicioActual) ? 3L : 0L;
                });

        EstadisticasPerdidasService.ResumenEstadistico resultado = servicio.obtenerResumen();

        assertEquals(3L, resultado.totalMesActual());
        assertEquals(0L, resultado.totalMesAnterior());

        assertEquals(
                "Sin base comparable",
                resultado.variacionMensual());

        assertFalse(resultado.sinDatos());
    }
}
