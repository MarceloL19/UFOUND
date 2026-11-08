# HU-33: coincidencias con atributos visuales

## Regla implementada

Cuando ambos objetos tienen tipo visual y confianza válida entre
0.35 y 1.0, se incorporan sus atributos al puntaje:

- Con color en ambos: 70 % del puntaje base, 20 % coincidencia de
  tipo y 10 % coincidencia de color.
- Sin color en alguno: 80 % del puntaje base y 20 % coincidencia
  de tipo.
- Sin atributos válidos en alguno: se conserva el puntaje base.

La confianza del detector no representa la probabilidad de que
dos reportes correspondan al mismo objeto.

## Evaluación

Se compararon los motores sobre los mismos 30 pares:
14 positivos y 16 negativos. Daniel Sandoval confirmó que los
reportes de maleta representan el mismo objeto del escenario.

Umbral principal: 40, para detectar coincidencias.
Umbral secundario: 60, para activar notificaciones.

| Umbral | Motor  | VP  | FP  | FN  | VN  | Precisión | Recall  | F1      |
| ------ | ------ | --- | --- | --- | --- | --------- | ------- | ------- |
| 40     | Base   | 8   | 0   | 6   | 16  | 100 %     | 57.14 % | 72.73 % |
| 40     | Visual | 8   | 0   | 6   | 16  | 100 %     | 57.14 % | 72.73 % |
| 60     | Base   | 6   | 0   | 8   | 16  | 100 %     | 42.86 % | 60 %    |
| 60     | Visual | 6   | 0   | 8   | 16  | 100 %     | 42.86 % | 60 %    |

## Conclusión y límites

Los atributos visuales modifican los puntajes, pero no mejoraron
las métricas en este conjunto ni en los umbrales evaluados.
El comportamiento base se conserva cuando faltan atributos.

El conjunto es pequeño, controlado y reutiliza fotografías.
Sus resultados no permiten afirmar una mejora general de precisión.
Los pesos y umbrales no se ajustaron para favorecer estos resultados.

## Reproducción

Desde la raíz del proyecto:

mvn -B "-Dtest=EvaluacionMotoresHu33Test" test

Entrada: docs/hu33/hu33-pares-ampliado.csv
Salidas: docs/hu33/resultados-pares.csv
docs/hu33/metricas-motores.csv
