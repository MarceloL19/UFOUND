# HU-33: coincidencias con atributos visuales

## Alcance y decisiones

La deteccion se vuelve a ejecutar **en el servidor** al registrar cada foto. La respuesta de HU-30 que ve el navegador no se usa como fuente para la coincidencia y nunca reemplaza el nombre, categoria, descripcion o ubicacion confirmados por la persona. Si el detector falla, la foto falta o la confianza es menor que 0.35, el objeto se registra sin atributos visuales y el calculo usa exactamente la formula anterior.

En `objetos_perdidos` y `objetos_encontrados` se agregan tres columnas nullable: `tipo_visual` (varchar 30), `color_visual` (varchar 30) y `confianza_visual` (double). El proyecto usa `spring.jpa.hibernate.ddl-auto=update`, por lo que Hibernate agrega estas columnas en MySQL local al arrancar. No hay herramienta de migraciones versionadas en el repositorio. Antes de desplegar en un entorno con `ddl-auto` desactivado, el equipo debe aplicar el cambio equivalente en su proceso de migracion y verificarlo. Las filas anteriores conservan `NULL` y usan la formula base. **No se recalculan coincidencias ni notificaciones ya guardadas**: el motor nuevo actua al registrar objetos nuevos. Un recálculo historico requeriria una tarea separada, con control de duplicados, cambios de estado y avisos.

## Regla fija antes de evaluar

La puntuacion base permanece igual: `30 * categoria_igual + 20 * ubicacion_igual + hasta 25 por interseccion de palabras del nombre + hasta 25 por interseccion de palabras de la descripcion`. La interseccion de texto y su redondeo conservan el codigo anterior.

Solo si **ambos** objetos tienen foto guardada, tipo entre las ocho clases de HU-30 y confianza finita en `[0.35, 1]`, se aplica:

```text
ajuste_tipo  = +15 si coincide el tipo; -20 si difiere
ajuste_color = +5 si ambos colores existen y coinciden;
               -5 si ambos existen y difieren; 0 si falta alguno
puntaje_final = max(0, min(100, puntaje_base + ajuste_tipo + ajuste_color))
```

El tipo tiene mas peso que el color porque este puede variar con luz y encuadre. Una discrepancia de tipo penaliza mas que una concordancia lo beneficia, para evitar unir objetos distintos de una misma categoria. El ajuste es acotado porque nombre y categoria pueden contener sugerencias de HU-32 y por tanto no son evidencia visual independiente. Esta es una regla explicita y comprobable, **no una calibracion estadistica**. Se mantienen 40 para crear coincidencia, 60 para notificar y cambiar el estado del perdido, y 60/80 para nivel medio/alto. La restriccion unica por pareja, permisos y flujos de notificacion existentes no cambian. Se separo el calculo en `CalculadoraCoincidencia`; una interfaz Strategy no aportaria claridad con estas dos formulas fijas.

## Comparacion reproducible con pares etiquetados por Daniel

`docs/HU-33-pares-equipo.csv` incorpora los **30 pares etiquetados por Daniel Sandoval** en `docs/hu33/hu33-pares-ampliado.csv` de la rama anterior `feature/HU-33-coincidencias-visuales`. Conserva las etiquetas, nombres, categorias, ubicaciones, descripciones, atributos visuales y justificaciones; adapta las columnas al formato de esta prueba. Son 14 positivos y 16 negativos, formados a partir de seis objetos perdidos y cinco encontrados del escenario controlado de clase. Un mismo objeto aparece en varios pares, por lo que se repiten sus atributos y la referencia logica de su foto. Las seis referencias `escenario-*.jpg` se asignaron durante la conversion para representar que habia atributos visuales persistidos; **no son rutas a fotos verificadas ni se ejecuta de nuevo el detector**. Una fila sin atributos visuales conserva vacia la foto, el tipo, el color y la confianza. Esta muestra no constituye una validacion independiente ni demuestra mejora general.

