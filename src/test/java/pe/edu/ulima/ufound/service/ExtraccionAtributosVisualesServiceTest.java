package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import pe.edu.ulima.ufound.model.AtributosVisuales;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExtraccionAtributosVisualesServiceTest {

    private ImagenAnalisisService validador;
    private DeteccionVisualService detector;
    private ExtraccionAtributosVisualesService servicio;
    private MockMultipartFile imagen;

    private final byte[] datos = { 1, 2, 3 };

    @BeforeEach
    void configurar() {
        validador = mock(ImagenAnalisisService.class);
        detector = mock(DeteccionVisualService.class);

        servicio = new ExtraccionAtributosVisualesService(
                validador, detector);

        imagen = new MockMultipartFile(
                "imagen", "objeto.png", "image/png", datos);

        // Esta prueba aísla la extracción.
        // La validación real de imágenes tiene sus propias pruebas.
        when(validador.validarImagen(imagen))
                .thenReturn(new ImagenAnalisisService.ResultadoImagen(
                        "LISTA_PARA_ANALISIS",
                        "PNG",
                        datos.length,
                        datos));
    }

    @Test
    void sinImagenNoInvocaValidacionNiDetector() {
        MockMultipartFile vacia = new MockMultipartFile(
                "imagen", new byte[0]);

        assertNull(servicio.extraer(null));
        assertNull(servicio.extraer(vacia));

        verifyNoInteractions(validador, detector);
    }

    @Test
    void conservaTipoColorYConfianzaDelDetector() throws Exception {
        when(detector.analizar(datos))
                .thenReturn(new DeteccionVisualService.Resultado(
                        "SUGERENCIA",
                        " Maleta ",
                        " Negro ",
                        0.92,
                        true));

        AtributosVisuales resultado = servicio.extraer(imagen);

        assertNotNull(resultado);
        assertEquals("Maleta", resultado.getTipo());
        assertEquals("Negro", resultado.getColor());
        assertEquals(0.92, resultado.getConfianza().doubleValue());

        var orden = inOrder(validador, detector);
        orden.verify(validador).validarImagen(imagen);
        orden.verify(detector).analizar(datos);
    }

    @Test
    void sinDeteccionNoGeneraAtributos() throws Exception {
        when(detector.analizar(datos))
                .thenReturn(new DeteccionVisualService.Resultado(
                        "SIN_DETECCION", null, null, 0, true));

        assertNull(servicio.extraer(imagen));
    }

    @Test
    void falloDelDetectorPermiteContinuarSinAtributos() throws Exception {
        when(detector.analizar(datos))
                .thenThrow(new IllegalStateException(
                        "Fallo simulado del detector"));

        assertNull(servicio.extraer(imagen));
    }

    @Test
    void imagenInvalidaSeRechazaAntesDeInvocarDetector() {
        when(validador.validarImagen(imagen))
                .thenThrow(new RegistroObjetoException(
                        "Imagen inválida"));

        assertThrows(
                RegistroObjetoException.class,
                () -> servicio.extraer(imagen));

        verifyNoInteractions(detector);
    }

    @Test
    void tipoValidoSinColorConservaSoloLoDisponible() throws Exception {
        when(detector.analizar(datos))
                .thenReturn(new DeteccionVisualService.Resultado(
                        "SUGERENCIA", "Maleta", " ", 0.90, true));

        AtributosVisuales resultado = servicio.extraer(imagen);

        assertNotNull(resultado);
        assertEquals("Maleta", resultado.getTipo());
        assertNull(resultado.getColor());
        assertEquals(0.90, resultado.getConfianza().doubleValue());
    }

    @Test
    void confianzaInvalidaNoGeneraAtributos() throws Exception {
        double[] confianzas = {
                -0.10,
                0.349,
                1.01,
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        };

        for (double confianza : confianzas) {
            when(detector.analizar(datos))
                    .thenReturn(new DeteccionVisualService.Resultado(
                            "SUGERENCIA",
                            "Maleta",
                            "Negro",
                            confianza,
                            true));

            assertNull(
                    servicio.extraer(imagen),
                    "No debe aceptar confianza: " + confianza);
        }
    }

    @Test
    void respuestaNulaDelDetectorNoGeneraAtributos() throws Exception {
        when(detector.analizar(datos)).thenReturn(null);

        assertNull(servicio.extraer(imagen));
    }
}