package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import pe.edu.ulima.ufound.model.*;
import pe.edu.ulima.ufound.repository.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegistroAtributosVisualesTest {

    @Test
    void registroPerdidoConservaAtributosYDatosManuales() {
        comprobarPerdido(new AtributosVisuales("Maleta", "Negro", 0.92));
    }

    @Test
    void registroEncontradoConservaAtributosYDatosManuales() {
        comprobarEncontrado(new AtributosVisuales("Maleta", "Negro", 0.92));
    }

    @Test
    void registroPerdidoContinuaSinAtributosVisuales() {
        comprobarPerdido(null);
    }

    @Test
    void registroEncontradoContinuaSinAtributosVisuales() {
        comprobarEncontrado(null);
    }

    private void comprobarPerdido(AtributosVisuales atributos) {
        ObjetoPerdidoRepository repositorio = mock(ObjetoPerdidoRepository.class);
        UploadService uploads = mock(UploadService.class);
        EstadoObjetoService estados = mock(EstadoObjetoService.class);
        CoincidenciaService coincidencias = mock(CoincidenciaService.class);
        ExtraccionAtributosVisualesService extractor = mock(ExtraccionAtributosVisualesService.class);

        MockMultipartFile imagen = imagen();
        when(extractor.extraer(imagen)).thenReturn(atributos);

        when(repositorio.save(any(ObjetoPerdido.class)))
                .thenAnswer(invocacion -> {
                    ObjetoPerdido objeto = invocacion.getArgument(0);

                    // Comprueba los atributos cuando se solicita guardar.
                    assertSame(atributos, objeto.getAtributosVisuales());
                    objeto.setIdObjeto(101L);
                    return objeto;
                });

        RegistroObjetoPerdidoForm form = new RegistroObjetoPerdidoForm();
        form.setNombre("Título manual");
        form.setCategoria(CategoriaObjeto.UTILES);
        form.setUbicacion(UbicacionCampus.A2);
        form.setDescripcion("Descripción manual");

        ObjetoPerdidoService servicio = new ObjetoPerdidoService(
                repositorio, uploads, estados, coincidencias, extractor);

        ObjetoPerdido guardado = servicio.registrar(form, imagen, new Usuario());

        assertSame(atributos, guardado.getAtributosVisuales());
        assertEquals("Título manual", guardado.getNombre());
        assertEquals(CategoriaObjeto.UTILES, guardado.getCategoria());
        assertEquals(UbicacionCampus.A2, guardado.getUbicacion());
        assertEquals("Descripción manual", guardado.getDescripcion());

        var orden = inOrder(extractor, repositorio, coincidencias);
        orden.verify(extractor).extraer(imagen);
        orden.verify(repositorio).save(same(guardado));
        orden.verify(coincidencias)
                .detectarParaObjetoPerdido(same(guardado));
    }

    private void comprobarEncontrado(AtributosVisuales atributos) {
        ObjetoEncontradoRepository repositorio = mock(ObjetoEncontradoRepository.class);
        UploadService uploads = mock(UploadService.class);
        EstadoObjetoService estados = mock(EstadoObjetoService.class);
        CoincidenciaService coincidencias = mock(CoincidenciaService.class);
        ExtraccionAtributosVisualesService extractor = mock(ExtraccionAtributosVisualesService.class);

        MockMultipartFile imagen = imagen();
        when(extractor.extraer(imagen)).thenReturn(atributos);

        when(repositorio.save(any(ObjetoEncontrado.class)))
                .thenAnswer(invocacion -> {
                    ObjetoEncontrado objeto = invocacion.getArgument(0);

                    assertSame(atributos, objeto.getAtributosVisuales());
                    objeto.setIdObjeto(102L);
                    return objeto;
                });

        RegistroObjetoEncontradoForm form = new RegistroObjetoEncontradoForm();
        form.setNombre("Título manual");
        form.setCategoria(CategoriaObjeto.UTILES);
        form.setUbicacion(UbicacionCampus.A2);
        form.setDescripcion("Descripción manual");

        ObjetoEncontradoService servicio = new ObjetoEncontradoService(
                repositorio, uploads, estados, coincidencias, extractor);

        ObjetoEncontrado guardado = servicio.registrar(form, imagen, new Usuario());

        assertSame(atributos, guardado.getAtributosVisuales());
        assertEquals("Título manual", guardado.getNombre());
        assertEquals(CategoriaObjeto.UTILES, guardado.getCategoria());
        assertEquals(UbicacionCampus.A2, guardado.getUbicacion());
        assertEquals("Descripción manual", guardado.getDescripcion());

        var orden = inOrder(extractor, repositorio, coincidencias);
        orden.verify(extractor).extraer(imagen);
        orden.verify(repositorio).save(same(guardado));
        orden.verify(coincidencias)
                .detectarParaObjetoEncontrado(same(guardado));
    }

    private MockMultipartFile imagen() {
        // El extractor está simulado; aquí no se valida la imagen real.
        return new MockMultipartFile(
                "imagen",
                "objeto.png",
                "image/png",
                new byte[] { 1, 2, 3 });
    }
}