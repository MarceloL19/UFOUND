# Evidencia tecnica HU-39

Fecha de ejecución: 16/11/2026 (America/Lima). Rama: `feature/HU-39-navegacion-ulises`.
Base comparada: `main` en `c4f0dbbfd14f02ddc95fd89f51438637ebc647ca`.

| Comprobación | Resultado |
|---|---|
| Base original: `mvn clean test` | 105 casos, 0 fallos, 0 errores, 2 omitidos; BUILD SUCCESS |
| Propuesta: `mvn clean test` | 136 casos, 0 fallos, 0 errores, 2 omitidos; BUILD SUCCESS |
| `mvn package -DskipTests` después de la suite | BUILD SUCCESS; jar ejecutable generado |
| `node --test docs/*.test.cjs` | 23 pruebas, 23 correctas, 0 fallos |
| `node --check src/main/resources/static/js/ayuda.js` | Correcto |
| Chromium / Playwright, tres roles | 26 comprobaciones correctas; sin errores JavaScript de página |
| `git diff --check` | Sin errores de espacios |

Se agregaron 31 casos Java respecto de main. El servicio cubre los destinos por rol, todas las preguntas de los botones, consultas ambiguas y solicitudes de otro rol. MockMvc comprueba autenticación, validación de preguntas, token y ruta, consumo único, aislamiento de sesiones, cambio de rol/cuenta, invalidación del destino anterior, context path e historial limitado por intercambios completos.

Los dos casos omitidos pertenecen a `RegistroCoincidenciaMySqlTest` (`registraPrimeroPerdidoYLuegoEncontradoConFotoYPersisteAtributos` y `registraPrimeroEncontradoYLuegoPerdidoSinFotoYUsaLaBase`). También estaban omitidos en la base original. No se presenta la suite como una comprobación de MySQL.

## Navegador

Se ejecutó la aplicación real con Spring Boot, las plantillas Thymeleaf y sus recursos, usando H2 2.3.232 temporal en memoria, modo MySQL. Se crearon exclusivamente tres cuentas ficticias en esa base aislada, sin objetos ni datos reales. No se agregó H2 al pom ni a la configuración del repositorio. JDK: Temurin 17.0.21; Maven: 3.9.9.

[Detalle de las 26 comprobaciones](navegador.txt): quince solicitudes de navegación; recarga por rol; consultas ambiguas y ajenas; cancelar y aceptar la salida de formularios de estudiante y Seguridad; panel visible a 390×844 por rol; fallo de red y sesión vencida. Las capturas de escritorio usan 1366×900.

Se inspeccionaron visualmente capturas del panel en móvil para estudiante y Seguridad y en escritorio para Oficina: mensajes legibles, entrada y botón de envío visibles, y conservación de los colores azul y naranja. También se comprobó automáticamente que el panel y la entrada permanecieran dentro del viewport móvil para los tres roles.

| Rol | Escritorio | Móvil |
|---|---|---|
| ESTUDIANTE | [Captura](estudiante-escritorio.png) | [Captura](estudiante-movil.png) |
| SEGURIDAD | [Captura](seguridad-escritorio.png) | [Captura](seguridad-movil.png) |
| OFICINA | [Captura](oficina-escritorio.png) | [Captura](oficina-movil.png) |

## Limites

Faltan la ejecución con MySQL y datos representativos, la revisión y aprobación de otro integrante y la aceptación del PO. No se probó una base existente ni Railway. Las casillas de aceptación del issue permanecen pendientes; esta evidencia no representa una ceremonia Scrum ni una aprobación del equipo.
