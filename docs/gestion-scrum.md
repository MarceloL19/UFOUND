# Gestión Scrum de UFOUND — Ingeniería de Software II

Se aplica el laboratorio 4 al proyecto de objetos perdidos y encontrados UFOUND. El caso de reservas de salas de la guía no sustituye el alcance del producto.

## Convención vigente de releases y sprints

La aplicación de **Ingeniería de Software I** constituye la **línea base heredada**. Sus funcionalidades no representan el Release 1 de Ingeniería de Software II.

El curso actual comprende **dos releases con dos sprints cada uno**:

- Release 1: Sprint 1 (R1-S1) y Sprint 2 (R1-S2).
- Release 2: Sprint 3 (R2-S1) y Sprint 4 (R2-S2).

La cuarta etapa antes llamada «Entrega final» corresponde al Sprint 4. Los documentos y comentarios históricos que llaman «R1» a la base anterior o «R2» a todo el curso deben interpretarse con esta aclaración; no se reescriben fechas ni hechos históricos para ajustarlos a la nueva nomenclatura.

## Cronograma y objetivos

| Release de Software II | Sprint global | Periodo de referencia | Hito de cierre registrado | Objetivo y alcance |
|---|---|---|---|---|
| Release 1 | Sprint 1 (R1-S1) | Inicio de referencia: 29/08/2026, por confirmar | 18/09/2026 | Revisar y estabilizar la aplicación heredada: autenticación, sesión y acceso por rol; pruebas iniciales. |
| Release 1 | Sprint 2 (R1-S2) | 19/09–09/10/2026 | 09/10/2026 | Asistencia y ayuda contextual (HU-26–28) y estadísticas descriptivas (HU-36–38). |
| Release 2 | Sprint 3 (R2-S1) | 10/10–30/10/2026 | 30/10/2026 | Registro asistido por imágenes: validación, detección, categoría y autocompletado editable (HU-29–32). |
| Release 2 | Sprint 4 (R2-S2) | 31/10–13/11/2026 | 13/11/2026 | Coincidencias con atributos visuales, ordenamiento y sugerencias (HU-33–35), integración, pruebas y documentación. |

