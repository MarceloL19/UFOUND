package pe.edu.ulima.ufound.hu32;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.hu31.CategoriaSugerenciaService;
import pe.edu.ulima.ufound.model.*;
import pe.edu.ulima.ufound.repository.*;
import pe.edu.ulima.ufound.service.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** HU-32: se guardan los valores que el usuario confirma, no los que sugirió el análisis. */
class ValoresConfirmadosHu32Test {

    private final Map<String, String> sugeridos =
            new AutocompletadoFormularioService(new CategoriaSugerenciaService())
                    .sugerirCampos("SUGERENCIA", "Mochila", "Negro");

    @Test
    void estudianteGuardaLosValoresQueModificoTrasElAutocompletado() {
        RegistroObjetoPerdidoForm formulario = new RegistroObjetoPerdidoForm();
        formulario.setNombre("Mochila negra Totto");
        formulario.setCategoria(CategoriaObjeto.valueOf(sugeridos.get("categoria")));
        formulario.setUbicacion(UbicacionCampus.A2);
        formulario.setDescripcion("Mochila de color negro con llavero de Pikachu.");

        ObjetoPerdidoRepository repositorio = mock(ObjetoPerdidoRepository.class);
        when(repositorio.save(any(ObjetoPerdido.class))).thenAnswer(invocacion -> {
            ObjetoPerdido objeto = invocacion.getArgument(0);
            objeto.setIdObjeto(20L);
            return objeto;
        });
        ObjetoPerdidoService servicio = new ObjetoPerdidoService(
                repositorio, mock(UploadService.class), mock(EstadoObjetoService.class),
                mock(CoincidenciaService.class), new ImagenAnalisisService());

        ObjetoPerdido guardado = servicio.registrar(formulario, null, new Usuario());

        assertEquals("Mochila negra Totto", guardado.getNombre());
        assertNotEquals(sugeridos.get("nombre"), guardado.getNombre());
        assertEquals("Mochila de color negro con llavero de Pikachu.", guardado.getDescripcion());
        assertEquals(CategoriaObjeto.ACCESORIOS, guardado.getCategoria());
        // La ubicación la eligió la persona; el autocompletado nunca la propone.
        assertEquals(UbicacionCampus.A2, guardado.getUbicacion());
    }

    @Test
    void seguridadGuardaLosValoresQueModificoTrasElAutocompletado() {
        RegistroObjetoEncontradoForm formulario = new RegistroObjetoEncontradoForm();
        formulario.setNombre("Bolso negro");
        formulario.setCategoria(CategoriaObjeto.OTROS);
        formulario.setUbicacion(UbicacionCampus.A2);
        formulario.setDescripcion("Corregido por seguridad");

        ObjetoEncontradoRepository repositorio = mock(ObjetoEncontradoRepository.class);
        when(repositorio.save(any(ObjetoEncontrado.class))).thenAnswer(invocacion -> {
            ObjetoEncontrado objeto = invocacion.getArgument(0);
            objeto.setIdObjeto(21L);
            return objeto;
        });
        ObjetoEncontradoService servicio = new ObjetoEncontradoService(
                repositorio, mock(UploadService.class), mock(EstadoObjetoService.class),
                mock(CoincidenciaService.class));

        ObjetoEncontrado guardado = servicio.registrar(formulario, null, new Usuario());

        assertEquals("Bolso negro", guardado.getNombre());
        assertEquals(CategoriaObjeto.OTROS, guardado.getCategoria());
        assertEquals("Corregido por seguridad", guardado.getDescripcion());
    }
}
