# PROYECTO-UFOUND

Sistema web para la gestion de objetos perdidos en la Universidad de Lima.

## EPIC 1

Modulo base de autenticacion y control de acceso por roles.

## EPIC 2

Registro de objetos perdidos exclusivo para usuarios con rol `ESTUDIANTE`.

Incluye:

- formulario web con Thymeleaf;
- entidad `ObjetoPerdido`;
- categorias y ubicaciones como enums;
- guardado en MySQL;
- asociacion con el usuario autenticado;
- carga opcional de imagen en `uploads/objetos-perdidos`;
- listado de reportes registrados por el estudiante.

## EPIC 3

Registro y visualizacion de objetos encontrados exclusivo para usuarios con rol `SEGURIDAD`.

Incluye:

- formulario web con Thymeleaf;
- entidad `ObjetoEncontrado`;
- guardado en MySQL;
- asociacion con el usuario de seguridad autenticado;
- carga opcional de imagen en `uploads/objetos-encontrados`;
- listado de objetos encontrados;
- vista de detalle del objeto encontrado.

## EPIC 4

Gestion del ciclo de vida de los objetos mediante estados e historial.

Incluye:

- enum unificado `EstadoObjeto`;
- entidad `HistorialEstado`;
- registro de estado inicial al crear objetos perdidos o encontrados;
- actualizacion de estado exclusiva para usuarios con rol `OFICINA`;
- vista de gestion de estados para oficina;
- vista de seguimiento de estado para estudiantes y seguridad;
- trazabilidad de cambios con estado anterior, estado nuevo, fecha y usuario responsable.

## EPIC 5

Deteccion de coincidencias y notificaciones automaticas para estudiantes.

Incluye:

- entidad `Coincidencia`;
- entidad `Notificacion`;
- enum `NivelCoincidencia`;
- comparacion automatica entre objetos perdidos y encontrados;
- calculo de similitud por categoria, ubicacion, nombre y descripcion;
- generacion de coincidencias desde 40% de similitud;
- generacion de notificaciones para coincidencias de nivel medio o alto;
- vista de coincidencias para estudiantes;
- vista de notificaciones leidas y no leidas;
- opcion para marcar notificaciones como leidas.

## EPIC 6

Busqueda, filtrado y consulta de objetos encontrados para estudiantes.

Incluye:

- `BusquedaController`;
- `BusquedaObjetoService`;
- busqueda por texto, categoria, ubicacion y fecha;
- resultados mostrados en cards con informacion resumida;
- vista de detalle del objeto encontrado;
- seccion de objetos encontrados recientes para estudiantes;
- acceso restringido al rol `ESTUDIANTE`;
- consulta dinamica usando datos persistidos en MySQL.

## EPIC 7

Dashboard administrativo para usuarios con rol `OFICINA`.

Incluye:

- `AdminDashboardService`;
- indicadores KPI de objetos perdidos, objetos encontrados, coincidencias, recuperados y archivados;
- datos agrupados por mes;
- distribucion por categoria;
- actividad reciente del sistema;
- panel administrativo dinamico basado en informacion de la base de datos;
- acceso restringido al rol `OFICINA`.

Tecnologias:

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- MySQL

Base de datos:

- `proyecto_ufound_db`
- La URL JDBC incluye `createDatabaseIfNotExist=true`, por lo que MySQL puede crearla automaticamente si el usuario tiene permisos.

Usuarios de prueba: configurar las cuentas y credenciales en el entorno local; no publicar contraseñas en la documentación.

Ejecucion:

```powershell
C:\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run
```

URL:

```text
http://localhost:8080/login
```

## Gestión Scrum y laboratorio 4

El trabajo del curso se organiza en tres sprints y una entrega final, usando la línea base del Release 1.

| Entrega | Cierre | Objetivo y alcance |
|---|---|---|
| Sprint 1 | 18/09/2026 | Estabilizar la línea base; revisar autenticación, sesión y home por rol. La evidencia de cinco pruebas de AuthService está en docs y conserva su fecha original del 20/09/2026. |
| Sprint 2 | 09/10/2026 | Ayuda contextual y estadísticas de pérdidas: HU-26, 27, 28, 36, 37 y 38. |
| Sprint 3 | 30/10/2026 | Registro asistido por imágenes: HU-29 a HU-32. |
| Entrega final | 13/11/2026 | Coincidencias avanzadas: HU-33 a HU-35; integración, pruebas y documentación. |

Developers registrados: Marcelo Franco Loayza Taboada y Daniel Joseph Sandoval Chavarria. Cuenta de Daniel verificada como `danielsandtyler`, colaborador existente del repositorio. Product Owner y Scrum Master: pendientes de designación del equipo.

Capacidad y Story Points del Release 2: pendientes de Planning Poker. Diferencias de estimación y sus motivos: pendientes de registrar después de la sesión. No se declaran ceremonias ni aprobaciones que todavía no ocurrieron.

- [Product Backlog con 38 HU y tareas](docs/product-backlog.md).
- [Plan del laboratorio, roles, configuración y seguimiento](docs/gestion-scrum.md).
- [Avance y alcance comprobado del Sprint 1](docs/sprint-1-estabilizacion.md).
- Configuración del laboratorio: [#39](https://github.com/MarceloL19/UFOUND/issues/39).

Las 25 HU heredadas de R1 se identifican como antecedentes; no cuentan como implementación nueva ni como velocidad de los sprints de R2. Las fechas de cierre de sus issues corresponden a su importación en GitHub.

## Tablero y seguimiento configurados

- [Project UFOUND - Gestion Scrum IS2](https://github.com/users/MarceloL19/projects/1).
- [Burn up - Sprint 2](https://github.com/users/MarceloL19/projects/1/insights/4).
- [Carga por integrante](https://github.com/users/MarceloL19/projects/1/insights/2).
- [Velocidad del equipo](https://github.com/users/MarceloL19/projects/1/insights/3).

El Project contiene las 38 HU, 12 tareas del Sprint 2 vinculadas como sub-issues y los registros de seguimiento. Las seis HU previstas para Sprint 2 y sus tareas están en Sprint Backlog; ese alcance sigue sujeto a validar capacidad con Planning Poker. Sprint 3 mantiene HU-29 a HU-32 y la entrega final HU-33 a HU-35. Los hitos cierran el 18/09, 09/10, 30/10 y 13/11/2026. La referencia de inicio del Sprint 1 en el calendario es 29/08; el equipo debe confirmarla.

Daniel Sandoval, colaborador existente del repositorio con la cuenta `danielsandtyler`, tiene Write en el Project. Los roles PO y SM todavía deben acordarse. En `main` está activa la regla de PR obligatorio con una aprobación; el PR #57 permanece abierto para revisión de otro integrante.

**¿Se cumplirá el Sprint Goal según el Burn up?** Todavía no se puede concluir. El gráfico registra 18 ítems abiertos (6 historias y 12 tareas), cero cierres del Sprint 2 y datos importados el 03/10/2026. El salto de esa fecha representa el alta en GitHub, no ejecución histórica. Faltan estimaciones, disponibilidad y evidencia de avance real. El conteo de ítems no equivale a esfuerzo.

Si el pronóstico muestra que no alcanza la capacidad, el PO debe conservar primero las HU Must (26–28), revisar el alcance Should (36–38), dividir historias grandes y devolver lo que no quepa al Product Backlog. Registrar bloqueos en Daily y actualizar el pronóstico con los cierres reales. Carga y velocidad están configuradas con suma de Story Points, pero sus ceros actuales reflejan puntos sin asignar; las 25 HU heredadas no se computan como velocidad de R2.
