package pe.edu.ulima.ufound.analisis;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalisisPerdidasCalculadoraTest {

    private final AnalisisPerdidasCalculadora calculadora = new AnalisisPerdidasCalculadora();
    private final LocalDate hoy = LocalDate.of(2026, 10, 11);

    @Test
    void categoriasSeOrdenanPorFrecuenciaDeObjetosPerdidos() {
        AnalisisPerdidasVista vista = calculadora.analizar(List.of(
                perdido(CategoriaObjeto.TECNOLOGIA, UbicacionCampus.AULAS, LocalDateTime.of(2026, 10, 2, 8, 0)),
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 10, 3, 9, 0)),
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 10, 4, 10, 0)),
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.CAFETERIA, LocalDateTime.of(2026, 10, 5, 11, 0))
        ), "30", null, null, hoy);

        assertEquals("Ropa", vista.getCategorias().get(0).getEtiqueta());
        assertEquals(3, vista.getCategorias().get(0).getTotal());
        assertEquals("Tecnologia", vista.getCategorias().get(1).getEtiqueta());
        assertEquals(1, vista.getCategorias().get(1).getTotal());
        assertTrue(vista.getCategorias().get(1).getTotal() >= vista.getCategorias().get(2).getTotal());
        assertEquals("Ropa", vista.getCategoriaLider());
    }

    @Test
    void periodoUsaLasFechasDeLosReportesYSeIdentifica() {
        AnalisisPerdidasVista vista = calculadora.analizar(List.of(
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 10, 1, 9, 0)),
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 10, 10, 9, 0)),
                perdido(CategoriaObjeto.DOCUMENTOS, UbicacionCampus.CAFETERIA, LocalDateTime.of(2026, 8, 1, 9, 0))
        ), "30", null, null, hoy);

        assertEquals("30", vista.getPeriodo().getCodigo());
        assertTrue(vista.getPeriodo().getDescripcion().contains("12/09/2026"));
        assertTrue(vista.getPeriodo().getDescripcion().contains("11/10/2026"));
        assertTrue(vista.getPeriodo().getDescripcion().contains("Ultimos 30 dias"));
        assertEquals(2, vista.getTotalPeriodo());
        assertEquals(2, vista.getUbicaciones().get(0).getTotal());
        assertEquals("Biblioteca", vista.getUbicacionLider());
        assertEquals(2, vista.getTendencia().stream().mapToLong(PuntoTendencia::getTotal).sum());
    }

    @Test
    void comparaElPeriodoAnteriorConLaMismaDuracion() {
        AnalisisPerdidasVista vista = calculadora.analizar(List.of(
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 10, 1, 9, 0)),
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 10, 10, 9, 0)),
                perdido(CategoriaObjeto.TECNOLOGIA, UbicacionCampus.AULAS, LocalDateTime.of(2026, 9, 1, 9, 0)),
                perdido(CategoriaObjeto.ACCESORIOS, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 8, 20, 9, 0))
        ), "30", null, null, hoy);

        assertTrue(vista.getPeriodo().isComparable());
        assertTrue(vista.getPeriodo().getDescripcionAnterior().contains("13/08/2026"));
        assertTrue(vista.getPeriodo().getDescripcionAnterior().contains("11/09/2026"));
        assertEquals(2, vista.getTotalPeriodo());
        assertEquals(2, vista.getTotalPeriodoAnterior());

        FrecuenciaUbicacion biblioteca = vista.getUbicaciones().stream()
                .filter(ubicacion -> ubicacion.getEtiqueta().equals("Biblioteca"))
                .findFirst()
                .orElseThrow();
        assertEquals(2, biblioteca.getTotal());
        assertEquals(1, biblioteca.getTotalAnterior());
        assertEquals("+1", biblioteca.getVariacionTexto());

        FrecuenciaUbicacion aulas = vista.getUbicaciones().stream()
                .filter(ubicacion -> ubicacion.getEtiqueta().equals("Aulas"))
                .findFirst()
                .orElseThrow();
        assertEquals(0, aulas.getTotal());
        assertEquals(1, aulas.getTotalAnterior());
        assertEquals("-1", aulas.getVariacionTexto());
    }

    @Test
    void historicoNoInventaUnPeriodoAnterior() {
        AnalisisPerdidasVista vista = calculadora.analizar(List.of(
                perdido(CategoriaObjeto.UTILES, UbicacionCampus.LABORATORIOS, LocalDateTime.of(2026, 1, 15, 8, 0)),
                perdido(CategoriaObjeto.UTILES, UbicacionCampus.LABORATORIOS, LocalDateTime.of(2026, 10, 2, 8, 0))
        ), "historico", null, null, hoy);

        assertFalse(vista.getPeriodo().isComparable());
        assertNull(vista.getPeriodo().getLimiteInferior());
        assertTrue(vista.getPeriodo().getDescripcion().contains("15/01/2026"));
        assertTrue(vista.getPeriodo().getDescripcion().contains("Historico completo"));
        assertEquals(2, vista.getTotalPeriodo());
        assertEquals(0, vista.getTotalPeriodoAnterior());
    }

    @Test
    void periodoPersonalizadoRespetaLasFechasInclusivas() {
        AnalisisPerdidasVista vista = calculadora.analizar(List.of(
                perdido(CategoriaObjeto.BOTELLAS, UbicacionCampus.GIMNASIO_F1, LocalDateTime.of(2026, 10, 5, 0, 5)),
                perdido(CategoriaObjeto.BOTELLAS, UbicacionCampus.GIMNASIO_F1, LocalDateTime.of(2026, 10, 8, 23, 50)),
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.AULAS, LocalDateTime.of(2026, 10, 4, 23, 0)),
                perdido(CategoriaObjeto.ROPA, UbicacionCampus.AULAS, LocalDateTime.of(2026, 10, 9, 0, 1))
        ), "personalizado", LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 5), hoy);

        assertEquals("personalizado", vista.getPeriodo().getCodigo());
        assertEquals(LocalDate.of(2026, 10, 5), vista.getPeriodo().getInicio());
        assertEquals(LocalDate.of(2026, 10, 8), vista.getPeriodo().getFin());
        assertTrue(vista.getMensaje().contains("Se ordenaron"));
        assertEquals(2, vista.getTotalPeriodo());
        assertEquals(4, vista.getTendencia().size());
    }

    @Test
    void reportesIncompletosNoEntranAlConteo() {
        ObjetoPerdido sinFecha = perdido(CategoriaObjeto.OTROS, UbicacionCampus.OTRO, null);
        ObjetoPerdido sinCategoria = perdido(null, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 10, 6, 12, 0));

        AnalisisPerdidasVista vista = calculadora.analizar(List.of(
                sinFecha,
                sinCategoria,
                perdido(CategoriaObjeto.DOCUMENTOS, UbicacionCampus.BIBLIOTECA, LocalDateTime.of(2026, 10, 6, 12, 0))
        ), "7", null, null, hoy);

        assertEquals(1, vista.getTotalPeriodo());
        assertEquals("Documentos", vista.getCategoriaLider());
    }

    private ObjetoPerdido perdido(CategoriaObjeto categoria, UbicacionCampus ubicacion, LocalDateTime fecha) {
        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setNombre("Reporte de prueba");
        objeto.setCategoria(categoria);
        objeto.setUbicacion(ubicacion);
        objeto.setFechaHora(fecha);
        objeto.setDescripcion("Descripcion suficiente para el analisis");
        return objeto;
    }
}