Las fechas anteriores son los hitos del cronograma registrado. La entrega del informe final es el **16/11/2026** y la exposición el **17/11/2026**; estas fechas no sustituyen las fechas reales de commits, pruebas, aceptaciones ni publicaciones de evidencias. Las mejoras finales adicionales incluyen la navegación de Ulises (HU-39, issue #79) y los ajustes visuales de integración.

Las HU-26–38 son las historias de evolución originalmente registradas para el curso. Las HU-01–25 son antecedentes: su importación o cierre administrativo no acredita implementación nueva ni velocidad. Si se trabaja sobre alguna de ellas, debe documentarse la tarea real de estabilización y su evidencia.

## Equipo y responsabilidades registradas

La siguiente asignación de roles corresponde al registro del Sprint 2; no acredita por sí sola que cada persona haya ejecutado todas las actividades propias del rol durante todo el proyecto.

| Integrante | Cuenta identificada en la documentación original | Rol registrado en Sprint 2 |
|---|---|---|
| Gabriel Revoredo | Por registrar en la tabla original | Scrum Master |
| Sergio Chaparro | Por registrar en la tabla original | Product Owner |
| Marcelo Franco Loayza Taboada | MarceloL19 | Developer |
| Rodrigo Figueroa | Por registrar en la tabla original | Developer |
| Daniel Joseph Sandoval Chavarria | danielsandtyler | Developer |

Las contribuciones y eventuales cambios de responsabilidad se sustentan mediante issues, PR y registros individuales. Las cuentas pendientes de la tabla original no implican ausencia de participación.

## Evidencia histórica del Sprint 1

El Sprint 1 de Software II revisa y estabiliza la base heredada. La primera entrega documentó ocho tareas y 13 puntos de tareas; no equivalen a Story Points de las 25 historias heredadas ni a capacidad de futuros sprints.

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

La evidencia original de AuthService registra cinco pruebas sin fallos ni errores el **20/09/2026**, después del hito del 18/09. No acredita una ejecución el 18/09, una ejecución nueva durante esta actualización documental ni la validación completa de sesiones, rutas y MySQL. Véase [el informe histórico de estabilización](sprint-1-estabilizacion.md).

## Product Backlog, estimaciones y seguimiento

- [Product Backlog](product-backlog.md).
- [Project UFOUND - Gestión Scrum IS2](https://github.com/users/MarceloL19/projects/1).
- Configuración del laboratorio: [#39](https://github.com/MarceloL19/UFOUND/issues/39) y [#56](https://github.com/MarceloL19/UFOUND/issues/56).
- Plantillas, documentación inicial y evidencia recibida: [PR #57](https://github.com/MarceloL19/UFOUND/pull/57).

La configuración inicial registrada incluyó estados Product Backlog, Sprint Backlog, In Progress, In Review y Done; prioridad MoSCoW; tipo de elemento; responsables; iteración Sprint; y Story Points numéricos. Las vistas son Product Backlog, Sprint Actual, Roadmap y Mis tareas.

Aquella configuración inicial tenía iteraciones Sprint 1–3 y un milestone «Entrega final». Es una descripción histórica de la herramienta, no la organización académica vigente: el cuarto incremento corresponde a **R2-S2 / Sprint 4**. Esta corrección de archivos no verifica ni modifica por sí misma los campos del Project, sus iteraciones o milestones.

Para las métricas:

1. Estimar historias con el equipo y registrar el acuerdo; la guía propone Fibonacci 1, 2, 3, 5, 8 y 13.
2. Comparar el alcance seleccionado con la disponibilidad y capacidad acordadas.
3. Sumar Story Points de historias, sin repetirlos en tareas, PR, reuniones o documentación.
4. No convertir automáticamente puntos de tareas históricos en Story Points de historias.
5. No deducir capacidad ni velocidad a partir de campos vacíos o cierres de historias importadas.
6. Identificar cada gráfico con su fecha, filtro, unidad y alcance.

El comentario de planificación del Sprint 2 contiene 29 SP y, a la vez, deja pendiente validar capacidad y estimaciones. Debe interpretarse en su contexto; no demuestra por sí solo una capacidad acordada. Los datos actuales requieren su evidencia específica.

## Registros de eventos y mejora del proceso

| Registro | Evidencia disponible y alcance |
|---|---|
| [Daily y planificación #41](https://github.com/MarceloL19/UFOUND/issues/41) | Planificación por chat publicada el 07/10, referida al 06/10, con objetivo, historias y responsabilidades del Sprint 2. El comentario del 16/11 documenta retrospectivamente dos Daily confirmados, sin inventar fechas exactas ni intervenciones individuales. |
| [Sprint Review #42](https://github.com/MarceloL19/UFOUND/issues/42) | Aceptación de HU-26 y HU-27 registrada el 11/10 y evidencia de ejecución de 28 pruebas el 12/10. Los resultados corresponden a esas fechas y versión; una suite satisfactoria no sustituye la verificación de todos los criterios funcionales. |
| [Retrospectiva #43](https://github.com/MarceloL19/UFOUND/issues/43) | Balance individual publicado el 16/11 con Start, Stop, Continue y acción de mejora. Incluye actividades posteriores al Sprint 2; no se atribuye todo al mismo sprint ni representa el aporte de los demás integrantes. Su nomenclatura histórica se interpreta con la convención vigente. |
| [Seguimiento #64](https://github.com/MarceloL19/UFOUND/issues/64) | Referencia de seguimiento para conciliación del cierre y actualización documental. |
| [Caso didáctico #40](https://github.com/MarceloL19/UFOUND/issues/40) | Bug simulado del laboratorio; no se presenta como defecto real comprobado. |

Los eventos y cierres de los demás sprints deben sustentarse con sus propios registros. La existencia de una plantilla o issue no demuestra que un evento haya ocurrido. Se conservan las fechas reales de publicación y se distingue el registro retrospectivo.

## Incrementos y evidencias de implementación

| Etapa del curso | Referencias |
|---|---|
| R1-S1 / Sprint 1 | Pruebas originales de AuthService y [documento de estabilización](sprint-1-estabilizacion.md). |
| R1-S2 / Sprint 2 | HU-26–28 y HU-36–38; [Review #42](https://github.com/MarceloL19/UFOUND/issues/42), PR #60 y #61 y referencias de cada historia. |
| R2-S1 / Sprint 3 | HU-29–32; PR #65, #67, #68 y [#69](https://github.com/MarceloL19/UFOUND/pull/69). |
| R2-S2 / Sprint 4 y mejoras finales | HU-33–35; PR [#73](https://github.com/MarceloL19/UFOUND/pull/73), [#75](https://github.com/MarceloL19/UFOUND/pull/75) y #76. Mejoras finales adicionales: HU-39 (issue #79), PR #80 y #82. |

La evaluación de HU-33 con 30 pares obtuvo los mismos resultados de clasificación que el motor base en los umbrales 40 y 60. La incorporación visual está implementada, pero la muestra no demuestra una mejora de precisión. Las pruebas omitidas, pendientes o condicionadas a MySQL deben identificarse como tales según la ejecución concreta.

## Insights y límites de interpretación

- [Burn up - Sprint 2](https://github.com/users/MarceloL19/projects/1/insights/4).
- [Carga por integrante](https://github.com/users/MarceloL19/projects/1/insights/2).
- [Velocidad del equipo](https://github.com/users/MarceloL19/projects/1/insights/3).

La captura inicial del Burn up mostraba 18 ítems (6 historias y 12 tareas) y cero cierres. Ese dato es histórico, no el estado actual. Contar historias y tareas juntas no equivale a medir esfuerzo. El historial comienza con la importación del 03/10 y no reproduce avance previo.

Para el informe final se interpretan gráficos por sprint, release y proyecto con datos verificables. No se reconstruyen curvas diarias sin registros ni se presenta una configuración de gráfico como prueba de cumplimiento del Sprint Goal.
