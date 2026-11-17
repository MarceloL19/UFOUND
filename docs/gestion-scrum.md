# Laboratorio 4 aplicado a UFOUND

## Estado actualizado al 16/11/2026

Esta sección sustituye las afirmaciones de estado actual del registro histórico que se conserva más abajo. No modifica fechas ni atribuye ceremonias o aprobaciones no documentadas.

| Elemento | Resultado comprobado |
|---|---|
| HU-26 a HU-38 | Las 13 historias nuevas tienen issues cerrados como Completed |
| Sprint 2 | Seis historias aceptadas y cerradas; decisiones actuales de Marcelo en cada issue y consolidación en #42 |
| Daily #41 | Cerrado como Completed; Marcelo confirmó retrospectivamente dos reuniones, sin fechas exactas ni intervenciones individuales registradas |
| Review #42 | Cerrada como Completed; consolida validaciones anteriores, no una nueva reunión |
| Retrospectiva #43 | Abierta; aporte de Marcelo publicado, pendiente participación del resto y consolidación de acuerdos |
| Coordinación #64 | Abierta; seguimiento de documentación y cierre administrativo del Sprint 2 |
| Sprint 3 y entrega final | Milestones cerrados, con cuatro y tres issues cerrados respectivamente |
| Sprint 1 | Milestone cerrado administrativamente, sin issues asociados; descripción de cierre y límites de evidencia conservados |
| Caso simulado #40 | Cerrado como Not planned, con revisión de código documentada; no representa una corrección implementada |
| PR #72 | Cerrada sin fusionar; sustituida por #75 |

### Equipo y decisiones de aceptación

Se conserva el reparto original: Gabriel como Scrum Master, Sergio como PO y Marcelo, Rodrigo y Daniel como Developers. Para regularizar el cierre, Marcelo declaró que asumía las funciones de PO y registró su aceptación en las historias de Sprint 2 y entrega final. No se atribuyen esas decisiones a Sergio ni se altera el historial de sus aceptaciones previas. Las cuentas observadas en el tablero y comentarios son MarceloL19, danielsandtyler, chapsergio, RodaloF02 y Progalo; no se deducen permisos actuales a partir de su aparición.

### Estimaciones y métricas

La consulta del Project mostró 29 SP del Sprint 2: HU-26=8, HU-27=5, HU-28=3, HU-36=5, HU-37=3 y HU-38=5. La planificación por chat del 06/10/2026 registra ese total, responsables y disponibilidad de tres horas diarias por integrante, dejando pendiente validar capacidad con Planning Poker. No se documentan votos individuales ni diferencias de estimación que no constan.

Sprint 3 y las historias finales aparecían sin SP. El seguimiento, tareas y PR no duplican puntos de historias. Las capturas iniciales de Insights no reflejan el cierre actual y el historial de importación no demuestra progreso previo. Los cierres de noviembre no acreditan entregas en septiembre u octubre.

El plan ubica HU-33 a HU-35 en la entrega final, sin Sprint 4. Se verificó la conciliación del tablero: las tres historias conservan Status Done y milestone Entrega final - Release 2, con el campo Sprint vacío. HU-29 a HU-32 permanecen en Sprint 3.

### Evidencia técnica y límites

La evidencia de Sprint 1 conserva su fecha 20/09/2026 y cinco pruebas de AuthService. En #42, Daniel publicó una ejecución del 12/10/2026 de 28 pruebas sin fallos, errores ni omisiones. Ambas fechas son posteriores a las entregas previstas respectivas. Las validaciones funcionales de HU-28 y HU-36 están en sus issues.

HU-33, HU-34 y HU-35 están integradas mediante PR #73, #75 y #76. La evaluación de 30 pares de HU-33 no demostró mejora de precisión en los umbrales analizados; véase [su documentación](HU-33-IMPLEMENTACION.md). Cerrar las historias no convierte ese resultado en una mejora estadística demostrada.

El cierre del Project sigue pendiente de terminar #43 y #64 y cerrar el milestone Sprint 2. Esta actualización documenta el avance; no declara cerrado el proyecto completo.

## Registro histórico de preparación del laboratorio

**Las secciones siguientes describen la configuración y los pendientes de la preparación inicial.** Expresiones como «pendiente», «cero completados», «Project privado» o «todavía debe» son antecedentes de ese corte, no una comprobación del estado actual. Se conservan para trazabilidad del laboratorio. Para el estado del cierre, usar la sección actualizada anterior.

