# Datos de demostracion para Oficina

Preparados para la tarea #81. Son **48 objetos perdidos y 36 encontrados ficticios**, con fotografías existentes del repositorio y algunos sin foto. Cubren siete categorías, ocho ubicaciones y unos seis meses relativos a la fecha de carga, con varios estados. Los primeros 30 días concentran más registros para que se aprecie la comparación con el periodo anterior.

Se identifican mediante `[DEMO]` en el nombre y `UFOUND_DEMO_VISUAL_V1:` en la descripción. Se crean dos cuentas técnicas de demostración desactivadas, sin una contraseña compartida ni acceso de inicio de sesión. Los objetos aparecen en las vistas generales y de Oficina; no se asignan a tus reportes personales.

## Cargar localmente

1. Usa una base local para la exposición o una copia de tus datos. Inicia primero UFOUND para que Hibernate haya creado sus tablas.
2. En MySQL Workbench, abre `cargar-demo.sql`, selecciona la base de UFOUND como esquema predeterminado y ejecuta el archivo completo. Alternativamente, entra en el cliente MySQL con tu cuenta local:

   ```sh
   mysql -u TU_USUARIO -p NOMBRE_DE_TU_BASE
   ```

   Dentro del cliente, ejecuta la ruta real de tu copia del repositorio. En Windows usa barras `/`, por ejemplo:

   ```sql
   SOURCE C:/Users/User/Downloads/UFOUND-local/docs/demo/cargar-demo.sql;
   ```

3. Comprueba los totales que imprime el script: **48 pérdidas y 36 hallazgos de demostración**. Repetir la carga no duplica esos registros ni cambia sus fechas iniciales.
4. Entra como Oficina y revisa Inicio, Estadísticas de objetos perdidos y Análisis de pérdidas. Cambia el periodo a los últimos 30 o 90 días y compáralo con el anterior.

No se modifica `data.sql`: los datos no se cargan al iniciar la aplicación ni se publican automáticamente en Railway. El script usa el esquema seleccionado, sin credenciales ni nombre de base fijo en el archivo.

## Que demuestran los datos

Los indicadores y gráficos se calculan sobre registros de las tablas existentes. Las estadísticas de pérdidas solo incluyen `objetos_perdidos`; los encontrados aparecen en los indicadores administrativos que ya los contemplan. Cada registro demo tiene un evento de estado de demostración, identificado por su cuenta responsable.

La importación prepara datos para visualizar estadísticas: no ejecuta el motor de coincidencias ni inventa sus puntajes. Las coincidencias y notificaciones que se generen al registrar objetos mediante la aplicación seguirán siendo calculadas por el flujo existente. Las fechas, estados e imágenes del conjunto son material de exposición, no observaciones reales del campus.

## Retirar el conjunto

Abre y ejecuta `retirar-demo.sql` en el mismo esquema, o usa:

```sql
SOURCE C:/Users/User/Downloads/UFOUND-local/docs/demo/retirar-demo.sql;
```

La retirada elimina los objetos demo que conservan su cuenta de demostración, que no están vinculados a coincidencias y que no tienen eventos gestionados por otra cuenta. También retira sus eventos de demostración y las cuentas desactivadas cuando ya no tienen referencias.

Si un objeto demo se vinculó o fue gestionado, se conserva junto con su historial. El resultado indica cuántos quedaron: revisa esos casos antes de decidir qué hacer con ellos. La retirada no borra coincidencias ni notificaciones para forzar la eliminación, ni toca objetos ajenos al conjunto.

## Verificacion realizada

Se probó sobre una **base aislada MariaDB 11.4.5**, usando el esquema generado por UFOUND: carga inicial y repetida con 48/36 registros, retirada con conservación de dos registros de control ajenos al conjunto, y conservación de un reporte demo gestionado por otra cuenta. Los scripts usan sintaxis de MySQL; queda pendiente repetir su ejecución en la versión MySQL concreta del entorno local de exposición.
