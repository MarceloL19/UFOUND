# Primera propuesta de asistente — HU26 y HU27

Esta propuesta fue generada por Codex y revisada y validada manualmente por el estudiante el 7 de octubre de 2026. Sigue pendiente la revisión del equipo y no constituye cierre de las historias.

## Alcance y flujo

- En las páginas autenticadas que usan el fragmento de navegación aparece la burbuja de Ulises. Al abrirla, `GET /ayuda/conversacion` devuelve saludo, historial y temas del rol; JavaScript los presenta en la ventana. `GET /ayuda` abre esa misma ventana en una página de ayuda.
- La ventana envía `POST /ayuda/conversacion` con `pregunta` de 1 a 300 caracteres. El controlador usa `AyudaService`, agrega pregunta y respuesta al historial de sesión y devuelve el estado actualizado. `POST /ayuda` sigue disponible como ruta de formulario sencilla. Si no hay una intención única, el servicio explica el límite y enumera los temas del rol.
- El historial conserva el saludo y los últimos diez intercambios en `HttpSession`. No se escribe en MySQL. `GET /logout` invalida esa sesión según el controlador existente; si el rol de la sesión cambiara, el historial de ayuda se reinicia.
- Las respuestas son instrucciones fijas verificadas con las rutas actuales. El asistente no consulta registros, no cambia estados y no usa un servicio externo de IA.

## Punto de reutilización para HU28

Daniel puede enlazar a `GET /ayuda?tema=reportar-perdida` desde una pantalla de registro de pérdidas, o a `GET /ayuda?tema=registrar-hallazgo` desde una pantalla de registro de hallazgos. El parámetro **solo precarga** la pregunta sugerida cuando el tema pertenece al rol autenticado; el usuario decide enviarla. Sin parámetro, se abre la conversación normal. También puede consultar `GET /ayuda/conversacion?tema=...` si más adelante integra la ventana en otro contexto. Ningún formulario de HU28 se cambia aquí.

Los identificadores de tema disponibles son:

| Rol | `tema` admitido |
| --- | --- |
| ESTUDIANTE | `reportar-perdida`, `buscar-hallazgos`, `mis-reportes`, `seguimiento` |
| SEGURIDAD | `registrar-hallazgo`, `consultar-hallazgos`, `seguimiento-hallazgo` |
| OFICINA | `consultar-estados`, `gestionar-estados` |

Un identificador de otro rol o desconocido no precarga una pregunta ni da acceso a una función ajena.

## Decisiones de diseño para revisar

- Se mantiene MVC y la arquitectura en capas: el controlador recibe solicitudes, usa la sesión y prepara la vista; `AyudaService` concentra catálogo e interpretación. Esto separa responsabilidades y evita duplicar frases en la plantilla.
- No se añadió Strategy ni una jerarquía de asistentes: son tres roles existentes y un catálogo pequeño, sin algoritmos intercambiables. Si el contenido crece, el estudiante puede revisar esa decisión.
- La coincidencia es literal tras normalizar mayúsculas, acentos y puntuación. Una pregunta con cero o varias intenciones devuelve el límite; la única excepción es «seguimiento de mi reporte», donde «mi reporte» identifica el objeto de la consulta. No se pretende comprensión de lenguaje natural general.
- «Registro» se refiere a reportar objetos, no a crear cuentas. Las consultas sobre cuentas o credenciales quedan fuera del contenido.
- Los mensajes se insertan en el DOM con `textContent`, sin interpretar HTML escrito por el usuario.

## Imágenes existentes

- El logo de la aplicación está en `src/main/resources/static/img/logo-ufound.png` y se sirve como `/img/logo-ufound.png`. La imagen original de Ulises adjunta para esta revisión se copió sin modificar a `src/main/resources/static/img/ulises-asistente.png` y se sirve como `/img/ulises-asistente.png`.
- Las fotos subidas por usuarios se guardan fuera de `src/main/resources`: la URL en MySQL tiene forma `/uploads/objetos-perdidos/<archivo>` o `/uploads/objetos-encontrados/<archivo>`, y el archivo físico está bajo el directorio `uploads` del proceso Java. Ese directorio está ignorado por Git. Además, el manejador original de Spring podía registrar la ubicación sin barra final si `uploads` aún no existía al iniciar, lo que producía 404 incluso al agregar una foto después; ahora la ubicación siempre termina en `/`. Si una fila de MySQL apunta a un archivo que ya no existe, el navegador muestra «Foto no disponible»; el proyecto no inventa una foto ni cambia la fila.
- Las 52 fotos predeterminadas aportadas en `fotos.zip` están en `src/main/resources/static/fotos/` y se sirven como `/fotos/<nombre>`. La lista exacta está en [fotos-predeterminadas.md](fotos-predeterminadas.md). `data.sql` no agrega objetos ni asigna fotos a filas de MySQL.
- Para reutilizar archivos originales ubicados en otra carpeta, define `UFOUND_UPLOAD_DIR` con la **ruta absoluta del directorio que contiene** `objetos-perdidos` y `objetos-encontrados` antes de iniciar Java. `UploadService` y el recurso `/uploads/**` usan esa misma ubicación. No se copian ni se suben imágenes de usuarios automáticamente.
- Para identificar los nombres exactos que faltan en tu instalación, consulta en MySQL `SELECT imagen_url FROM objetos_perdidos WHERE imagen_url IS NOT NULL UNION SELECT imagen_url FROM objetos_encontrados WHERE imagen_url IS NOT NULL;`. Compara las rutas `/fotos/...` con las fotos predeterminadas y las rutas `/uploads/...` con los archivos originales bajo `UFOUND_UPLOAD_DIR`. Esta copia local no incluye una exportación de esas filas.

## Validación manual comunicada por el estudiante

El estudiante comprobó en UFOUND que la ventana abre y acepta consultas, que las opciones cambian según el rol, que las respuestas cubren registro de objetos, búsqueda y seguimiento, y que una pregunta ajena obtiene un mensaje de límite y los temas permitidos. También confirmó que las fotos predeterminadas se muestran después de la corrección de rutas. Una prueba de borrado en MySQL requirió confirmar con `COMMIT` para reflejarse en la página; no era un defecto de código. Codex no consultó ni verificó la base local del estudiante.

## Comprobaciones adicionales sugeridas al equipo

1. Abrir `/ayuda` y `/ayuda/conversacion` sin sesión: deben llevar a `/login`.
2. Entrar con cada rol: confirmar burbuja, saludo y temas propios, y ausencia de opciones ajenas.
3. Escribir preguntas sobre registro de objetos, búsqueda y seguimiento; revisar respuestas, rutas y los mensajes consecutivos. Cerrar y reabrir la ventana y cambiar de página para revisar el historial.
4. Enviar una consulta ajena y otra que mezcle dos temas; comprobar el límite y las opciones del rol.
5. Abrir otra pestaña con la misma sesión y revisar el historial; cerrar sesión, iniciar una nueva y comprobar que el historial anterior no aparece.
6. Abrir las dos URL con `tema` de registro desde el rol correcto y desde otro rol.
7. Revisar la ventana en escritorio y móvil: escritura, botón Enviar, Enter, Shift+Enter, cierre con Escape y desplazamiento del historial.
8. Comprobar una foto real cuya URL esté en MySQL y cuyo archivo siga en `UFOUND_UPLOAD_DIR`; luego comprobar el aviso de una foto ausente.
