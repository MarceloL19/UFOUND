# Evidencia de mejoras visuales (#81)

Ejecución: 16/11/2026, America/Lima. Base inicial de main: `c4f0dbbfd14f02ddc95fd89f51438637ebc647ca`. Rama `style/mejoras-visuales-entrega`.

| Verificación | Resultado |
|---|---|
| `mvn clean verify`, JDK 17 | 114 pruebas, 0 fallos, 0 errores, 2 omitidas; BUILD SUCCESS y jar empaquetado |
| `node --test docs/*.test.cjs` | 23 correctas, 0 fallos |
| `node --check src/main/resources/static/js/ayuda.js` | Correcto |
| Chromium / Playwright | 19 grupos de comprobaciones correctos, tres roles |
| Scripts de datos, MariaDB 11.4.5 aislada | Carga 48/36, repetición sin duplicados, retirada selectiva y protección del caso gestionado |
| `git diff --check` | Correcto |

Se agregaron nueve casos Java respecto de main: siete verificaciones de controlador/vista (incluidas variantes con/sin foto) y dos de consulta propia. Cubren la foto y el resumen, escape de texto, rol ajeno, reporte inexistente/ajeno y aviso flash de hallazgo conservando la redirección existente.

Las dos pruebas omitidas son los dos casos de `RegistroCoincidenciaMySqlTest`, ya omitidos en la línea base. La prueba de navegador usó una instancia aislada de MariaDB con un driver MariaDB añadido únicamente al classpath de trabajo; no se cambió el driver MySQL ni las dependencias de producción del repositorio. Falta validar con la versión concreta de MySQL del usuario.

## Recorrido visual

[Resultado del navegador](navegador.txt). Se revisaron inicio, menú, formularios, paneles IA, confirmaciones con/sin foto, asistente, estadísticas y análisis. Todos los destinos del menú de cada rol se comprobaron a anchos de **1440, 1024, 768 y 390 px**, sin desbordamiento horizontal de la página; las tablas anchas tienen desplazamiento interno. Las capturas principales usan 1440×1000 y 390×844.

El flujo de registro creó cuatro objetos por recorrido en la base aislada; hubo varias ejecuciones durante el ajuste visual. Los totales de las capturas incluyen el conjunto demo, dos controles ajenos al conjunto y esos registros de prueba. Las coincidencias y notificaciones que muestran las capturas fueron calculadas por los servicios existentes durante esos registros, no introducidas como cifras de decoración.

Para presentar los paneles IA se controló la respuesta de detección de imagen. Categoría, autocompletado, deshacer y registros con imágenes operaron con los endpoints reales. Esta prueba valida la presentación y la conservación de controles, no la precisión del modelo.

| Pantalla | Capturas |
|---|---|
| Inicio estudiante | [Escritorio](estudiante-inicio-escritorio.png), [móvil](estudiante-inicio-movil.png) |
| Inicio Seguridad | [Escritorio](seguridad-inicio-escritorio.png) |
| Inicio y estadísticas Oficina | [Escritorio](oficina-inicio-escritorio.png), [móvil](oficina-inicio-movil.png) |
| Paneles IA estudiante | [Escritorio](estudiante-ia-escritorio.png), [móvil](estudiante-ia-movil.png) |
| Paneles IA Seguridad | [Escritorio](seguridad-ia-escritorio.png) |
| Confirmación pérdida | [Con foto](estudiante-confirmacion-foto-escritorio.png), [sin foto en móvil](estudiante-confirmacion-sin-foto-movil.png) |
| Confirmación hallazgo | [Con foto](seguridad-confirmacion-foto-escritorio.png), [sin foto en móvil](seguridad-confirmacion-sin-foto-movil.png) |
| Asistente | [Móvil](estudiante-asistente-movil.png) |
| Análisis de pérdidas | [Escritorio](oficina-analisis-escritorio.png), [móvil](oficina-analisis-movil.png) |

Se inspeccionaron visualmente las tarjetas e iconos del estudiante, los paneles IA de estudiante en escritorio/móvil, la confirmación de pérdida con fotografía, el inicio/estadísticas de Oficina y la confirmación de hallazgo sin fotografía en móvil.

## Datos y revisión pendientes

[Resultado de los scripts de datos](datos-demo.txt). La carga y retirada se ejecutaron en una base nueva de trabajo. No se accedió al disco de Windows ni a la base local del usuario, y no se desplegó en Railway. La adopción de los datos de demostración en la exposición requiere ejecutar el script opcional en el esquema elegido.

La revisión de otro integrante y la aceptación visual de Marcelo/equipo siguen pendientes. La tarea no se presenta como una nueva HU ni como aceptación del PO ya realizada.
