package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AtributosVisualesServiceTest {

    private final DeteccionVisualService detector = mock(DeteccionVisualService.class);
    private final AtributosVisualesService servicio = new AtributosVisualesService(detector);
    private final MockMultipartFile imagen = new MockMultipartFile(
            "imagen", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3});

    @Test
    void conservaSoloUnaDeteccionValidaDelServidor() throws Exception {
        when(detector.analizar(any())).thenReturn(
                new DeteccionVisualService.Resultado("SUGERENCIA", "Laptop", "Gris", .7, true));
        var atributos = servicio.extraer(imagen).orElseThrow();
        assertEquals("Laptop", atributos.tipo());
        assertEquals("Gris", atributos.color());
        assertEquals(.7, atributos.confianza());
    }

    @Test
    void sinFotoNoInvocaElModelo() {
        assertTrue(servicio.extraer(null).isEmpty());
        verifyNoInteractions(detector);
    }

    @Test
    void noConservaUnaDeteccionInciertaONoSoportada() throws Exception {
        when(detector.analizar(any())).thenReturn(
                new DeteccionVisualService.Resultado("SUGERENCIA", "Laptop", "Gris", .34, true),
                new DeteccionVisualService.Resultado("SUGERENCIA", "Audifonos", "Negro", .9, true),
                new DeteccionVisualService.Resultado("SIN_DETECCION", null, null, 0, true));
        assertTrue(servicio.extraer(imagen).isEmpty());
        assertTrue(servicio.extraer(imagen).isEmpty());
        assertTrue(servicio.extraer(imagen).isEmpty());
    }

    @Test
    void falloDelDetectorPermiteElRegistroManual() throws Exception {
        when(detector.analizar(any())).thenThrow(new IllegalStateException("modelo no disponible"));
        assertTrue(servicio.extraer(imagen).isEmpty());
    }
}
