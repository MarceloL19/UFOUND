package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.Coincidencia;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.CoincidenciaRepository;
import pe.edu.ulima.ufound.repository.NotificacionRepository;
import pe.edu.ulima.ufound.repository.ObjetoEncontradoRepository;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HU-34: las coincidencias se muestran de mayor a menor puntaje con un
 * desempate reproducible.
 */
class OrdenCoincidenciasHu34Test {

    private static final LocalDateTime AYER = LocalDateTime.of(2026, 11, 9, 10, 0);
    private static final LocalDateTime HOY = LocalDateTime.of(2026, 11, 10, 10, 0);

    @Test
    void ordenaDeMayorAMenorPuntaje() {
        List<Coincidencia> lista = listar(
                coincidencia(1L, 45, HOY), coincidencia(2L, 92, AYER), coincidencia(3L, 70, HOY));

        assertEquals(List.of(2L, 3L, 1L), ids(lista));
    }

    @Test
    void conIgualPuntajeVaPrimeroLaDetectadaMasRecientemente() {
        List<Coincidencia> lista = listar(coincidencia(1L, 80, AYER), coincidencia(2L, 80, HOY));

        assertEquals(List.of(2L, 1L), ids(lista));
    }

    @Test
    void conIgualPuntajeYFechaDesempataPorIdDeFormaEstable() {
        List<Coincidencia> lista = listar(
                coincidencia(4L, 80, HOY), coincidencia(9L, 80, HOY), coincidencia(6L, 80, HOY));

        assertEquals(List.of(9L, 6L, 4L), ids(lista));
    }

    @Test
    void elOrdenEsReproducibleSinImportarComoLlegueDeLaBaseDeDatos() {
        List<Coincidencia> datos = new ArrayList<>(List.of(
                coincidencia(1L, 60, AYER), coincidencia(2L, 60, HOY), coincidencia(3L, 60, HOY),
                coincidencia(4L, 95, AYER), coincidencia(5L, 40, HOY), coincidencia(6L, 95, AYER)));
        List<Long> esperado = List.of(6L, 4L, 3L, 2L, 1L, 5L);

        Random azar = new Random(34);
        for (int intento = 0; intento < 25; intento++) {
            Collections.shuffle(datos, azar);
            assertEquals(esperado, ids(listar(datos.toArray(Coincidencia[]::new))));
        }
    }

