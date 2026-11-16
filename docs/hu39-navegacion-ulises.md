# HU-39: navegacion y orientacion con Ulises

## Cambio de alcance y trazabilidad

[HU-39 / issue #79](https://github.com/MarceloL19/UFOUND/issues/79) amplía HU-26 y HU-27, implementadas en [PR #60](https://github.com/MarceloL19/UFOUND/pull/60). Marcelo solicitó incorporarla a la entrega final el **16/11/2026**. Las historias originales permanecen cerradas. El backlog original tenía 38 historias (25 antecedentes R1 y 13 nuevas R2); esta ampliación agrega una historia R2, para un total de 39.

La historia está en [el Project](https://github.com/users/MarceloL19/projects/1), con milestone **Entrega final - Release 2** reabierto para representar el trabajo pendiente. Se conserva su fecha histórica de 13/11/2026: el registro nuevo no acredita desarrollo anterior ni cumplimiento de esa fecha. No se crea un Sprint 4. Los Story Points y el responsable quedan pendientes de acuerdo real del equipo; la prioridad Should have es propuesta.

La integración permitió leer el Project y su workflow incorporó automáticamente el issue, pero rechazó `addProjectV2ItemById` y `updateProjectV2ItemFieldValue` con `Resource not accessible by integration`. No se pudo cambiar manualmente Status, Tipo ni Prioridad. La selección de milestone sí quedó guardada en el issue. La edición de esos campos debe completarla alguien con acceso de escritura al Project; no se modificaron permisos ni protecciones.

Este documento describe exclusivamente HU-39. No incorpora los cambios de README, gestion-scrum ni product-backlog de la PR #78, aún pendiente al iniciar este trabajo.

## Historia y criterios

Como estudiante, personal de Seguridad o de Oficina autenticado, quiero que Ulises abra el apartado que solicito y explique al llegar qué hizo y qué puedo hacer allí, para realizar mis tareas con menos pasos y orientación acorde a mi rol.

Los criterios Dado/Cuando/Entonces se registran en #79. La revisión técnica automatizada respalda la propuesta; los criterios del issue no se marcan como aceptados por el PO ni se inventa una ceremonia de revisión.

| Rol | Tema | Destino |
|---|---|---|
| ESTUDIANTE | Reportar una pérdida | `/objetos-perdidos/registrar` |
| ESTUDIANTE | Buscar hallazgos | `/buscar` |
| ESTUDIANTE | Mis reportes / seguimiento | `/objetos-perdidos/mis-reportes` |
| ESTUDIANTE | Coincidencias | `/coincidencias` |
| ESTUDIANTE | Notificaciones | `/notificaciones` |
| SEGURIDAD | Registrar hallazgo | `/objetos-encontrados/registrar` |
| SEGURIDAD | Consultar hallazgos / seguimiento | `/objetos-encontrados` |
| OFICINA | Consultar / gestionar estados | `/oficina/estados` |
| OFICINA | Análisis de pérdidas | `/analisis-perdidas` |
| Los tres | Inicio | `/home` |

El seguimiento lleva al listado autorizado: el usuario elige un objeto para abrir su estado. Ulises no elige un registro particular ni registra, modifica o elimina datos. En coincidencias se aclara que el puntaje no es una probabilidad de propiedad.

## Diseno por capas

- **Servicio:** `AyudaService` reconoce temas del catálogo del rol y devuelve las instrucciones y un destino tipado. El catálogo es determinista; no se añadió una API de IA ni dependencias de producción. Las consultas sin coincidencia única reciben una petición de aclaración sin destino.
- **Controlador:** `AyudaController` obtiene el rol de la sesión, guarda la conversación y emite un token de navegación asociado a una ruta fija. `POST /ayuda/llegada` valida sesión, token y ruta pendiente, agrega la confirmación y consume el token. Cambiar de rol o de cuenta reinicia conversación y destino. El GET de conversación nunca ordena navegar. El historial conserva los diez últimos intercambios completos, incluyendo sus confirmaciones.
- **Vista:** `ayuda.js` abre la ruta devuelta y vuelve a mostrar el chat en el destino. Solo guarda en sessionStorage el estado abierto y el token/ruta transitorios; los mensajes permanecen en la sesión del servidor. La confirmación se solicita al cargar la pantalla coincidente y no se repite al recargar. Un formulario modificado requiere confirmación antes de salir; cancelar conserva sus valores. Los errores de red conservan la pregunta para reintentar.
- **Acceso:** se conservan las validaciones existentes de los controladores de destino. Las rutas incluyen el context path cuando la aplicación se despliega en un subdirectorio.

## Verificacion reproducible

Requisitos: JDK 17, Maven y Node. Para el recorrido de navegador: Python con Playwright y Chromium, una instancia aislada de prueba y una cuenta de cada rol.

```sh
mvn clean test
node --check src/main/resources/static/js/ayuda.js
node --test docs/*.test.cjs
python docs/hu39-navegacion-browser.py --base-url http://localhost:8080 --evidencias work/hu39-browser
git diff --check
```

El script de navegador lee `HU39_ESTUDIANTE_CORREO`, `HU39_ESTUDIANTE_PASSWORD` y las variables equivalentes para `SEGURIDAD` y `OFICINA`. No incorpora contraseñas al repositorio. Navega y conversa sin registrar objetos ni cambiar estados.

En el entorno Cloud se prepararon Maven 3.9.9 y Temurin JDK 17 en el directorio de trabajo. La instancia de navegador usó H2 2.3.232 en memoria, modo MySQL, con tres usuarios ficticios y sin objetos. H2 se añadió únicamente al classpath temporal: no se cambió el pom ni la configuración de producción. No se tocó una base MySQL existente ni Railway.

Resultados y capturas: [evidencia de HU-39](evidencias/hu39/resultado.md).

## Revision y aceptacion pendientes

Otro integrante debe revisar y aprobar la PR; el PO debe comprobar los criterios antes de cerrar #79. Debe repetirse el recorrido con MySQL y datos representativos antes de la aceptación de la entrega. El navegador comprobó las rutas y los estados vacíos, no el contenido de reportes reales ni cambios de estado. H2 no demuestra compatibilidad adicional con MySQL. No se afirma haber desplegado ni probado Railway.