`EvaluacionHu33Test` verifica que no haya ID de par duplicado, permite referencias de foto repetidas, y calcula ambos motores sobre exactamente los mismos pares con umbrales 40 y 60. Reproducir desde la raiz del proyecto:

```powershell
mvn -B '-Dtest=EvaluacionHu33Test' '-Dhu33.dataset=docs/HU-33-pares-equipo.csv' test
```

La comparacion con la regla de esta rama (fijada antes de incorporar el conjunto de Daniel) da:

| Umbral | Motor | VP | FP | VN | FN | Precision | Recall | F1 |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 40 | Base | 8 | 0 | 16 | 6 | 1.000 | 0.571 | 0.727 |
| 40 | Visual | 8 | 0 | 16 | 6 | 1.000 | 0.571 | 0.727 |
| 60 | Base | 6 | 0 | 16 | 8 | 1.000 | 0.429 | 0.600 |
| 60 | Visual | 6 | 0 | 16 | 8 | 1.000 | 0.429 | 0.600 |

Los puntajes visuales cambian en varios pares, pero ninguna prediccion cruza estos umbrales. Por tanto el F1 es igual al de la formula base en esta muestra. La aceptacion del alcance y de la evidencia sigue pendiente de la decision del Product Owner; hacen falta pares independientes y diversos para estimar rendimiento general.

## Evaluacion provisional, sin validacion del equipo

`HU-33-pares-provisionales.csv` contiene **12 escenarios hipoteticos**, seis etiquetados positivos y seis negativos por esta implementacion. Sus 18 identificadores de foto son unicos, pero **no apuntan a fotos reales**: los atributos visuales y las etiquetas son simulados. Hay seis lados sin foto. Esto prueba el procedimiento de comparacion, no la precision del detector ni la identidad real de objetos. La regla se fijo antes de ejecutar esta evaluacion y no se ajusto tras ver los resultados. `EvaluacionHu33Test` usa los mismos pares y umbrales para ambos motores; imprime cada puntaje y la matriz de confusion. La salida completa esta en `evidencias/hu33/evaluacion-provisional-mvn-test.txt`.

| Umbral | Motor | VP | FP | VN | FN | Precision | Recall | F1 |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 40 | Base | 6 | 4 | 2 | 0 | 0.600 | 1.000 | 0.750 |
| 40 | Visual | 6 | 3 | 3 | 0 | 0.667 | 1.000 | 0.800 |
| 60 | Base | 6 | 3 | 3 | 0 | 0.667 | 1.000 | 0.800 |
| 60 | Visual | 6 | 2 | 4 | 0 | 0.750 | 1.000 | 0.857 |

**No hay una mejora demostrada**: el conjunto es pequeno, hipotetico y no fue etiquetado por el equipo. Incluso en el ejemplo N02, dos objetos distintos de la misma categoria superan el umbral tras el ajuste visual. La decision final requiere pares reales, etiquetas revisadas por el equipo, reglas fijadas antes de usar esos pares y aceptacion del Product Owner.

`HU-33-plantilla-etiquetado.csv` proporciona las primeras 18 columnas que consume la prueba y columnas adicionales para revisor, fecha y motivo. Cada par debe tener ID unico; un objeto y su foto pueden repetirse en varios pares. Si no hay foto, deje vacios foto, tipo, color y confianza. Registrar positivos y negativos variados, acordar la etiqueta entre revisores y separar pares para ajuste de regla de pares para evaluacion final. Para otro conjunto validado, guarde un CSV distinto y ejecute:

```powershell
mvn -B '-Dtest=EvaluacionHu33Test' '-Dhu33.dataset=docs/otro-conjunto-validado.csv' test
```

## Pruebas y reproduccion

