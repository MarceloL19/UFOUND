package pe.edu.ulima.ufound.hu31;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.*;
import pe.edu.ulima.ufound.repository.*;
import pe.edu.ulima.ufound.service.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** HU-31: el valor que decide la persona tiene prioridad sobre la sugerencia. */
class EleccionUsuarioHu31Test {

    @Test
    void categoriaElegidaPorEstudianteSeGuardaAunqueHu30SugieraOtra() {
        // HU-30 sugirió Accesorios para una mochila; el usuario eligió Útiles.
        CategoriaObjeto sugerida = new CategoriaSugerenciaService()
                .sugerir("SUGERENCIA", "Mochila").orElseThrow();
        assertEquals(CategoriaObjeto.ACCESORIOS, sugerida);

        RegistroObjetoPerdidoForm formulario = new RegistroObjetoPerdidoForm();
        formulario.setNombre("Cuaderno encontrado en mochila");
        formulario.setCategoria(CategoriaObjeto.UTILES);
        formulario.setUbicacion(UbicacionCampus.A2);
        formulario.setDescripcion("Descripción ingresada por la persona");

        ObjetoPerdidoRepository repositorio = mock(ObjetoPerdidoRepository.class);
        when(repositorio.save(any(ObjetoPerdido.class)))
                .thenAnswer(invocacion -> {
                    ObjetoPerdido objeto = invocacion.getArgument(0);
                    objeto.setIdObjeto(10L);
                    return objeto;
                });
        ObjetoPerdidoService servicio = new ObjetoPerdidoService(
                repositorio, mock(UploadService.class), mock(EstadoObjetoService.class),
                mock(CoincidenciaService.class), new ImagenAnalisisService());

        ObjetoPerdido guardado = servicio.registrar(formulario, null, new Usuario());
        assertEquals(CategoriaObjeto.UTILES, guardado.getCategoria());
        assertNotEquals(sugerida, guardado.getCategoria());
    }

    @Test
    void categoriaElegidaPorSeguridadSeGuardaAunqueHu30SugieraOtra() {
        RegistroObjetoEncontradoForm formulario = new RegistroObjetoEncontradoForm();
        formulario.setNombre("Objeto hallado");
        formulario.setCategoria(CategoriaObjeto.DOCUMENTOS);
        formulario.setUbicacion(UbicacionCampus.A2);
        formulario.setDescripcion("Seleccionado por seguridad");

        ObjetoEncontradoRepository repositorio = mock(ObjetoEncontradoRepository.class);
        when(repositorio.save(any(ObjetoEncontrado.class)))
                .thenAnswer(invocacion -> {
                    ObjetoEncontrado objeto = invocacion.getArgument(0);
                    objeto.setIdObjeto(11L);
                    return objeto;
                });
        ObjetoEncontradoService servicio = new ObjetoEncontradoService(
                repositorio, mock(UploadService.class), mock(EstadoObjetoService.class),
                mock(CoincidenciaService.class));

        ObjetoEncontrado guardado = servicio.registrar(formulario, null, new Usuario());
        assertEquals(CategoriaObjeto.DOCUMENTOS, guardado.getCategoria());
    }
}
