# HU-32 - Autocompletar formulario de registro de objeto

## Qué se añadió

Cuando HU-30 detecta un objeto en la foto, HU-32 completa en el formulario **solo los campos que el
análisis respalda** y los marca con la etiqueta **Sugerido por IA**. El usuario los revisa, los corrige si
hace falta y, al registrar, se guardan los valores que quedan en el formulario.

| Campo | Se completa con | Condición |
| --- | --- | --- |
| Título | Tipo + color con concordancia de género (`Laptop negra`, `Mouse blanco`) | Tipo reconocido; sin color solo el tipo |
| Categoría | La categoría del catálogo que propone HU-31 | Tipo reconocido |
| Descripción | `<Tipo> de color <color>.` | Solo si HU-30 estimó un color válido |
| Ubicación | **Nunca** | La ubicación no se infiere de la fotografía |

### Reglas

- Un campo que el usuario ya completó antes de analizar **no se sobrescribe**; el panel lo indica.
- Si el usuario edita un campo sugerido, pierde la marca y pasa a ser un valor confirmado.
- **Deshacer autocompletado** restaura los valores previos de los campos sugeridos que no se editaron.
- Si se cambia la foto, se retiran las sugerencias que no se editaron y se ignoran respuestas tardías.
- Tipos o colores que no provienen del detector se descartan en el servidor (no se copia texto arbitrario).
- No hay campos ocultos: el registro usa los mismos `input`/`select`/`textarea` del formulario original.

### Flujo

1. Estudiante o Seguridad elige una imagen y pulsa **Analizar imagen**.
2. HU-29 valida y HU-30 detecta tipo y color (`POST /imagenes/analizar`), y emite `ufound:imagen-analizada`.
3. HU-32 envía tipo y color a `POST /hu32/autocompletar`, que devuelve los campos respaldados.
4. El formulario se completa y el panel naranja resume qué se completó y qué se conservó.
5. El usuario confirma o corrige y pulsa **Registrar**; los servicios existentes guardan lo que ve.

### Archivos

- `hu32/AutocompletadoFormularioService.java`: reglas de qué campos completar (reutiliza HU-31 para la categoría).
- `hu32/AutocompletadoFormularioController.java`: endpoint `POST /hu32/autocompletar` (requiere sesión).
- `static/js/hu32-autocompletado.js` y `static/css/hu32-autocompletado.css`.
- `templates/registrar-objeto-perdido.html` y `registrar-objeto-encontrado.html`: panel y recursos de HU-32.

### Pruebas

- `AutocompletadoFormularioServiceTest`, `AutocompletadoFormularioControllerTest`, `ValoresConfirmadosHu32Test`.
- `node --test docs/hu32-js-smoke.test.cjs` para la lógica del navegador.

HU-32 depende de HU-30 y HU-31; debe integrarse después de ellas.
