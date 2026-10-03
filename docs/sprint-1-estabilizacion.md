# Sprint 1 - Estabilización y validación técnica de U-FOUND

Este documento corresponde al Sprint 1 del Release 2, desarrollado en Ingeniería de Software II.

## Objetivo del Sprint

Estabilizar y validar técnicamente la línea base funcional de U-FOUND mediante revisión del código existente y pruebas unitarias del proceso de autenticación.

## Línea base

Las historias de usuario HU-01 a HU-25 corresponden al Release 1 y ya estaban implementadas antes del inicio de Ingeniería de Software II. En este Sprint 1 del Release 2 se consideran la línea base histórica heredada del sistema; no fueron desarrolladas nuevamente durante el Sprint.

## Historias de la línea base revisadas

- HU-01: iniciar sesión.
- HU-02: cerrar sesión.
- HU-03: visualizar home según el rol.

La revisión de código abarcó estas tres historias. La validación automatizada añadida en este Sprint comprueba exclusivamente el comportamiento de autenticación de `AuthService`; no constituye una validación automatizada de HU-02, HU-03 ni de todas las HU-01 a HU-25.

## Componentes revisados

- `AuthController`.
- `AuthService`.
- `UsuarioRepository`.
- `SesionInterceptor`.
- `HomeController`.
- Plantillas de home por rol: estudiante, seguridad y oficina.

## Trabajo realizado

- Revisión del flujo de autenticación.
- Revisión del manejo de sesión y logout.
- Revisión de la redirección del home según los roles `ESTUDIANTE`, `SEGURIDAD` y `OFICINA`.
- Preparación y ejecución de pruebas unitarias automatizadas para `AuthService`.
- Registro de hallazgos técnicos para los siguientes sprints.

## Casos de prueba

| Caso | Entrada o preparación | Resultado esperado | Resultado real |
| --- | --- | --- | --- |
| CP-01 Correo vacío | Correo compuesto únicamente por espacios y contraseña válida. | Se lanza `AuthException` con el mensaje `Ingresa correo y contrasena.` | Aprobado |
| CP-02 Contraseña vacía | Correo válido y contraseña vacía. | Se lanza `AuthException` con el mensaje `Ingresa correo y contrasena.` | Aprobado |
| CP-03 Usuario inexistente o inactivo | El repositorio devuelve `Optional.empty()`. | Se lanza `AuthException` con el mensaje `Credenciales incorrectas.` | Aprobado |
| CP-04 Contraseña incorrecta | El repositorio devuelve un usuario activo y la contraseña ingresada es diferente. | Se lanza `AuthException` con el mensaje `Credenciales incorrectas.` | Aprobado |
| CP-05 Autenticación correcta y normalización | Correo con espacios y mayúsculas; usuario activo con contraseña correcta. | Se consulta el correo normalizado y se devuelve exactamente el usuario encontrado. | Aprobado |

Las pruebas utilizan Mockito para simular `UsuarioRepository`. No cargan el contexto de Spring ni se conectan a MySQL.

La ejecución automatizada real finalizó con 5 pruebas ejecutadas, 0 fallos, 0 errores, 0 pruebas omitidas y `BUILD SUCCESS`. La salida completa y verificable de Maven se conserva en [`docs/evidencias/sprint-1/maven-test-output.txt`](evidencias/sprint-1/maven-test-output.txt).

## Hallazgos técnicos

- El repositorio original no tenía pruebas automatizadas.
- El README utiliza una ruta local específica de Maven.
- Estos hallazgos se documentan como oportunidades de mejora, pero no se corrigen todos en este Sprint para mantener un incremento pequeño y seguro.

## Evidencias

- Salida textual auténtica de `mvn clean test`: [`docs/evidencias/sprint-1/maven-test-output.txt`](evidencias/sprint-1/maven-test-output.txt).

Evidencias manuales pendientes de obtener por el estudiante:

- Captura del login.
- Captura del logout.
- Capturas del home de estudiante, seguridad y oficina.
- Identificador del commit realizado por el estudiante.

## Limitaciones

Las pruebas unitarias automatizadas sí pudieron ejecutarse mediante un repositorio Maven temporal dentro del proyecto. Las validaciones manuales de login, logout y vistas por rol no se ejecutaron porque requieren levantar la aplicación con el entorno MySQL configurado; el estudiante debe realizarlas y tomar las evidencias indicadas. El alcance automatizado de este Sprint no valida la aplicación completa ni reemplaza esas comprobaciones manuales.

## Alcance de esta copia para GitHub

Esta copia resume el alcance de estabilización y conserva la evidencia de pruebas recibida; no sustituye el informe completo de la primera entrega.
