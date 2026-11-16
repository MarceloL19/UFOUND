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

## Gestión Scrum y estado del Release 2

Corte de seguimiento: **16/11/2026**. Se mantienen tres sprints y una entrega final sobre la base heredada del Release 1. Las fechas siguientes son las previstas de entrega, no las fechas de integración ni de aceptación.

| Entrega | Fecha prevista | Alcance | Estado al corte |
|---|---|---|---|
| Sprint 1 | 18/09/2026 | Estabilización de R1; autenticación, sesión y home por rol | Evidencia de cinco pruebas de AuthService del 20/09; milestone cerrado administrativamente, sin issues asociados |
| Sprint 2 | 09/10/2026 | HU-26, 27, 28, 36, 37 y 38 | Seis historias cerradas como Completed; Daily #41 y Review #42 cerrados; retrospectiva #43 abierta |
| Sprint 3 | 30/10/2026 | HU-29 a HU-32: registro asistido por imágenes | Cuatro issues cerrados como Completed y milestone cerrado |
| Entrega final | 13/11/2026 | HU-33 a HU-35: coincidencias con atributos visuales, orden y sugerencias | Tres issues cerrados como Completed y milestone cerrado |

Las 25 HU de R1 son antecedentes y no cuentan como implementación nueva ni velocidad de R2. Las 13 HU nuevas están cerradas como Completed. El cierre de un issue no acredita por sí solo una fecha de entrega anterior ni una ejecución de pruebas.

El equipo acordado para Sprint 2 fue Gabriel Revoredo (Scrum Master), Sergio Chaparro (Product Owner), Marcelo Loayza, Rodrigo Figueroa y Daniel Sandoval (Developers). En la regularización del cierre, Marcelo registró que asumía la revisión y aceptación funcional. Las aceptaciones anteriores se conservan en el historial.

### Evidencias y límites

- Sprint 1: cinco pruebas de AuthService, con evidencia fechada el 20/09/2026; no acredita por sí sola la validación de toda la aplicación.
- Sprint 2: Daniel publicó en #42 una ejecución del 12/10/2026 con 28 pruebas, sin fallos, errores ni omisiones. Es posterior a la fecha prevista del sprint. Las validaciones de HU-28 y HU-36 están en sus issues.
- Daily #41: cerrado como Completed con confirmación retrospectiva de Marcelo sobre dos reuniones realizadas. No se registraron fechas exactas ni intervenciones individuales; no se reconstruyen actas.
- HU-33: integrada mediante PR #73. La evaluación de 30 pares etiquetados por Daniel compara ambos motores con iguales métricas. En los umbrales 40 y 60, las métricas permanecen iguales: no demuestra mejora general de precisión. Véase [la documentación de evaluación](docs/HU-33-IMPLEMENTACION.md).
- HU-34 y HU-35: integradas mediante PR #75 y #76. La PR anterior #72 se cerró sin fusionar por sustitución. Las reglas, alcance y pruebas se describen en sus documentos de implementación.
- El caso #40 se cerró como Not planned: era una simulación y la revisión de código mostró una fecha asignada por el servidor, sin entrada editable en el formulario. No se contabiliza como corrección implementada.

### Tablero, estimaciones y pendientes

- [Project UFOUND - Gestion Scrum IS2](https://github.com/users/MarceloL19/projects/1).
- [Product Backlog y trazabilidad](docs/product-backlog.md).
- [Gestión Scrum e historial del laboratorio](docs/gestion-scrum.md).
- [Estabilización del Sprint 1](docs/sprint-1-estabilizacion.md).
- [Burn up - Sprint 2](https://github.com/users/MarceloL19/projects/1/insights/4), [carga por integrante](https://github.com/users/MarceloL19/projects/1/insights/2) y [velocidad](https://github.com/users/MarceloL19/projects/1/insights/3).

La consulta del Project mostró 29 SP en las seis HU del Sprint 2: 8, 5, 3, 5, 3 y 5 para HU-26, 27, 28, 36, 37 y 38, respectivamente. La coordinación del 06/10 también registra 29 SP y disponibilidad de tres horas diarias por integrante, pero deja pendiente validar capacidad con Planning Poker. No hay aquí un acta de estimaciones individuales que permita afirmar ese acuerdo. Sprint 3 y las HU de entrega final aparecían sin puntos; no se inventan estimaciones retrospectivas. Tareas, PR y seguimiento permanecen fuera del cómputo de SP de historias.

Los gráficos y sus capturas iniciales son antecedentes: sus ceros y conteos no representan el estado actual. El historial de importación de GitHub no reconstruye el desarrollo previo; el conteo de ítems tampoco equivale a esfuerzo. Las aceptaciones consolidadas acreditan el resultado funcional actual del Sprint 2, no el cumplimiento del plazo previsto.

Quedan abiertas [la retrospectiva #43](https://github.com/MarceloL19/UFOUND/issues/43), a la espera de aportes del equipo y acuerdos, y [el seguimiento #64](https://github.com/MarceloL19/UFOUND/issues/64), que incluye integrar esta documentación y cerrar administrativamente el Sprint 2. Sprint 1 ya tiene su milestone cerrado administrativamente, con sus límites de evidencia documentados. Esta actualización no cierra esos registros ni el Project.

### Despliegue

Se preparó Railway con MySQL y volúmenes persistentes y se comprobó la carga de la aplicación por su dominio público. Según el contexto de despliegue, los despliegues se detuvieron para conservar crédito; esa condición no se ha verificado en el panel durante esta revisión. La validación completa de login, persistencia y fotos en el entorno público sigue siendo una comprobación separada antes de la exposición.
