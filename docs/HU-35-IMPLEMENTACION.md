# HU-35 - Sugerir posibles coincidencias automáticamente al usuario

## Objetivo

Cuando un estudiante consulta el detalle de uno de sus objetos perdidos, UFOUND muestra las coincidencias que el motor existente ya calculó y guardó para ese reporte. El estudiante puede abrir el detalle de cada coincidencia para revisar el objeto encontrado.

La sugerencia es informativa: consultar una coincidencia no confirma la propiedad del objeto, no cambia estados y no registra una entrega.

## Flujo implementado

1. El estudiante entra a **Mis objetos perdidos**.
2. Desde uno de sus reportes selecciona **Ver detalle y sugerencias**.
3. El endpoint `GET /objetos-perdidos/detalle/{id}` valida que la sesión corresponda a un estudiante.
4. `Hu35Service` recupera el objeto perdido y verifica que pertenezca al estudiante autenticado.
5. Se consultan las entidades `Coincidencia` asociadas a ese objeto y se ordenan por puntaje descendente, fecha de detección descendente e ID descendente.
6. La vista `detalle-objeto-perdido.html` presenta los datos del reporte y las posibles coincidencias.
7. Al seleccionar **Consultar detalle**, se navega a `GET /coincidencias/{id}`. La autorización existente vuelve a comprobar que la coincidencia pertenezca al estudiante.

## Alcance y preservación

- No se modifica el algoritmo de `CoincidenciaService` ni sus umbrales.
- No se vuelve a calcular ni se persiste una coincidencia al abrir el detalle; la pantalla consulta las coincidencias ya registradas por el flujo de registro de objetos.
- No se modifican los estados de objetos ni el flujo de entrega.
- No se modifican las funcionalidades de HU-30, HU-31 o HU-32.
- La consulta de sugerencias está limitada al propietario estudiante del objeto perdido.
- Si el reporte no existe o pertenece a otro usuario, se vuelve a **Mis objetos perdidos** y se muestra el mismo mensaje genérico en ambos casos.
- Si no hay coincidencias registradas, se presenta un estado vacío informativo.

## Integración con HU-33 y HU-34

Esta rama incorpora el `main` que ya contiene HU-33 (PR #73). HU-35 presenta el porcentaje guardado por ese motor: no recalcula las coincidencias al abrir la página ni modifica las existentes.

HU-34 (PR #75) aún no está integrada en `main`. Mientras tanto, `Hu35Service` aplica el mismo criterio acordado allí: porcentaje de similitud descendente, fecha de detección descendente e ID de coincidencia descendente. El repositorio consulta las coincidencias del reporte y carga el objeto encontrado; el servicio ordena incluso si llegan en otro orden. Cuando HU-34 se integre, sustituir el comparador local por `OrdenCoincidencias.POR_RELEVANCIA` para mantener una sola definición y repetir las pruebas sobre la combinación final.

## Archivos agregados

- `src/main/java/pe/edu/ulima/ufound/hu35/DetalleObjetoPerdidoHu35.java`
- `src/main/java/pe/edu/ulima/ufound/hu35/Hu35Service.java`
- `src/main/java/pe/edu/ulima/ufound/hu35/Hu35Controller.java`
- `src/main/resources/templates/detalle-objeto-perdido.html`
- `src/test/java/pe/edu/ulima/ufound/hu35/Hu35ServiceTest.java`
- `src/test/java/pe/edu/ulima/ufound/hu35/Hu35ControllerTest.java`

## Archivos existentes con cambios mínimos

- `CoincidenciaRepository`: método de consulta por objeto perdido con carga de `objetoEncontrado`.
- `mis-objetos-perdidos.html`: enlace para acceder al detalle y aviso visible al regresar por acceso denegado.

## Pruebas incorporadas

- El propietario puede consultar su reporte y recibir coincidencias.
- La consulta ordena por puntaje, fecha e ID aunque el repositorio devuelva otra secuencia.
- Un reporte sin coincidencias devuelve una lista vacía.
- Otro estudiante no puede consultar el reporte.
- Un reporte inexistente es rechazado.
- El endpoint devuelve la vista de detalle para un estudiante.
- Los roles distintos de estudiante son rechazados.
- Los errores de autorización o inexistencia regresan a Mis objetos perdidos y el aviso llega al modelo de esa pantalla.

## Validación manual recomendada

1. Iniciar sesión con un usuario de rol `ESTUDIANTE`.
2. Abrir **Mis objetos perdidos** y entrar al detalle de un reporte propio.
3. Verificar que se muestran sus datos y las coincidencias existentes por puntaje descendente; en empates, comprobar fecha más reciente e ID mayor primero.
4. Abrir **Consultar detalle** en una sugerencia y verificar los datos del objeto encontrado.
5. Confirmar que la navegación no cambia el estado del objeto ni crea una entrega.
6. Probar un reporte sin coincidencias.
7. Intentar acceder al detalle de un reporte ajeno y a un ID inexistente; en ambos casos, verificar que el aviso genérico aparece en **Mis objetos perdidos**.
8. Iniciar sesión como `SEGURIDAD` u `OFICINA` y verificar que no se permite el endpoint HU-35.

La suite Maven debe ejecutarse en un entorno que tenga Maven, Java 17 o superior y las dependencias del proyecto disponibles mediante `mvn test`.
