# HU-35 - Sugerir posibles coincidencias automáticamente al usuario

## Objetivo

Cuando un estudiante consulta el detalle de uno de sus objetos perdidos, UFOUND muestra las coincidencias que el motor existente ya calculó y guardó para ese reporte. El estudiante puede abrir el detalle de cada coincidencia para revisar el objeto encontrado.

La sugerencia es informativa: consultar una coincidencia no confirma la propiedad del objeto, no cambia estados y no registra una entrega.

## Flujo implementado

1. El estudiante entra a **Mis objetos perdidos**.
2. Desde uno de sus reportes selecciona **Ver detalle y sugerencias**.
3. El endpoint `GET /objetos-perdidos/detalle/{id}` valida que la sesión corresponda a un estudiante.
4. `Hu35Service` recupera el objeto perdido y verifica que pertenezca al estudiante autenticado.
5. Se consultan las entidades `Coincidencia` asociadas a ese objeto, ordenadas por porcentaje de similitud descendente y fecha de detección descendente.
6. La vista `detalle-objeto-perdido.html` presenta los datos del reporte y las posibles coincidencias.
7. Al seleccionar **Consultar detalle**, se navega a `GET /coincidencias/{id}`. La autorización existente vuelve a comprobar que la coincidencia pertenezca al estudiante.

## Alcance y preservación

- No se modifica el algoritmo de `CoincidenciaService` ni sus umbrales.
- No se vuelve a calcular ni se persiste una coincidencia al abrir el detalle; la pantalla consulta las coincidencias ya registradas por el flujo de registro de objetos.
- No se modifican los estados de objetos ni el flujo de entrega.
- No se modifican las funcionalidades de HU-30, HU-31 o HU-32.
- La consulta de sugerencias está limitada al propietario estudiante del objeto perdido.
- Si el reporte no existe o pertenece a otro usuario, se rechaza la consulta con un mensaje genérico.
- Si no hay coincidencias registradas, se presenta un estado vacío informativo.

## Integración con HU-33 y HU-34

La copia de `main` utilizada como base contiene el motor de coincidencias existente en `CoincidenciaService` y el ordenamiento por porcentaje y fecha en `CoincidenciaRepository`. Sin embargo, el backlog de esta misma copia registra HU-33 y HU-34 como pendientes y no se encontraron implementaciones separadas de esas historias en el código recibido.

Por ello, HU-35 consume las coincidencias existentes sin duplicar ni anticipar el algoritmo avanzado de HU-33 ni las reglas adicionales de desempate de HU-34. Cuando esas historias se integren a `main`, esta pantalla consumirá los registros producidos por el motor integrado.

## Archivos agregados

- `src/main/java/pe/edu/ulima/ufound/hu35/DetalleObjetoPerdidoHu35.java`
- `src/main/java/pe/edu/ulima/ufound/hu35/Hu35Service.java`
- `src/main/java/pe/edu/ulima/ufound/hu35/Hu35Controller.java`
- `src/main/resources/templates/detalle-objeto-perdido.html`
- `src/test/java/pe/edu/ulima/ufound/hu35/Hu35ServiceTest.java`
- `src/test/java/pe/edu/ulima/ufound/hu35/Hu35ControllerTest.java`

## Archivos existentes con cambios mínimos

- `CoincidenciaRepository`: método de consulta por objeto perdido con carga de `objetoEncontrado` y ordenamiento existente.
- `mis-objetos-perdidos.html`: enlace para acceder al nuevo detalle desde cada reporte.

## Pruebas incorporadas

- El propietario puede consultar su reporte y recibir coincidencias.
- Un reporte sin coincidencias devuelve una lista vacía.
- Otro estudiante no puede consultar el reporte.
- Un reporte inexistente es rechazado.
- El endpoint devuelve la vista de detalle para un estudiante.
- Los roles distintos de estudiante son rechazados.
- Los errores de autorización o inexistencia regresan a Mis objetos perdidos.

## Validación manual recomendada

1. Iniciar sesión con un usuario de rol `ESTUDIANTE`.
2. Abrir **Mis objetos perdidos** y entrar al detalle de un reporte propio.
3. Verificar que se muestran sus datos y las coincidencias existentes en orden descendente de similitud.
4. Abrir **Consultar detalle** en una sugerencia y verificar los datos del objeto encontrado.
5. Confirmar que la navegación no cambia el estado del objeto ni crea una entrega.
6. Probar un reporte sin coincidencias.
7. Intentar acceder al detalle de un reporte perteneciente a otro estudiante y verificar que se deniega el acceso.
8. Iniciar sesión como `SEGURIDAD` u `OFICINA` y verificar que no se permite el endpoint HU-35.

La suite Maven debe ejecutarse en un entorno que tenga Maven, Java 17 o superior y las dependencias del proyecto disponibles mediante `mvn test`.
