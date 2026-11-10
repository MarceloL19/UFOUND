package pe.edu.ulima.ufound.service;

import org.junit.jupiter.api.Test;
import pe.edu.ulima.ufound.model.CategoriaObjeto;
import pe.edu.ulima.ufound.model.ObjetoEncontrado;
import pe.edu.ulima.ufound.model.ObjetoPerdido;
import pe.edu.ulima.ufound.model.UbicacionCampus;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EvaluacionHu33Test {

    private final CalculadoraCoincidencia calculadora = new CalculadoraCoincidencia();

    @Test
    void comparaLosDosMotoresConLosMismosParesProvisionales() throws Exception {
        List<String> lineas = Files.readAllLines(Path.of(System.getProperty("hu33.dataset",
                "docs/HU-33-pares-provisionales.csv")), StandardCharsets.UTF_8);
        Set<String> ids = new HashSet<>();
        Set<String> fotos = new HashSet<>();
        int positivos = 0;
        int negativos = 0;
        int sinFoto = 0;
        int[][][] matrices = new int[2][2][4];
        for (String linea : lineas.subList(1, lineas.size())) {
            if (linea.isBlank()) continue;
            String[] c = linea.split(";", -1);
            assertTrue(c.length >= 18, linea);
            assertTrue(ids.add(c[0]), "ID repetido: " + c[0]);
            for (int indice : new int[]{6, 14}) {
                if (!c[indice].isBlank()) assertTrue(fotos.add(c[indice]), "Foto repetida: " + c[indice]);
                else sinFoto++;
            }
            boolean positivo = switch (c[1]) {
                case "1" -> true;
                case "0" -> false;
                default -> throw new IllegalArgumentException("Etiqueta invalida: " + c[0]);
            };
            if (positivo) positivos++; else negativos++;
            ObjetoPerdido perdido = perdido(c);
            ObjetoEncontrado encontrado = encontrado(c);
            int base = calculadora.calcularBase(perdido, encontrado);
            int visual = calculadora.calcular(perdido, encontrado);
            System.out.printf(Locale.ROOT, "%s etiqueta=%d base=%d visual=%d%n",
                    c[0], positivo ? 1 : 0, base, visual);
            for (int t = 0; t < 2; t++) {
                int umbral = t == 0 ? 40 : 60;
                acumular(matrices[t][0], positivo, base >= umbral);
                acumular(matrices[t][1], positivo, visual >= umbral);
            }
        }
        assertTrue(ids.size() >= 12);
        assertTrue(positivos >= 4 && negativos >= 4 && sinFoto >= 2);
        System.out.printf("Conjunto provisional: pares=%d positivos=%d negativos=%d fotos_unicas=%d ausencias_de_foto=%d%n",
                ids.size(), positivos, negativos, fotos.size(), sinFoto);
        for (int t = 0; t < 2; t++) {
            for (int motor = 0; motor < 2; motor++) {
                imprimir(t == 0 ? 40 : 60, motor == 0 ? "base" : "visual", matrices[t][motor]);
            }
        }
    }

    private ObjetoPerdido perdido(String[] c) {
        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setCategoria(CategoriaObjeto.valueOf(c[2]));
        objeto.setUbicacion(UbicacionCampus.valueOf(c[3]));
        objeto.setNombre(c[4]);
        objeto.setDescripcion(c[5]);
        objeto.setImagenUrl(c[6].isBlank() ? null : "/provisional/" + c[6]);
        objeto.setTipoVisual(c[7].isBlank() ? null : c[7]);
        objeto.setColorVisual(c[8].isBlank() ? null : c[8]);
        objeto.setConfianzaVisual(c[9].isBlank() ? null : Double.valueOf(c[9]));
        return objeto;
    }

    private ObjetoEncontrado encontrado(String[] c) {
        ObjetoEncontrado objeto = new ObjetoEncontrado();
        objeto.setCategoria(CategoriaObjeto.valueOf(c[10]));
        objeto.setUbicacion(UbicacionCampus.valueOf(c[11]));
        objeto.setNombre(c[12]);
        objeto.setDescripcion(c[13]);
        objeto.setImagenUrl(c[14].isBlank() ? null : "/provisional/" + c[14]);
        objeto.setTipoVisual(c[15].isBlank() ? null : c[15]);
        objeto.setColorVisual(c[16].isBlank() ? null : c[16]);
        objeto.setConfianzaVisual(c[17].isBlank() ? null : Double.valueOf(c[17]));
        return objeto;
    }

    private void acumular(int[] matriz, boolean real, boolean predicho) {
        if (real && predicho) matriz[0]++;
        else if (!real && predicho) matriz[1]++;
        else if (!real) matriz[2]++;
        else matriz[3]++;
    }

    private void imprimir(int umbral, String motor, int[] matriz) {
        double precision = proporcion(matriz[0], matriz[0] + matriz[1]);
        double recall = proporcion(matriz[0], matriz[0] + matriz[3]);
        double f1 = precision + recall == 0 ? 0 : 2 * precision * recall / (precision + recall);
        System.out.printf(Locale.ROOT,
                "umbral=%d motor=%s VP=%d FP=%d VN=%d FN=%d precision=%.3f recall=%.3f F1=%.3f%n",
                umbral, motor, matriz[0], matriz[1], matriz[2], matriz[3], precision, recall, f1);
    }

    private double proporcion(int numerador, int denominador) {
        return denominador == 0 ? 0 : (double) numerador / denominador;
    }
}
