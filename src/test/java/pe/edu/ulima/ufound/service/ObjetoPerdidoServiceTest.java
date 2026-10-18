
package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.RegistroObjetoPerdidoForm;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.model.Usuario;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ObjetoPerdidoServiceTest {

    private ObjetoPerdidoRepository repositorio;
    private UploadService uploadService;
    private EstadoObjetoService estadoService;
    private CoincidenciaService coincidenciaService;
    private ObjetoPerdidoService servicio;

    @BeforeEach
    void configurar() {
        repositorio = mock(ObjetoPerdidoRepository.class);
        uploadService = mock(UploadService.class);
        estadoService = mock(EstadoObjetoService.class);
        coincidenciaService = mock(CoincidenciaService.class);

        ImagenAnalisisService analizador = new ImagenAnalisisService();

        servicio = new ObjetoPerdidoService(
                repositorio,
                uploadService,
                estadoService,
                coincidenciaService,
                analizador);
    }

    private RegistroObjetoPerdidoForm crearFormulario() {
        RegistroObjetoPerdidoForm form = new RegistroObjetoPerdidoForm();

        form.setNombre("Mochila negra");
        form.setCategoria(CategoriaObjeto.ACCESORIOS);
        form.setUbicacion(UbicacionCampus.A2);
        form.setDescripcion(
                "Mochila negra con compartimento para laptop.");

        return form;
    }

    private void simularGuardado() {
        when(repositorio.save(any(ObjetoPerdido.class)))
                .thenAnswer(invocacion -> {
                    ObjetoPerdido objeto = invocacion.getArgument(0);

                    objeto.setIdObjeto(42L);
                    return objeto;
                });
    }

    @Test
    void debeRechazarImagenDanadaAntesDeGuardar() {
        MockMultipartFile imagenDanada = new MockMultipartFile(
                "imagen",
                "fotografia.png",
                "image/png",
                "Este archivo no es una imagen"
                        .getBytes(StandardCharsets.UTF_8));

        assertThrows(
                RegistroObjetoException.class,
                () -> servicio.registrar(
                        crearFormulario(),
                        imagenDanada,
                        new Usuario()));

        verifyNoInteractions(
                repositorio,
                uploadService,
                estadoService,
                coincidenciaService);
    }

    @Test
    void debePermitirRegistroManualSinFotografia() {
        simularGuardado();

        Usuario estudiante = new Usuario();

        ObjetoPerdido guardado = servicio.registrar(
                crearFormulario(),
                null,
                estudiante);

        assertNotNull(guardado);
        assertEquals(42L, guardado.getIdObjeto());
        assertEquals("Mochila negra", guardado.getNombre());
        assertNull(guardado.getImagenUrl());
        assertSame(estudiante, guardado.getUsuario());

        verify(uploadService)
                .guardarImagenObjetoPerdido(isNull());

        verify(repositorio)
                .save(any(ObjetoPerdido.class));

        verify(estadoService)
                .registrarEstadoInicial(
                        any(),
                        eq(42L),
                        any(),
                        same(estudiante));

        verify(coincidenciaService)
                .detectarParaObjetoPerdido(same(guardado));
    }

    @Test
    void debePermitirRegistroConImagenValida()
            throws IOException {

        BufferedImage imagenReal = new BufferedImage(
                2, 2,
                BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        assertTrue(ImageIO.write(
                imagenReal,
                "png",
                salida));

        MockMultipartFile imagenValida = new MockMultipartFile(
                "imagen",
                "mochila.png",
                "image/png",
                salida.toByteArray());

        when(uploadService.guardarImagenObjetoPerdido(
                same(imagenValida))).thenReturn(
                        "/uploads/objetos-perdidos/mochila.png");

        simularGuardado();

        ObjetoPerdido guardado = servicio.registrar(
                crearFormulario(),
                imagenValida,
                new Usuario());

        assertNotNull(guardado);
        assertEquals(
                "/uploads/objetos-perdidos/mochila.png",
                guardado.getImagenUrl());

        verify(uploadService)
                .guardarImagenObjetoPerdido(
                        same(imagenValida));

        verify(repositorio)
                .save(any(ObjetoPerdido.class));
    }
}
