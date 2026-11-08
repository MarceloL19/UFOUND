# HU-34: priorizar coincidencias por similitud

## Comportamiento

Las coincidencias del usuario se ordenan por:

1. Puntaje de similitud descendente.
2. Fecha de detección descendente.
3. ID de coincidencia ascendente.

El ID resuelve los empates de puntaje y fecha de forma reproducible.
Se utiliza el puntaje almacenado por el motor integrado en HU-33.

## Implementación

El ordenamiento se realiza en la consulta de CoincidenciaRepository.
CoincidenciaService utiliza esa consulta para listar los resultados.

## Validación

Cinco pruebas de integración con H2 verifican:

- Orden por puntaje, independientemente de la fecha.
- Desempate por fecha más reciente.
- Desempate por ID y repetibilidad.
- Separación de resultados por usuario.
- Lista vacía para usuarios sin coincidencias.

Ejecutar desde la raíz del proyecto:

mvn -B "-Dtest=OrdenCoincidenciasHu34Test" test

Estas pruebas verifican la consulta sobre H2; no constituyen
una validación específica del motor MySQL.

## Dependencia

Esta rama parte de feature/HU-33-coincidencias-visuales.
Su integración final debe coordinarse con el PR #71.
