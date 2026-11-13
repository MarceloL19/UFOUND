package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.EstadoObjeto;
import pe.edu.ulima.ufound.model.NivelCoincidencia;
import pe.edu.ulima.ufound.model.Notificacion;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.NotificacionRepository;
import pe.edu.ulima.ufound.repository.ObjetoEncontradoRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CoincidenciaServiceTest {

    private Escenario escenario(int puntaje) {
        CoincidenciaRepository coincidencias = mock(CoincidenciaRepository.class);
        NotificacionRepository notificaciones = mock(NotificacionRepository.class);
        ObjetoEncontradoRepository encontrados = mock(ObjetoEncontradoRepository.class);
        ObjetoPerdidoRepository perdidos = mock(ObjetoPerdidoRepository.class);
        EstadoObjetoService estados = mock(EstadoObjetoService.class);
        CalculadoraCoincidencia calculadora = mock(CalculadoraCoincidencia.class);
        ObjetoPerdido perdido = new ObjetoPerdido();
        perdido.setIdObjeto(1L);
        perdido.setNombre("Mochila");
        perdido.setUsuario(new Usuario());
        ObjetoEncontrado encontrado = new ObjetoEncontrado();
        encontrado.setIdObjeto(2L);
        when(encontrados.findAll()).thenReturn(List.of(encontrado));
        when(calculadora.calcular(perdido, encontrado)).thenReturn(puntaje);
        when(coincidencias.findByObjetoPerdidoAndObjetoEncontrado(perdido, encontrado))
                .thenReturn(Optional.empty());
        when(coincidencias.save(any(Coincidencia.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(notificaciones.findByUsuarioAndCoincidencia(any(), any())).thenReturn(Optional.empty());
        CoincidenciaService servicio = new CoincidenciaService(
                coincidencias, notificaciones, perdidos, encontrados, estados, calculadora);
        return new Escenario(servicio, perdido, coincidencias, notificaciones, estados);
    }

    @Test
    void respetaLosUmbrales39_40_59_60() {
        for (int puntaje : new int[]{39, 40, 59, 60}) {
            Escenario e = escenario(puntaje);
            e.servicio.detectarParaObjetoPerdido(e.perdido);
            if (puntaje < 40) {
                verify(e.coincidencias, never()).save(any());
            } else {
                verify(e.coincidencias).save(argThat(coincidencia ->
                        coincidencia.getPorcentajeSimilitud() == puntaje
                                && coincidencia.getNivelCoincidencia() ==
                                (puntaje >= 60 ? NivelCoincidencia.MEDIO : NivelCoincidencia.BAJO)));
            }
            if (puntaje < 60) {
                verify(e.notificaciones, never()).save(any());
                verifyNoInteractions(e.estados);
            } else {
                verify(e.notificaciones).save(any(Notificacion.class));
                verify(e.estados).cambiarEstado(any(), any(),
                        eq(EstadoObjeto.COINCIDENCIA_DETECTADA), any());
            }
        }
    }

    @Test
    void revaluarLaMismaParejaNoDuplicaCoincidenciaNiNotificacion() {
        CoincidenciaRepository coincidencias = mock(CoincidenciaRepository.class);
        NotificacionRepository notificaciones = mock(NotificacionRepository.class);
        ObjetoEncontradoRepository encontrados = mock(ObjetoEncontradoRepository.class);
        EstadoObjetoService estados = mock(EstadoObjetoService.class);
        CalculadoraCoincidencia calculadora = mock(CalculadoraCoincidencia.class);
        ObjetoPerdido perdido = new ObjetoPerdido();
        perdido.setIdObjeto(1L);
        perdido.setNombre("Laptop");
        perdido.setUsuario(new Usuario());
        ObjetoEncontrado encontrado = new ObjetoEncontrado();
        encontrado.setIdObjeto(2L);
        AtomicReference<Coincidencia> guardada = new AtomicReference<>();
        AtomicReference<Notificacion> aviso = new AtomicReference<>();
        when(encontrados.findAll()).thenReturn(List.of(encontrado));
        when(calculadora.calcular(perdido, encontrado)).thenReturn(70);
        when(coincidencias.findByObjetoPerdidoAndObjetoEncontrado(perdido, encontrado))
                .thenAnswer(invocacion -> Optional.ofNullable(guardada.get()));
        when(coincidencias.save(any(Coincidencia.class))).thenAnswer(invocacion -> {
            Coincidencia coincidencia = invocacion.getArgument(0);
            guardada.set(coincidencia);
            return coincidencia;
        });
        when(notificaciones.findByUsuarioAndCoincidencia(any(), any()))
                .thenAnswer(invocacion -> Optional.ofNullable(aviso.get()));
        when(notificaciones.save(any(Notificacion.class))).thenAnswer(invocacion -> {
            Notificacion notificacion = invocacion.getArgument(0);
            aviso.set(notificacion);
            return notificacion;
        });
        doAnswer(invocacion -> {
            perdido.setEstado(EstadoObjeto.COINCIDENCIA_DETECTADA);
            return null;
        }).when(estados).cambiarEstado(any(), any(), any(), any());
        CoincidenciaService servicio = new CoincidenciaService(
                coincidencias, notificaciones, mock(ObjetoPerdidoRepository.class),
                encontrados, estados, calculadora);

        servicio.detectarParaObjetoPerdido(perdido);
        Coincidencia primera = guardada.get();
        servicio.detectarParaObjetoPerdido(perdido);

        assertSame(primera, guardada.get());
        verify(notificaciones, times(1)).save(any(Notificacion.class));
        verify(estados, times(1)).cambiarEstado(any(), any(), any(), any());
    }

    private record Escenario(CoincidenciaService servicio, ObjetoPerdido perdido,
                             CoincidenciaRepository coincidencias, NotificacionRepository notificaciones,
                             EstadoObjetoService estados) {}
}
