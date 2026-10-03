# Laboratorio 4 aplicado a UFOUND

Se adapta el laboratorio al proyecto de objetos perdidos y encontrados UFOUND y al cronograma ya acordado. El caso de reservas de salas de la guía no sustituye el alcance del proyecto.

## Equipo y roles

| Integrante | GitHub | Participación |
|---|---|---|
| Marcelo Franco Loayza Taboada | MarceloL19 | Developer |
| Daniel Joseph Sandoval Chavarria | Por confirmar | Developer |

El equipo debe designar un Product Owner y un Scrum Master. No se asignan estos roles por inferencia ni se reutilizan los seis integrantes de la documentación antigua. Los demás integrantes, si los hubiera, también deben confirmarse. Agregar a los integrantes como colaboradores y otorgar Write en el Project una vez identificadas sus cuentas.

## Cronograma y Sprint Goals

| Entrega | Fecha límite | Periodo previsto | Alcance | Sprint Goal propuesto |
|---|---|---|---|---|
| Sprint 1 | 18/09/2026 | Inicio por confirmar | Estabilización de R1; revisión de HU-01 a HU-03 | Estabilizar y validar técnicamente la autenticación y revisar sesión y home por rol. |
| Sprint 2 | 09/10/2026 | 19/09–09/10/2026 | HU-26, 27, 28, 36, 37, 38 | Ofrecer ayuda contextual y análisis de reportes de pérdidas. |
| Sprint 3 | 30/10/2026 | 10/10–30/10/2026 | HU-29 a HU-32 | Registrar objetos con sugerencias editables obtenidas de una imagen. |
| Entrega final | 13/11/2026 | 31/10–13/11/2026 | HU-33 a HU-35; integración, pruebas y documentación | Integrar coincidencias avanzadas y entregar una versión validada y documentada. |

La entrega final no se denomina Sprint 4. Los periodos futuros se proponen a partir de los cierres del cronograma. La selección de alcance debe revisarse tras estimar la capacidad real.

## Sprint 1: avance recibido

Las 25 HU de R1 son una línea base heredada. El Sprint 1 tuvo ocho tareas de estabilización, con 13 puntos de tareas registrados en la primera entrega; esos valores no equivalen a Story Points de las 25 HU.

| Tarea | Relación | Trabajo documentado | Puntos de tarea históricos |
|---|---|---|---|
| T-01 | HU-01 a HU-25 | Inventariar módulos y capas | 1 |
| T-02 | HU-01 | Revisar autenticación y credenciales | 2 |
| T-03 | HU-02 | Revisar sesión y logout | 1 |
| T-04 | HU-03 | Revisar home por rol y rutas | 2 |
| T-05 | HU-01 | Definir cinco casos unitarios | 2 |
| T-06 | HU-01 | Implementar AuthServiceTest | 3 |
| T-07 | HU-01 | Ejecutar y registrar las pruebas | 1 |
| T-08 | Transversal | Documentar hallazgos y alcance | 1 |

Se incorpora el test existente y la evidencia recibida, sin modificar el código de producción. La salida de Maven conserva su fecha original: 20/09/2026, posterior a la fecha límite del cronograma. No acredita ejecución el 18/09 ni se presenta como una ejecución nueva de este PR.

Resultado registrado en esa evidencia: cinco pruebas unitarias de AuthService, cero fallos y cero errores. No valida automáticamente logout, home por rol, integración con MySQL ni la aplicación completa. Las comprobaciones manuales siguen pendientes según [el informe de estabilización](sprint-1-estabilizacion.md).

## Planning Poker y capacidad

Usar Fibonacci 1, 2, 3, 5, 8, 13. Registrar estimaciones individuales, diferencia discutida y acuerdo. No convertir los puntos en horas ni reutilizar como capacidad los 13 puntos de tareas históricas.

| Dato que debe acordar el equipo | Situación |
|---|---|
| Disponibilidad por integrante y capacidad del Sprint 2 | Pendiente |
| Story Points de cada HU nueva | Pendiente |
| HU con mayor diferencia de estimación y motivos | Pendiente: no hubo una sesión registrada |
| Responsables de tareas y compromiso del sprint | Pendiente |

Comprobar que la suma de puntos de las HU seleccionadas no supere la capacidad acordada. Estimar las historias para las métricas; las tareas de descomposición no vuelven a sumar esos puntos.

## Configuración pendiente de GitHub Projects

Crear `UFOUND - Gestión Scrum`, vincularlo al repositorio y conceder Write a los integrantes confirmados. Esta sección describe la configuración que debe hacerse; la existencia del documento no significa que el Project esté configurado.

| Campo | Tipo | Valores |
|---|---|---|
| Status | Single select | Product Backlog, Sprint Backlog, In Progress, In Review, Done |
| Sprint | Iteration | Sprint 1, Sprint 2, Sprint 3 y Entrega final según sus fechas; confirmar primero el inicio del Sprint 1 |
| Story Points | Number | Estimaciones acordadas por el equipo |
| Prioridad | Single select | Must, Should, Could, Won't, en ese orden |
| Tipo | Single select | Historia, Tarea, Bug, Spike; seguimiento sin puntos |

