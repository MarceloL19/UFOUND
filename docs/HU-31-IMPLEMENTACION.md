# HU-31 - Sugerir categoría automática del objeto

## Qué se añadió

HU-31 **no modifica ni reemplaza la detección visual HU-30**. Cuando HU-30 analiza una imagen,
emite un evento que un script nuevo aprovecha para solicitar una sugerencia de categoría real.
El usuario puede pulsar **Usar categoría sugerida** o elegir cualquier otra opción.
La categoría guardada por los servicios existentes es **la que aparece seleccionada en el formulario**.
No se modificó la base de datos, las entidades, los formularios Java ni los servicios de registro.

### Regla del catálogo real

| Resultado de HU-30 | CategoriaObjeto |
| --- | --- |
| Mochila, Bolso, Maleta | ACCESORIOS |
| Botella | BOTELLAS |
| Laptop, Mouse, Celular | TECNOLOGIA |
| Libro | UTILES |

Las opciones del catálogo existente son TECNOLOGIA, DOCUMENTOS, ACCESORIOS, ROPA,
UTILES, BOTELLAS, OTROS. No se inventan categorías como `MOCHILAS`.
Los tipos no soportados y las detecciones insuficientes no generan sugerencia.

### Flujo

1. Estudiante o Seguridad selecciona imagen y pulsa **Analizar imagen**.
2. HU-29 valida y HU-30 ejecuta la detección una única vez (`POST /imagenes/analizar`).
3. HU-30 emite `ufound:imagen-analizada` solo si la respuesta fue exitosa y vigente.
4. HU-31 consulta `POST /hu31/sugerir-categoria` con el tipo detectado.
5. HU-31 muestra la categoría sugerida, sin alterar aún la categoría del formulario.
6. El usuario pulsa **Usar categoría sugerida**, o selecciona otra categoría manualmente.
7. El formulario existente guarda la categoría finalmente seleccionada. Ningún campo oculto la suplanta.

### Pruebas IntelliJ

1. Importar el proyecto Maven con JDK 17 (o superior compatible).
2. Ejecutar `CategoriaSugerenciaServiceTest` y `CategoriaSugerenciaControllerTest`.
3. Ejecutar también `ImagenAnalisisControllerTest` para comprobar regresión de HU-30.
4. Con MySQL local configurado, ejecutar `UFoundApplication`.
5. Iniciar sesión de estudiante, abrir `/objetos-perdidos/registrar`, adjuntar una foto
   que HU-30 reconozca, pulsar **Analizar imagen**.
6. Comprobar que aparece la sugerencia y que pulsar **Usar categoría sugerida** selecciona
   una categoría válida; cambiarla manualmente y registrar. Verificar que se guarda la elegida.
7. Repetir como Seguridad en `/objetos-encontrados/registrar`.
8. Repetir sin foto, con una imagen no reconocida y con cambio de foto: el registro manual sigue disponible.

### Alcance de la HU

HU-31 no autocompleta nombre, descripción ni ubicación (eso pertenece a HU-32).
La categoría se sugiere, no se impone. No se modifica HU-30 ni se realizan llamadas adicionales al modelo ONNX.

### Cambios mínimos sobre archivos previos

Se extendieron tres archivos existentes solo para conectar el flujo:
- `static/js/analisis-imagen.js`: emite un evento al completar correctamente HU-30.
- `templates/registrar-objeto-perdido.html`: incluye UI y recursos nuevos de HU-31.
- `templates/registrar-objeto-encontrado.html`: agrega el botón de HU-30 y la sugerencia de HU-31.

El resto de archivos del ZIP original permanece idéntico, byte a byte.

### Trabajo en Git

Implementar HU-31 en una rama propia, no directamente en `main` ni sobre la rama de otro integrante.
Como se parte de HU-30, una vez que el PR de HU-30 sea fusionado a `main`, actualizar la rama
de HU-31 con `main` y crear un PR donde solo aparezcan los cambios de HU-31.
