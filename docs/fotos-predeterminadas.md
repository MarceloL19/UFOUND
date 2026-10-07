# Fotos predeterminadas de UFOUND

Se incorporaron las 52 fotos de objetos de `fotos.zip` en `src/main/resources/static/fotos/`, conservando exactamente sus nombres. Spring Boot las sirve como `/fotos/<nombre>` desde el proyecto o desde el JAR compilado. En una URL, los espacios se escriben `%20` (por ejemplo, `/fotos/llavero%20negro.png`).

Archivos incorporados:

- `agenda-marron.jpg`
- `audifonos-blancos.jpg`
- `audifonos-bluetooth.jpg`
- `audifonos-negros.jpg`
- `billetera-azul.jpg`
- `billetera-marron.jpg`
- `botella-morada.jpg`
- `botella-verde.jpg`
- `cable-usb.jpg`
- `calculadora-negra.jpg`
- `cargador-blanco.jpg`
- `cargador-negro.jpg`
- `cartuchera-roja.jpg`
- `cartuchera-rosada.jpg`
- `cartuchera-verde.jpg`
- `casaca-azul.jpg`
- `casaca-gris.jpg`
- `casaca-roja.jpg`
- `celular-negro.jpg`
- `chompa-azul.jpg`
- `chompa-verde.jpg`
- `cuaderno-blanco.jpg`
- `cuaderno-rojo.jpg`
- `estuche-lentes.jpg`
- `folder-azul.jpg`
- `folder-verde.jpg`
- `gorro-rojo.jpg`
- `lapicero-azul.jpg`
- `laptop-gris.jpg`
- `laptop-negra-2.jpg`
- `laptop-negra.jpg`
- `llave-auto.jpg`
- `llavero negro.png`
- `llaves-plateadas.jpg`
- `maletin-negro.jpg`
- `mochila-gris.jpg`
- `mochila-marron.jpg`
- `mochila-negra.jpg`
- `morral-negro.jpg`
- `mouse-blanco.jpg`
- `mouse-negro.jpg`
- `paraguas-negro.jpg`
- `polo-azul.jpg`
- `polo-blanco.jpg`
- `reloj-dorado.jpg`
- `tablet-negra.jpg`
- `tomatodo rojo.png`
- `tomatodo-azul.jpg`
- `tomatodo-celeste.jpg`
- `tomatodo-gris.jpg`
- `usb-azul.jpg`
- `usb-plateado.jpg`

`fotos/LOGO UFOUND.png` no se copió porque es idéntico al `src/main/resources/static/img/logo-ufound.png` existente.

Los registros de objetos obtienen su imagen de `imagen_url` en MySQL. La ruta directa de una foto predeterminada es `/fotos/<nombre exacto>`. No se modificaron registros: este proyecto no incluye las filas de objetos ni se pudo acceder a la base local en esta revisión. Si una fila apunta a `/uploads/objetos-perdidos/<uuid>` o `/uploads/objetos-encontrados/<uuid>`, necesita el archivo original de la carga del usuario; estas fotos no lo reemplazan. `uploads/` sigue excluido de Git.

Como compatibilidad con las rutas antiguas, `/uploads/objetos-perdidos/<nombre exacto>` y `/uploads/objetos-encontrados/<nombre exacto>` también buscan una foto predeterminada con ese nombre si no existe un archivo físico de usuario. Si el nombre guardado es un UUID o no coincide exactamente con la lista, no se muestra una foto distinta por conjetura.

La revisión del estudiante encontró, por ejemplo, `/uploads/objetos-encontrados/audifonos-blancos.jpg`, `/uploads/objetos-encontrados/audifonos-bluetooth.jpg` y `/uploads/objetos-encontrados/audifonos-negros.jpg` en `imagen_url`. Esas tres rutas se comprobaron con solicitudes HTTP en las pruebas de Spring y devolvieron los bytes de las fotos predeterminadas correspondientes. El estudiante confirmó después que las fotos se mostraban en la aplicación; Codex no verificó su MySQL local.