Crear etiquetas `historia-usuario`, `tarea`, `spike`, `frontend`, `backend`, `release-1` y `release-2`; conservar `bug`. Las etiquetas declaradas en una plantilla deben existir en el repositorio para poder aplicarse.

Crear milestones Sprint 1, Sprint 2, Sprint 3 y Entrega final con los cierres indicados. Mantener HU-01 a HU-25 fuera de los milestones de R2: su importación histórica no los convierte en trabajo del Sprint 1 actual.

Importar las HU nuevas y sus tareas, asignar Tipo y Prioridad, y completar Sprint conforme al plan ratificado. Las HU futuras permanecen en Product Backlog; las seleccionadas para el sprint van a Sprint Backlog después de validar estimaciones y capacidad. Vincular cada tarea preparada mediante Add existing sub-issue en su HU padre y comprobar la barra de progreso.

| Vista | Tipo | Configuración |
|---|---|---|
| Product Backlog | Table | Title, Status, Prioridad, Story Points, Sprint, Assignees, Labels; filtro `-status:Done`; Prioridad ascendente y orden manual por valor dentro de cada prioridad |
| Sprint Actual | Board | Columnas por Status; filtro `sprint:@current`; mostrar Story Points, Assignees, Prioridad y Sub-issues progress; suma de Story Points por columna |
| Roadmap | Roadmap | Fechas por Sprint y agrupación por Prioridad; mostrar Sprint 2, Sprint 3 y Entrega final |
| Mis tareas | Table | Filtro `assignee:@me -status:Done` |

Activar y verificar los workflows: item añadido → Product Backlog; issue cerrado → Done; PR fusionado → Done; issue reabierto → In Progress. Configurar auto-add de issues y PR abiertos del repositorio. Crear un issue de prueba, comprobar alta y transición al cerrarlo, y eliminarlo al finalizar la verificación.

Proteger main: exigir PR y una aprobación. Cada integrante completa al menos un ciclo real: tarea In Progress, rama `feature/<issue>-descripcion`, commit que referencia el issue, PR con `Closes #N`, tarea In Review, comentario y aprobación de otro integrante, merge y verificación de cierre y estado Done. Este PR preparado no sustituye los ciclos individuales de todos los integrantes.

## Registros de los eventos

- Daily Scrum: [#41](https://github.com/MarceloL19/UFOUND/issues/41). Registrar al menos dos reuniones reales y las respuestas de cada integrante.
- Sprint Review: [#42](https://github.com/MarceloL19/UFOUND/issues/42). El PO comprueba criterios, acepta las HU demostradas y devuelve lo incompleto al backlog.
- Retrospectiva: [#43](https://github.com/MarceloL19/UFOUND/issues/43). Cada integrante aporta a Start, Stop y Continue; crear acciones como sub-issues para Sprint 3 con responsables.
- Bug ficticio del laboratorio: [#40](https://github.com/MarceloL19/UFOUND/issues/40). Escenario didáctico identificado como simulación; el PO decide cómo tratarlo, sin presentarlo como un defecto comprobado.
- Archivos del repositorio: [#39](https://github.com/MarceloL19/UFOUND/issues/39). Cerrar mediante el PR revisado.
- Configuración del Project y ajustes: [#56](https://github.com/MarceloL19/UFOUND/issues/56). Cerrar después de comprobar su configuración.

## Insights y evidencias

Configurar Burnup filtrado por el Sprint actual; `Carga por integrante` como barras apiladas por Assignees y Status, sumando Story Points; y `Velocidad` por Sprint, sumando Story Points y filtrando Done. Para las métricas de historias, incluir únicamente las HU de R2 y excluir tareas, PR, seguimiento y antecedentes R1. Si se necesita medir tareas, hacerlo en una vista separada y explicitar la unidad.

Obtener las tres capturas después de configurar los gráficos y de registrar datos reales. Mientras no haya estimaciones o cierres, conservar el gráfico vacío y explicar esa situación; no rellenarlo con puntos ficticios. Los cierres históricos importados no prueban velocidad del curso.

Agregar al README el resultado real del Sprint Goal, las diferencias de estimación discutidas, enlaces a PR revisados y las capturas de Insights cuando existan. Cerrar el milestone después de la Review y refinar el Sprint 3.

## Estado verificable de la preparación

| Requisito | Estado |
|---|---|
| 38 HU y distribución de entregas | Issues creados; ver [Product Backlog](product-backlog.md) |
| Tareas de HU del Sprint 2 | Issues creados; vinculación nativa y responsables pendientes |
| Plantillas, documentación y pruebas recibidas | Preparadas en PR; pendientes de revisión y merge |
| Project, vistas, campos, workflows, milestones y protección | Pendientes de configurar mediante una vía que permita esas operaciones |
| Roles, estimaciones, Daily, Review y Retro | Participación real del equipo pendiente |
| Ciclo individual por integrante e Insights con capturas | Pendientes de ejecución y evidencia |

La guía no pide implementar todo el Release 2 durante este laboratorio: pide demostrar la gestión y al menos un cambio mínimo por integrante mediante el flujo de PR.
