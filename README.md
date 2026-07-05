# PROYECTO-UFOUND

Sistema web para la gestion de objetos perdidos en la Universidad de Lima.

## EPIC 1

Modulo base de autenticacion y control de acceso por roles.

## EPIC 2

Registro de objetos perdidos exclusivo para usuarios con rol `ESTUDIANTE`.

Incluye:

- formulario web con Thymeleaf;
- entidad `ObjetoPerdido`;
- categorias y ubicaciones como enums;
- guardado en MySQL;
- asociacion con el usuario autenticado;
- carga opcional de imagen en `uploads/objetos-perdidos`;
- listado de reportes registrados por el estudiante.

## EPIC 3

Registro y visualizacion de objetos encontrados exclusivo para usuarios con rol `SEGURIDAD`.

Incluye:

- formulario web con Thymeleaf;
- entidad `ObjetoEncontrado`;
- guardado en MySQL;
- asociacion con el usuario de seguridad autenticado;
- carga opcional de imagen en `uploads/objetos-encontrados`;
- listado de objetos encontrados;
- vista de detalle del objeto encontrado.

## EPIC 4

Gestion del ciclo de vida de los objetos mediante estados e historial.

Incluye:

- enum unificado `EstadoObjeto`;
- entidad `HistorialEstado`;
- registro de estado inicial al crear objetos perdidos o encontrados;
- actualizacion de estado exclusiva para usuarios con rol `OFICINA`;
- vista de gestion de estados para oficina;
- vista de seguimiento de estado para estudiantes y seguridad;
- trazabilidad de cambios con estado anterior, estado nuevo, fecha y usuario responsable.

## EPIC 5

Deteccion de coincidencias y notificaciones automaticas para estudiantes.

Incluye:

- entidad `Coincidencia`;
- entidad `Notificacion`;
- enum `NivelCoincidencia`;
- comparacion automatica entre objetos perdidos y encontrados;
- calculo de similitud por categoria, ubicacion, nombre y descripcion;
- generacion de coincidencias desde 40% de similitud;
- generacion de notificaciones para coincidencias de nivel medio o alto;
- vista de coincidencias para estudiantes;
- vista de notificaciones leidas y no leidas;
- opcion para marcar notificaciones como leidas.

Tecnologias:

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- MySQL

Base de datos:

- `proyecto_ufound_db`
- La URL JDBC incluye `createDatabaseIfNotExist=true`, por lo que MySQL puede crearla automaticamente si el usuario tiene permisos.

Usuarios de prueba:

- `20235694@aloe.ulima.edu.pe` / `123456` / ESTUDIANTE
- `seguridad@ulima.edu.pe` / `123456` / SEGURIDAD
- `oficina@ulima.edu.pe` / `123456` / OFICINA

Ejecucion:

```powershell
C:\apache-maven-3.9.16\bin\mvn.cmd spring-boot:run
```

URL:

```text
http://localhost:8080/login
```
