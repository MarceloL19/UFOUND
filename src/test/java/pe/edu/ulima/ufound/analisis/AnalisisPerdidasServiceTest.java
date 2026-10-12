package pe.edu.ulima.ufound.analisis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;
import pe.edu.ulima.ufound.repository.ObjetoPerdidoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalisisPerdidasServiceTest {

    @Mock
    private ObjetoPerdidoRepository objetoPerdidoRepository;

    @Test
    void objetosEncontradosNoAlteranElConteoExclusivoDePerdidos() {
        ObjetoEncontrado encontrado = new ObjetoEncontrado();
        encontrado.setNombre("Mochila hallada en porteria");
        encontrado.setCategoria(CategoriaObjeto.ROPA);
        encontrado.setUbicacion(UbicacionCampus.BIBLIOTECA);
        encontrado.setFechaHora(LocalDateTime.of(2026, 10, 3, 15, 0));
        encontrado.setDescripcion("Este hallazgo no pertenece al conteo de perdidas.");

        ObjetoPerdido perdido = new ObjetoPerdido();
        perdido.setNombre("Casaca reportada");
        perdido.setCategoria(CategoriaObjeto.ROPA);
        perdido.setUbicacion(UbicacionCampus.BIBLIOTECA);
        perdido.setFechaHora(LocalDateTime.of(2026, 10, 3, 11, 0));
        perdido.setDescripcion("Reporte de objeto perdido.");

        when(objetoPerdidoRepository.findAll()).thenReturn(List.of(perdido));
        AnalisisPerdidasService service = new AnalisisPerdidasService(objetoPerdidoRepository, new AnalisisPerdidasCalculadora());

        AnalisisPerdidasVista vista = service.consultar("30", null, null, LocalDate.of(2026, 10, 11));

        assertNotNull(encontrado.getNombre());
        assertEquals(1, vista.getTotalPeriodo());
        assertEquals(1, vista.getCategorias().get(0).getTotal());
        assertEquals("Ropa", vista.getCategoriaLider());
        verify(objetoPerdidoRepository).findAll();
    }
}