Se adapta el laboratorio al proyecto de objetos perdidos y encontrados UFOUND y al cronograma ya acordado. El caso de reservas de salas de la guía no sustituye el alcance del proyecto.

## Equipo y roles

| Integrante | GitHub | Participación |
|---|---|---|
| Gabriel Revoredo | Por registrar | Scrum Master |
| Sergio Chaparro | Por registrar | Product Owner |
| Marcelo Franco Loayza Taboada | MarceloL19 | Developer |
| Rodrigo Figueroa | Por registrar | Developer |
| Daniel Joseph Sandoval Chavarria | danielsandtyler | Developer |

Marcelo confirmó a los cinco integrantes y sus roles para el Sprint 2. Los nombres de usuario de Gabriel, Sergio y Rodrigo quedan por registrar en esta tabla; no se asignan cuentas de GitHub por inferencia. Daniel ya tiene Write en el Project y Marcelo conserva Admin como propietario.

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

## Configuración realizada de GitHub Projects

[UFOUND - Gestion Scrum IS2](https://github.com/users/MarceloL19/projects/1) está vinculado a UFOUND. Marcelo tiene Admin y Daniel (`danielsandtyler`) tiene Write. El Project es privado.

| Campo | Tipo | Valores |
|---|---|---|
| Status | Single select | Product Backlog, Sprint Backlog, In Progress, In Review, Done, en ese orden |
| Sprint | Iteration | Sprint 1, Sprint 2 y Sprint 3; entrega final mediante milestone |
| Story Points | Number | Pendientes de estimación del equipo |
| Prioridad | Single select | Must have, Should have, Could have, Won't have |
| Tipo | Single select | Historia, Tarea, Bug, Spike; XL de la plantilla pendiente de eliminación definitiva autorizada |

Sprint 2 y Sprint 3 usan los periodos acordados. Sprint 1 tiene inicio 29/08 como referencia de tres semanas, pendiente de confirmación; el cierre es 18/09. Los campos vacíos Start date y Target date de la plantilla también quedan pendientes de limpieza autorizada.

Las etiquetas de la guía existen: `historia-usuario`, `tarea`, `bug`, `spike`, `frontend`, `backend`. Las 38 HU tienen historia-usuario y las 12 tareas del Sprint 2 tienen tarea.

Se crearon los cuatro milestones con cierre 18/09, 09/10, 30/10 y 13/11/2026. Las 13 HU nuevas están asignadas según el plan; las 12 tareas y los registros de Daily, Review y Retrospectiva tienen milestone Sprint 2. Las 25 HU heredadas quedan fuera de los sprints y métricas de R2.

Las seis HU del Sprint 2 y sus 12 tareas están en Sprint Backlog con Sprint 2; la selección sigue pendiente de validar estimaciones y capacidad. HU-29–32 conservan Product Backlog y Sprint 3. HU-33–35 conservan Product Backlog, Sprint vacío y el milestone Entrega final. Cada una de las seis HU del Sprint 2 tiene dos tareas vinculadas mediante sub-issues. No se asignaron responsables ni puntos ficticios.

| Vista | Tipo | Configuración |
|---|---|---|
| Product Backlog | Table | Title, Status, Prioridad, Story Points, Sprint, Assignees, Labels y campos de trazabilidad; filtro `-status:Done`; Prioridad ascendente |
| Sprint Actual | Board | Columnas por Status; filtro `sprint:@current`; Story Points, Assignees, Prioridad y Sub-issues progress; suma de Story Points por columna |
| Roadmap | Roadmap | Fechas por Sprint y agrupación por Prioridad; entrega final representada por su milestone, sin crear Sprint 4 |
| Mis tareas | Table | Filtro `assignee:@me -status:Done` |

Workflows activos: alta → Product Backlog; cierre → Done; merge de PR → Done; reapertura → In Progress; auto-add de issues y PR abiertos de UFOUND; auto-add de sub-issues; PR vinculado → In Review. El cierre automático de issues al moverlos a Done está desactivado para exigir validación real antes del cierre. La prueba temporal #58 verificó auto-add a Product Backlog, cierre a Done y reapertura a In Progress; quedó cerrada, sin Sprint ni puntos, pendiente de borrado definitivo autorizado.

En main está activa [la regla Lab04 - main con PR y aprobacion](https://github.com/MarceloL19/UFOUND/settings/rules/24406992), con PR obligatorio, una aprobación y sin bypass. El PR #57 fue aprobado por Daniel e integrado en main. Cada integrante todavía debe completar un ciclo real: tarea In Progress, rama, commit que referencia el issue, PR con Closes #N, revisión con comentario y aprobación de otro integrante, merge y comprobación de cierre/Done.

## Registros de los eventos

- Daily Scrum: [#41](https://github.com/MarceloL19/UFOUND/issues/41). Registrar al menos dos reuniones reales y las respuestas de cada integrante.
- Sprint Review: [#42](https://github.com/MarceloL19/UFOUND/issues/42). El PO comprueba criterios, acepta las HU demostradas y devuelve lo incompleto al backlog.
- Retrospectiva: [#43](https://github.com/MarceloL19/UFOUND/issues/43). Cada integrante aporta a Start, Stop y Continue; crear acciones como sub-issues para Sprint 3 con responsables.
- Bug ficticio del laboratorio: [#40](https://github.com/MarceloL19/UFOUND/issues/40). Escenario didáctico identificado como simulación; el PO decide cómo tratarlo, sin presentarlo como un defecto comprobado.
- Archivos del repositorio: [#39](https://github.com/MarceloL19/UFOUND/issues/39). Cerrar mediante el PR revisado.
- Configuración del Project y ajustes: [#56](https://github.com/MarceloL19/UFOUND/issues/56). Cerrar después de comprobar su configuración.

## Insights y evidencias

| Gráfico guardado | Configuración | Dato disponible |
|---|---|---|
| [Burn up - Sprint 2](https://github.com/users/MarceloL19/projects/1/insights/4) | Stacked area; Time; Count of items; `is:issue sprint:"Sprint 2" -label:documentation` | 18 ítems abiertos: 6 HU y 12 tareas; cero completados |
| [Carga por integrante](https://github.com/users/MarceloL19/projects/1/insights/2) | Stacked bar; Assignees; Group by Status; Sum of Story Points | Sin puntos estimados; cero no demuestra carga nula |
| [Velocidad del equipo](https://github.com/users/MarceloL19/projects/1/insights/3) | Column; Sprint; Sum of Story Points; `status:Done` | Las HU históricas no tienen Sprint ni puntos; no acreditan velocidad de R2 |

Las tres capturas se tomaron con los datos actuales. El historial comienza con la importación del 03/10/2026; no reproduce avance anterior. Burn up cuenta ítems, incluyendo descomposición, y no mide esfuerzo. Para medir velocidad futura, estimar solo las HU; dejar tareas, PR y seguimiento sin puntos para evitar doble conteo.

La respuesta sobre el Sprint Goal y las acciones si no alcanza la capacidad están en README. No se puede pronosticar el cumplimiento sin disponibilidad, estimaciones y cierres reales. El PO debe priorizar Must, revisar Should, dividir historias grandes y devolver el exceso al backlog.

## Estado verificable de la preparación

| Requisito | Estado |
|---|---|
| 38 HU y distribución de entregas | Issues importados; 25 antecedentes R1, 13 HU nuevas con milestones |
| Tareas de HU del Sprint 2 | 12 issues con Sprint 2, prioridad, Tipo Tarea y vínculos nativos 2 por HU |
| Plantillas, documentación y pruebas recibidas | PR #57 aprobado e integrado; la ejecución nueva de pruebas sigue pendiente |
| Project, vistas, campos, workflows, milestones y protección | Configurados; limpieza definitiva de elementos vacíos pendiente de autorización |
| Acceso del equipo | Equipo de cinco confirmado por Marcelo; cuentas verificadas en esta tabla para Marcelo y Daniel |
| Insights con capturas | Tres gráficos guardados y capturados; datos insuficientes para pronóstico |
| Roles, estimaciones, Daily, Review y Retro | Roles confirmados; estimaciones en el Project y registros reales de ceremonias pendientes |
| Ciclo individual por integrante | Pendiente de trabajo y revisión reales |

La guía no pide implementar todo el Release 2 durante este laboratorio: pide demostrar la gestión y al menos un cambio mínimo por integrante mediante el flujo de PR.
