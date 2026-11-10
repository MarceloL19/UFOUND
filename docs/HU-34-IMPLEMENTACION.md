# HU-34 - Priorizar coincidencias por nivel de similitud

## Qué se añadió

La lista **Coincidencias** del estudiante se ordena con una regla única y reproducible, definida en
`OrdenCoincidencias.POR_RELEVANCIA`:

| Orden | Criterio | Sentido |
| --- | --- | --- |
| 1 | `porcentajeSimilitud`, el puntaje del motor mejorado de HU-33 | Mayor a menor |
| 2 | `fechaDeteccion` | Más reciente primero |
| 3 | `idCoincidencia`, que es único | Mayor a menor |

- El tercer criterio garantiza que dos consultas con los mismos datos devuelvan siempre el mismo orden.
- Es necesario porque `fecha_deteccion` se guarda con precisión de segundos. Al registrar un objeto se
  generan varias coincidencias en el mismo segundo, así que suelen empatar en fecha.
- Cada tarjeta muestra su posición (`#1`, `#2`, ...) y la página explica el criterio de orden.

## Archivos

- `service/OrdenCoincidencias.java`: comparador con la regla de orden y desempate. Los valores nulos van al final.
- `service/CoincidenciaService.listarPorUsuario`: aplica el comparador. El repositorio solo filtra por usuario
  (`findByObjetoPerdidoUsuario`), de modo que la regla vive en un solo lugar.
- `templates/coincidencias.html` y `static/css/styles.css`: posición de cada coincidencia y nota del criterio.

## Pruebas

`OrdenCoincidenciasHu34Test` (6 pruebas):

- Orden de mayor a menor puntaje.
- Desempate por fecha y por id.
- Mismo resultado tras 25 mezclas aleatorias de la entrada.
- Con el motor real de HU-33, entre dos encontrados con igual puntaje base, va primero el que coincide en
  tipo y color visual.
- Lista vacía.

## Validación manual

1. Se registraron cinco objetos encontrados: dos `Laptop gris` idénticos, `Laptop negra Lenovo`,
   `Mochila gris` y `Casaca azul`.
2. Luego el estudiante registró un perdido `Laptop gris` (Tecnología, A2).
3. Resultado en `/coincidencias`:

| Posición | Coincidencia | Puntaje |
| --- | --- | --- |
| #1 | id 4 | 100% |
| #2 | id 3 | 100% |
| #3 | id 2 | 64% |
| #4 | id 1 | 46% |

- La casaca no supera el umbral de 40.
- Las cuatro tenían la misma `fecha_deteccion`, así que el empate a 100% lo resolvió el id.

## Dependencias y límites

- HU-34 depende de HU-33 (PR #73), que aporta el puntaje con atributos visuales.
- Las coincidencias históricas no se recalculan: se ordenan con el puntaje que tenían guardado.