    @Test
    void elOrdenUsaElPuntajeVisualDeHu33() {
        ObjetoPerdido perdido = perdido();
        ObjetoEncontrado mochila = encontrado(10L, "Mochila gris", "Mochila gris con llavero",
                CategoriaObjeto.ACCESORIOS);
        ObjetoEncontrado candidatoDiscordante = encontrado(11L, perdido.getNombre(),
                perdido.getDescripcion(), CategoriaObjeto.TECNOLOGIA);
        ObjetoEncontrado candidatoCompatible = encontrado(12L, perdido.getNombre(),
                perdido.getDescripcion(), CategoriaObjeto.TECNOLOGIA);

        perdido.setImagenUrl("/pruebas/perdido.jpg");
        perdido.setTipoVisual("Laptop");
        perdido.setColorVisual("Gris");
        perdido.setConfianzaVisual(0.95);

        candidatoDiscordante.setImagenUrl("/pruebas/candidato-discordante.jpg");
        candidatoDiscordante.setTipoVisual("Mouse");
        candidatoDiscordante.setColorVisual("Negro");
        candidatoDiscordante.setConfianzaVisual(0.95);

        candidatoCompatible.setImagenUrl("/pruebas/candidato-compatible.jpg");
        candidatoCompatible.setTipoVisual("Laptop");
        candidatoCompatible.setColorVisual("Gris");
        candidatoCompatible.setConfianzaVisual(0.95);

        CalculadoraCoincidencia calculadora = new CalculadoraCoincidencia();
        assertEquals(100, calculadora.calcularBase(perdido, candidatoCompatible));
        assertEquals(100, calculadora.calcularBase(perdido, candidatoDiscordante));

        CoincidenciaRepository coincidencias = mock(CoincidenciaRepository.class);
        ObjetoEncontradoRepository encontrados = mock(ObjetoEncontradoRepository.class);
        NotificacionRepository notificaciones = mock(NotificacionRepository.class);
        List<Coincidencia> guardadas = new ArrayList<>();
        // El candidato discordante recibe un ID mayor y ganaría si ambos puntajes
        // siguieran empatados.
        when(encontrados.findAll()).thenReturn(List.of(mochila, candidatoCompatible, candidatoDiscordante));
        when(coincidencias.findByObjetoPerdidoAndObjetoEncontrado(any(), any())).thenReturn(Optional.empty());
        when(coincidencias.save(any(Coincidencia.class))).thenAnswer(invocacion -> {
            Coincidencia coincidencia = invocacion.getArgument(0);
            coincidencia.setIdCoincidencia((long) guardadas.size() + 1);
            coincidencia.setFechaDeteccion(HOY);
            guardadas.add(coincidencia);
            return coincidencia;
        });
        when(notificaciones.findByUsuarioAndCoincidencia(any(), any())).thenReturn(Optional.empty());
        when(coincidencias.findByObjetoPerdidoUsuario(perdido.getUsuario())).thenAnswer(invocacion -> guardadas);
        CoincidenciaService servicio = new CoincidenciaService(coincidencias, notificaciones,
                mock(ObjetoPerdidoRepository.class), encontrados, mock(EstadoObjetoService.class),
                calculadora);

        servicio.detectarParaObjetoPerdido(perdido);
        List<Coincidencia> lista = servicio.listarPorUsuario(perdido.getUsuario());

        assertEquals(List.of(candidatoCompatible, candidatoDiscordante, mochila),
                lista.stream().map(Coincidencia::getObjetoEncontrado).toList());
        assertEquals(List.of(100, 75, 46),
                lista.stream().map(Coincidencia::getPorcentajeSimilitud).toList());
    }

    @Test
    void sinCoincidenciasDevuelveListaVacia() {
        assertTrue(listar().isEmpty());
    }

    private List<Coincidencia> listar(Coincidencia... datos) {
        Usuario usuario = new Usuario();
        CoincidenciaRepository repositorio = mock(CoincidenciaRepository.class);
        when(repositorio.findByObjetoPerdidoUsuario(usuario)).thenReturn(List.of(datos));
        CoincidenciaService servicio = new CoincidenciaService(repositorio, mock(NotificacionRepository.class),
                mock(ObjetoPerdidoRepository.class), mock(ObjetoEncontradoRepository.class),
                mock(EstadoObjetoService.class), new CalculadoraCoincidencia());
        return servicio.listarPorUsuario(usuario);
    }

    private static Coincidencia coincidencia(Long id, int puntaje, LocalDateTime fecha) {
        Coincidencia coincidencia = new Coincidencia();
        coincidencia.setIdCoincidencia(id);
        coincidencia.setPorcentajeSimilitud(puntaje);
        coincidencia.setFechaDeteccion(fecha);
        return coincidencia;
    }

    private static List<Long> ids(List<Coincidencia> lista) {
        return lista.stream().map(Coincidencia::getIdCoincidencia).toList();
    }

    private static ObjetoPerdido perdido() {
        ObjetoPerdido perdido = new ObjetoPerdido();
        perdido.setIdObjeto(1L);
        perdido.setUsuario(new Usuario());
        perdido.setNombre("Laptop gris");
        perdido.setDescripcion("Laptop gris con stickers");
        perdido.setCategoria(CategoriaObjeto.TECNOLOGIA);
        perdido.setUbicacion(UbicacionCampus.A2);
        return perdido;
    }

    private static ObjetoEncontrado encontrado(Long id, String nombre, String descripcion, CategoriaObjeto categoria) {
        ObjetoEncontrado encontrado = new ObjetoEncontrado();
        encontrado.setIdObjeto(id);
        encontrado.setNombre(nombre);
        encontrado.setDescripcion(descripcion);
        encontrado.setCategoria(categoria);
        encontrado.setUbicacion(UbicacionCampus.A2);
        return encontrado;
    }
}
