
package pe.edu.ulima.ufound.coincidencias;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Evaluacion del conjunto etiquetado; no exige que el motor visual mejore. */
public class EvaluacionMotoresHu33Test {
    @Test
    void compararMotoresSobreLosMismosPares() throws Exception {
        Path carpeta = Path.of("docs", "hu33");
        Path entrada = carpeta.resolve("hu33-pares-ampliado.csv");
        assertTrue(Files.isRegularFile(entrada), "Falta el CSV: " + entrada.toAbsolutePath());
        List<List<String>> tabla = leerCsv(Files.readString(entrada, StandardCharsets.UTF_8));
        List<String> cabecera = tabla.remove(0);
        cabecera.set(0, cabecera.get(0).replace("\uFEFF", ""));
        assertEquals(30, tabla.size(), "Se esperan los 30 pares del conjunto acordado");
        MotorSimilitud base = new MotorSimilitudBase();
        MotorSimilitud visual = new MotorSimilitudVisual();
        // Orden de contadores: verdaderos positivos, falsos positivos,
        // falsos negativos y verdaderos negativos.
        int[][][] conteos = new int[2][2][4];
        int[] umbrales = { 40, 60 };
        int positivos = 0;
        Set<String> pares = new HashSet<>();
        StringBuilder detalle = new StringBuilder(
                "id_perdido,id_encontrado,etiqueta,puntaje_base,puntaje_visual\n");
        for (List<String> fila : tabla) {
            assertEquals(cabecera.size(), fila.size(), "Fila CSV incompleta");
            Map<String, String> r = new HashMap<>();
            for (int i = 0; i < cabecera.size(); i++)
                r.put(cabecera.get(i), fila.get(i));
            String par = r.get("id_perdido") + ":" + r.get("id_encontrado");
            assertTrue(pares.add(par), "Par duplicado: " + par);
            assertTrue(Set.of("0", "1").contains(r.get("etiqueta")), "Etiqueta invalida: " + par);
            assertFalse(r.getOrDefault("revisor", "").isBlank(), "Falta revisor: " + par);
            assertFalse(r.getOrDefault("justificacion", "").isBlank(), "Falta justificacion: " + par);
            int esperado = Integer.parseInt(r.get("etiqueta"));
            positivos += esperado;
            ObjetoPerdido p = new ObjetoPerdido();
            p.setNombre(r.get("nombre_perdido"));
            p.setDescripcion(r.get("descripcion_perdido"));
            p.setCategoria(CategoriaObjeto.valueOf(r.get("categoria_perdido")));
            p.setUbicacion(UbicacionCampus.valueOf(r.get("ubicacion_perdido")));
            p.setAtributosVisuales(atributos(r, "perdido"));
            ObjetoEncontrado e = new ObjetoEncontrado();
            e.setNombre(r.get("nombre_encontrado"));
            e.setDescripcion(r.get("descripcion_encontrado"));
            e.setCategoria(CategoriaObjeto.valueOf(r.get("categoria_encontrado")));
            e.setUbicacion(UbicacionCampus.valueOf(r.get("ubicacion_encontrado")));
            e.setAtributosVisuales(atributos(r, "encontrado"));
            int[] puntajes = { base.calcular(p, e), visual.calcular(p, e) };
            if (p.getAtributosVisuales() == null || e.getAtributosVisuales() == null) {
                assertEquals(puntajes[0], puntajes[1], "Cambio sin atributos en ambos: " + par);
            }
            detalle.append(r.get("id_perdido")).append(',').append(r.get("id_encontrado"))
                    .append(',').append(esperado).append(',').append(puntajes[0])
                    .append(',').append(puntajes[1]).append('\n');
            for (int m = 0; m < 2; m++) {
                assertTrue(puntajes[m] >= 0 && puntajes[m] <= 100);
                for (int u = 0; u < umbrales.length; u++) {
                    boolean predicho = puntajes[m] >= umbrales[u];
                    int indice = predicho ? (esperado == 1 ? 0 : 1) : (esperado == 1 ? 2 : 3);
                    conteos[m][u][indice]++;
                }
            }
        }
        assertEquals(14, positivos, "Revisar etiquetas del escenario confirmado");
        StringBuilder resumen = new StringBuilder(
                "motor,umbral,pares,VP,FP,FN,VN,precision,recall,f1\n");
        String[] nombres = { "base", "visual" };
        for (int m = 0; m < 2; m++) {
            for (int u = 0; u < umbrales.length; u++) {
                int[] c = conteos[m][u];
                double precision = dividir(c[0], c[0] + c[1]);
                double recall = dividir(c[0], c[0] + c[2]);
                double f1 = dividir(2.0 * c[0], 2 * c[0] + c[1] + c[2]);
                resumen.append(String.format(Locale.ROOT,
                        "%s,%d,%d,%d,%d,%d,%d,%.6f,%.6f,%.6f%n",
                        nombres[m], umbrales[u], tabla.size(), c[0], c[1], c[2], c[3],
                        precision, recall, f1));
            }
        }
        Files.writeString(carpeta.resolve("resultados-pares.csv"), detalle, StandardCharsets.UTF_8);
        Files.writeString(carpeta.resolve("metricas-motores.csv"), resumen, StandardCharsets.UTF_8);
        System.out.println("HU33: umbral principal 40 (coincidencias); secundario 60 (notificaciones).");
        System.out.println(resumen);
    }

    private static double dividir(double numerador, int denominador) {
        return denominador == 0 ? 0.0 : numerador / denominador;
    }

    private static AtributosVisuales atributos(Map<String, String> r, String lado) {
        String tipo = nullable(r.get("tipo_" + lado));
        String color = nullable(r.get("color_" + lado));
        String confianza = nullable(r.get("confianza_" + lado));
        if (tipo == null || confianza == null)
            return null;
        return new AtributosVisuales(tipo, color, Double.valueOf(confianza));
    }

    private static String nullable(String valor) {
        return valor == null || valor.isBlank() || "NULL".equalsIgnoreCase(valor.trim())
                ? null
                : valor.trim();
    }

    // CSV con comas, comillas escapadas y saltos de linea dentro de campos.
    private static List<List<String>> leerCsv(String texto) {
        List<List<String>> filas = new ArrayList<>();
        List<String> fila = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean comillas = false;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '"') {
                if (comillas && i + 1 < texto.length() && texto.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else
                    comillas = !comillas;
            } else if (c == ',' && !comillas) {
                fila.add(campo.toString());
                campo.setLength(0);
            } else if ((c == '\n' || c == '\r') && !comillas) {
                if (c == '\r' && i + 1 < texto.length() && texto.charAt(i + 1) == '\n')
                    i++;
                fila.add(campo.toString());
                campo.setLength(0);
                if (fila.size() > 1 || !fila.get(0).isBlank())
                    filas.add(fila);
                fila = new ArrayList<>();
            } else
                campo.append(c);
        }
        if (comillas)
            throw new IllegalArgumentException("CSV con comillas sin cerrar");
        if (campo.length() > 0 || !fila.isEmpty()) {
            fila.add(campo.toString());
            filas.add(fila);
        }
        return filas;
    }
}