Antes de editar, `main` en `781a3ce` paso `mvn -B clean test`: **77 pruebas, 0 fallos, 0 errores, 0 omitidas**. Salida completa en `evidencias/hu33/main-baseline-mvn-clean-test.txt`.

En esta rama, la corrida final de `mvn -B clean test` con el esquema MySQL de pruebas y la integracion habilitada paso con **91 pruebas, 0 fallos, 0 errores y 0 omitidas**. Sin `UFOUND_HU33_MYSQL_TEST=true`, las dos pruebas MySQL se omiten de forma deliberada. Las tres suites JavaScript existentes pasaron con **23 pruebas, 0 fallos**. Salidas en `evidencias/hu33/hu33-mvn-clean-test.txt` y `evidencias/hu33/hu33-javascript-test.txt`.

```powershell
mvn -B clean test
node --test docs/hu31-js-smoke.test.cjs docs/hu31-integracion.test.cjs docs/hu32-js-smoke.test.cjs
```

Las pruebas unitarias cubren concordancia y discrepancia visual, falta de foto, confianza invalida, topes 0/100, umbrales 39/40/59/60, ausencia de avisos indebidos y no duplicacion de avisos. `RegistroCoincidenciaMySqlTest` prueba la colaboracion real de servicios, repositorios JPA y MySQL en ambos ordenes de registro. Se habilita de forma explicita para no tocar la base habitual. Usa **solo** `proyecto_ufound_hu33_test`, con `ddl-auto=update`; sus tablas permanecen para futuras corridas y las filas creadas por las pruebas se revierten al terminar cada caso.

```powershell
$claveHu33 = Read-Host 'Clave de MySQL para pruebas' -AsSecureString
$env:SPRING_DATASOURCE_PASSWORD = [System.Net.NetworkCredential]::new('', $claveHu33).Password
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3306/proyecto_ufound_hu33_test?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true'
$env:UFOUND_HU33_MYSQL_TEST = 'true'
mvn -B clean test
```

Tras corregir la clave local de MySQL, la integracion paso: **2 pruebas, 0 fallos, 0 errores, 0 omitidas**. Salida en `evidencias/hu33/hu33-mysql-integracion.txt`. La primera corrida habia fallado con `SQL 1045` por acceso rechazado; la salida conservada corresponde a la corrida aprobada.

## Comprobacion manual

1. Arranque esta rama con MySQL y `mvn spring-boot:run`. Entre como Estudiante, registre una laptop perdida con `src/main/resources/static/fotos/laptop-gris.jpg` y complete los datos del formulario.
2. Entre como Seguridad y registre el hallazgo correspondiente con foto. En MySQL compruebe que ambos objetos tienen `tipo_visual`, `color_visual` y `confianza_visual`, y que la coincidencia conserva nivel, estado y notificacion esperados. Modifique manualmente el titulo o categoria antes de guardar para confirmar que el detector no los reemplaza.
3. Registre otro par sin fotos. Confirme que las tres columnas visuales quedan `NULL` y que la puntuacion coincide con la formula base.

Consultas de solo lectura:

```sql
SELECT id_objeto, nombre, categoria, tipo_visual, color_visual, confianza_visual
FROM objetos_perdidos ORDER BY id_objeto DESC LIMIT 5;
SELECT id_objeto, nombre, categoria, tipo_visual, color_visual, confianza_visual
FROM objetos_encontrados ORDER BY id_objeto DESC LIMIT 5;
SELECT id_objeto_perdido, id_objeto_encontrado, porcentaje_similitud, nivel_coincidencia
FROM coincidencias ORDER BY id_coincidencia DESC LIMIT 5;
```

## Dependencia de HU-34

El PR #72 apunta a la rama antigua de HU-33. Tras integrar esta nueva HU-33 en `main`, el autor de HU-34 debe crear o rebasar su rama sobre ese `main`, trasladar solo los cambios propios de HU-34 y actualizar la base del PR. Esta implementacion no modifica el PR #72.
