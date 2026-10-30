# HU-30: detección visual de características

## Alcance

La historia [#30](https://github.com/MarceloL19/UFOUND/issues/30) pide devolver tipo y color probables para una imagen procesable y presentar cualquier resultado incierto como sugerencia. La validación de archivo sigue a cargo de HU-29. El análisis no escribe en la base de datos ni completa automáticamente el formulario; la persona confirma o corrige la información antes de registrar. El llenado automático y su aceptación pertenecen a HU-31/HU-32.

## Decisión técnica y trazabilidad

- Se usa el archivo `src/main/resources/models/yolox_nano.onnx` que estaba en el PR #66 retirado. Se reutiliza **solo el modelo**, no el código de integración de ese PR. SHA-256: `C789161ED43C8269FCD4E67C67EEEB4E80C622DA2EB296A20BC6007BD18A0B7D`.
- El archivo no incluye metadatos de procedencia, entrenamiento ni calibración. La prueba de funcionamiento se basa en inferencias reproducibles con fotografías ya presentes en este repositorio. No se afirma una precisión general ni que la puntuación sea una probabilidad calibrada.
- La integración Java usa ONNX Runtime 1.20.0, entrada BGR de 416 × 416 con relleno, y decodifica la salida de 3549 filas del modelo. Solo se sugieren ocho tipos pertinentes al prototipo: mochila, bolso, maleta, botella, laptop, mouse, celular y libro. El umbral de puntuación es 0,35.
- El color se estima de los píxeles dentro de la región detectada. Es una heurística: iluminación, fondo, múltiples colores u oclusión pueden alterar la sugerencia.
- Si no hay una detección suficientemente confiable, la API devuelve `SIN_DETECCION` sin tipo ni color. Si falla el motor, devuelve 503 con mensaje de continuación manual. Un archivo inválido sigue devolviendo 400 y una sesión ausente 401.

## Evidencia y límites de validación

Ejecutar desde la raíz del proyecto:

```powershell
mvn test
```

Las pruebas cubren el modelo y el endpoint con `laptop-gris.jpg` (Laptop, Gris), el modelo con `botella-verde.jpg` (Botella, Verde), una imagen blanca sin objeto, el flujo WebP, estimación de colores básicos, archivo inválido, sesión ausente y fallo del detector. Las fotografías proceden de `src/main/resources/static/fotos/`. El conjunto completo de pruebas Maven debe pasar antes de solicitar revisión del PR.

La revisión manual en un navegador conectado a la base de datos y la aceptación del PO aún están pendientes. Las fotografías del repositorio no constituyen un conjunto representativo para medir precisión; las categorías no detectadas o ambiguas deben continuar con ingreso manual. El texto de la interfaz pide confirmar o corregir cada sugerencia.

La decisión de limitar el alcance y mantener evidencia de pruebas sigue las pautas de diseño ágil, documentación de arquitectura, uso crítico de IA y aseguramiento de calidad entregadas en las semanas 1, 6, 8 y 9 del curso.
