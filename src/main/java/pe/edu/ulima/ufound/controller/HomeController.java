
package pe.edu.ulima.ufound.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;

import pe.edu.ulima.ufound.service.AdminCategoriaDato;
import pe.edu.ulima.ufound.service.AdminDashboardService;
import pe.edu.ulima.ufound.service.BusquedaObjetoService;
import pe.edu.ulima.ufound.service.CoincidenciaService;
import pe.edu.ulima.ufound.service.EstadisticasPerdidasService;
import pe.edu.ulima.ufound.service.NotificacionService;
import pe.edu.ulima.ufound.service.ObjetoEncontradoService;
import pe.edu.ulima.ufound.service.ObjetoPerdidoService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

        private final ObjetoPerdidoService objetoPerdidoService;
        private final ObjetoEncontradoService objetoEncontradoService;
        private final CoincidenciaService coincidenciaService;
        private final NotificacionService notificacionService;
        private final BusquedaObjetoService busquedaObjetoService;
        private final AdminDashboardService adminDashboardService;
        private final EstadisticasPerdidasService estadisticasPerdidasService;

        public HomeController(
                        ObjetoPerdidoService objetoPerdidoService,
                        ObjetoEncontradoService objetoEncontradoService,
                        CoincidenciaService coincidenciaService,
                        NotificacionService notificacionService,
                        BusquedaObjetoService busquedaObjetoService,
                        AdminDashboardService adminDashboardService,
                        EstadisticasPerdidasService estadisticasPerdidasService) {

                this.objetoPerdidoService = objetoPerdidoService;
                this.objetoEncontradoService = objetoEncontradoService;
                this.coincidenciaService = coincidenciaService;
                this.notificacionService = notificacionService;
                this.busquedaObjetoService = busquedaObjetoService;
                this.adminDashboardService = adminDashboardService;
                this.estadisticasPerdidasService = estadisticasPerdidasService;
        }

        @GetMapping("/home")
        public String home(HttpSession session, Model model) {

                // 1. Obtener el usuario autenticado desde la sesión.
                Usuario usuario = (Usuario) session.getAttribute(
                                "usuarioAutenticado");

                // 2. Verificar que exista una sesión autenticada.
                if (usuario == null) {
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Debes iniciar sesión para acceder al sistema");
                }

                model.addAttribute("usuario", usuario);

                // 3. Panel de ESTUDIANTE.
                // Se conserva el comportamiento original.
                if (usuario.getRol() == Rol.ESTUDIANTE) {

                        model.addAttribute(
                                        "totalReportes",
                                        objetoPerdidoService.contarPorUsuario(usuario));

                        model.addAttribute(
                                        "totalCoincidencias",
                                        coincidenciaService.contarPorUsuario(usuario));

                        model.addAttribute(
                                        "totalNotificaciones",
                                        notificacionService.contarNoLeidas(usuario));

                        model.addAttribute(
                                        "encontradosRecientes",
                                        busquedaObjetoService.listarEncontradosRecientes());

                        return "home-estudiante";
                }

                // 4. Panel de SEGURIDAD.
                // Se conserva el comportamiento original.
                if (usuario.getRol() == Rol.SEGURIDAD) {

                        model.addAttribute(
                                        "totalHallazgos",
                                        objetoEncontradoService.contarPorUsuario(usuario));

                        return "home-seguridad";
                }

                // 5. Control de acceso al panel administrativo.
                // Solo OFICINA puede visualizar las estadísticas.
                if (usuario.getRol() != Rol.OFICINA) {

                        throw new ResponseStatusException(
                                        HttpStatus.FORBIDDEN,
                                        "No tienes permisos para acceder al panel administrativo");
                }

                // 6. Dashboard administrativo original (R1).
                // Se mantienen los KPI y gráficos existentes.
                List<AdminCategoriaDato> categorias = adminDashboardService.obtenerCategorias();

                model.addAttribute(
                                "kpis",
                                adminDashboardService.obtenerKpis());

                model.addAttribute(
                                "meses",
                                adminDashboardService.obtenerDatosMensuales());

                model.addAttribute(
                                "categorias",
                                categorias);

                model.addAttribute(
                                "categoriasPieStyle",
                                adminDashboardService.construirPieStyle(categorias));

                model.addAttribute(
                                "actividad",
                                adminDashboardService.obtenerActividadReciente());

                model.addAttribute(
                                "fechaActual",
                                LocalDate.now());

                // 7. HU-36: estadísticas exclusivas de objetos perdidos.
                EstadisticasPerdidasService.ResumenEstadistico estadisticas = estadisticasPerdidasService
                                .obtenerResumen();

                model.addAttribute(
                                "estadisticas",
                                estadisticas);

                // 8. Calcular la escala del gráfico mensual.
                // Se evita dividir entre cero cuando no hay registros.
                long maximoMensual = estadisticas.tendenciaMensual()
                                .stream()
                                .mapToLong(
                                                EstadisticasPerdidasService.DatoMensual::total)
                                .max()
                                .orElse(0L);

                model.addAttribute(
                                "maximoMensual",
                                Math.max(1L, maximoMensual));

                // 9. Mostrar el panel administrativo con HU-36.
                return "home-oficina";
        }
}
