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

## Organización del proyecto en Ingeniería de Software II

La aplicación construida en **Ingeniería de Software I** es la **línea base heredada**. No es el Release 1 del curso actual.

En **Ingeniería de Software II se desarrollan dos releases con dos sprints cada uno**. La numeración global de los sprints es 1, 2, 3 y 4; dentro de cada release corresponde a Sprint 1 y Sprint 2. La cuarta etapa del cronograma corresponde al Sprint 4, aunque algunos registros históricos la denominen «Entrega final».

| Release de Software II | Sprint global | Periodo de referencia | Hito de cierre registrado | Objetivo y alcance |
|---|---|---|---|---|
| Release 1 | Sprint 1 (R1-S1) | Inicio de referencia: 29/08/2026, por confirmar | 18/09/2026 | Revisar y estabilizar la aplicación heredada: autenticación, sesión y acceso por rol; pruebas iniciales. |
| Release 1 | Sprint 2 (R1-S2) | 19/09–09/10/2026 | 09/10/2026 | Asistencia y ayuda contextual (HU-26–28) y estadísticas descriptivas (HU-36–38). |
| Release 2 | Sprint 3 (R2-S1) | 10/10–30/10/2026 | 30/10/2026 | Registro asistido por imágenes: validación, detección, categoría y autocompletado editable (HU-29–32). |
| Release 2 | Sprint 4 (R2-S2) | 31/10–13/11/2026 | 13/11/2026 | Coincidencias con atributos visuales, ordenamiento y sugerencias (HU-33–35), integración, pruebas y documentación. |

Las fechas anteriores son los hitos del cronograma registrado. La entrega del informe final es el **16/11/2026** y la exposición el **17/11/2026**; estas fechas no sustituyen las fechas reales de commits, pruebas, aceptaciones ni publicaciones de evidencias. Las mejoras finales adicionales incluyen la navegación de Ulises (HU-39, issue #79) y los ajustes visuales de integración.

Las HU-01–25 describen funcionalidades heredadas que se revisan y estabilizan cuando corresponde. No deben contabilizarse como implementación nueva ni como velocidad de los sprints por el solo hecho de haber sido importadas a GitHub. Las HU-26–38 constituyen el alcance de evolución originalmente registrado para Software II; HU-39 es una mejora adicional posterior.

### Documentación y seguimiento

- [Product Backlog y enlaces a historias](docs/product-backlog.md).
- [Organización de releases, sprints y evidencias](docs/gestion-scrum.md).
- [Evidencia de estabilización del Sprint 1](docs/sprint-1-estabilizacion.md).
- [Project UFOUND - Gestión Scrum IS2](https://github.com/users/MarceloL19/projects/1).
- [Daily y planificación del Sprint 2: #41](https://github.com/MarceloL19/UFOUND/issues/41).
- [Review del Sprint 2: #42](https://github.com/MarceloL19/UFOUND/issues/42).
- [Retrospectiva y balance individual: #43](https://github.com/MarceloL19/UFOUND/issues/43).
- [Seguimiento de mejoras: #64](https://github.com/MarceloL19/UFOUND/issues/64).

Los documentos históricos pueden conservar la nomenclatura anterior que agrupaba el trabajo actual bajo «R2» o separaba la cuarta etapa como «Entrega final». Para el informe y la organización vigente del curso se utiliza la correspondencia de la tabla anterior. Esta aclaración no modifica las evidencias históricas.

### Evidencias de implementación

- Asistente y ayuda contextual: PR #60 y #61.
- Registro asistido por imágenes: PR #65, #67, #68 y #69.
- Coincidencias avanzadas: PR #73, #75 y #76.
- Navegación de Ulises y mejoras visuales finales: PR #80 y #82.

Los resultados de pruebas corresponden a la versión y fecha indicadas en cada evidencia. Por ejemplo, las cinco pruebas originales de AuthService conservan la fecha del 20/09/2026. La evaluación de HU-33 con 30 pares no demostró mejora de precisión frente al motor base en los umbrales utilizados; no debe presentarse como una mejora cuantitativa comprobada.

### Métricas del proyecto

El esfuerzo se sigue mediante Story Points de historias estimadas por el equipo. Las tareas, PR y registros de reuniones no vuelven a sumar los puntos de sus historias. Los puntos históricos de tareas del Sprint 1 no son automáticamente Story Points de historias ni capacidad de los siguientes sprints.

Los gráficos se interpretan con sus datos y fechas reales. Un valor cero con estimaciones vacías no demuestra esfuerzo nulo y la importación de issues no reproduce el avance anterior. Los gráficos históricos del Sprint 2 deben identificarse con su fecha de captura:

- [Burn up - Sprint 2](https://github.com/users/MarceloL19/projects/1/insights/4).
- [Carga por integrante](https://github.com/users/MarceloL19/projects/1/insights/2).
- [Velocidad del equipo](https://github.com/users/MarceloL19/projects/1/insights/3).
