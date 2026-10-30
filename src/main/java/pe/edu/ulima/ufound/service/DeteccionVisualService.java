package pe.edu.ulima.ufound.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PreDestroy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class DeteccionVisualService {

    private static final int LADO = 416;
    private static final int CLASES_MODELO = 80;
    private static final int FILAS_MODELO = 3549;
    private static final float UMBRAL_SUGERENCIA = 0.35f;
    private static final Map<Integer, String> TIPOS_SOPORTADOS = Map.of(
            24, "Mochila",
            26, "Bolso",
            28, "Maleta",
            39, "Botella",
            63, "Laptop",
            64, "Mouse",
            67, "Celular",
            73, "Libro");

    private final OrtEnvironment entorno = OrtEnvironment.getEnvironment();
    private volatile OrtSession sesion;

    public record Resultado(String estado, String tipoProbable, String colorProbable,
                            double confianza, boolean requiereConfirmacion) {
        static Resultado sinDeteccion() {
            return new Resultado("SIN_DETECCION", null, null, 0, true);
        }
    }

    public Resultado analizar(byte[] datos) throws Exception {
        if (datos == null || datos.length == 0) {
            throw new IllegalArgumentException("La imagen está vacía.");
        }
        BufferedImage imagen = ImageIO.read(new ByteArrayInputStream(datos));
        if (imagen == null) {
            throw new IOException("No se pudo decodificar la imagen validada.");
        }

        float escala = Math.min((float) LADO / imagen.getWidth(),
                (float) LADO / imagen.getHeight());
        float[][][][] entrada = prepararEntrada(imagen, escala);

        Candidato mejor;
        try (OnnxTensor tensor = OnnxTensor.createTensor(entorno, entrada);
             OrtSession.Result inferencia = obtenerSesion().run(Map.of("images", tensor))) {
            Object valor = inferencia.get(0).getValue();
            if (!(valor instanceof float[][][] salida)
                    || salida.length != 1 || salida[0].length != FILAS_MODELO) {
                throw new IllegalStateException("La salida del modelo no tiene el formato esperado.");
            }
            mejor = elegirCandidato(salida[0], escala);
        }

        if (mejor == null) {
            return Resultado.sinDeteccion();
        }

        String color = EstimadorColor.estimar(imagen, mejor.x1(), mejor.y1(),
                mejor.x2(), mejor.y2());
        return new Resultado("SUGERENCIA", TIPOS_SOPORTADOS.get(mejor.clase()),
                color, Math.round(mejor.puntuacion() * 1000d) / 1000d, true);
    }

    private float[][][][] prepararEntrada(BufferedImage original, float escala) {
        int ancho = Math.max(1, (int) (original.getWidth() * escala));
        int alto = Math.max(1, (int) (original.getHeight() * escala));
        BufferedImage reducida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D grafico = reducida.createGraphics();
        try {
            grafico.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            grafico.drawImage(original, 0, 0, ancho, alto, null);
        } finally {
            grafico.dispose();
        }

        float[][][][] tensor = new float[1][3][LADO][LADO];
        for (int y = 0; y < LADO; y++) {
            for (int x = 0; x < LADO; x++) {
                int rgb = x < ancho && y < alto ? reducida.getRGB(x, y) : 0x727272;
                // El modelo exportado recibe BGR en escala 0..255.
                tensor[0][0][y][x] = rgb & 0xFF;
                tensor[0][1][y][x] = (rgb >>> 8) & 0xFF;
                tensor[0][2][y][x] = (rgb >>> 16) & 0xFF;
            }
        }
        return tensor;
    }

    private Candidato elegirCandidato(float[][] predicciones, float escala) {
        Candidato mejor = null;
        int fila = 0;
        for (int stride : new int[]{8, 16, 32}) {
            int ladoCuadricula = LADO / stride;
            for (int y = 0; y < ladoCuadricula; y++) {
                for (int x = 0; x < ladoCuadricula; x++) {
                    float[] prediccion = predicciones[fila++];
                    if (prediccion.length != CLASES_MODELO + 5
                            || !Float.isFinite(prediccion[4])) {
                        throw new IllegalStateException("Predicción ONNX inválida.");
                    }
                    if (prediccion[4] < UMBRAL_SUGERENCIA) {
                        continue;
                    }
                    int clase = -1;
                    float probabilidadClase = 0;
                    for (int indice = 0; indice < CLASES_MODELO; indice++) {
                        float probabilidad = prediccion[5 + indice];
                        if (Float.isFinite(probabilidad)
                                && probabilidad > probabilidadClase) {
                            clase = indice;
                            probabilidadClase = probabilidad;
                        }
                    }
                    float puntuacion = prediccion[4] * probabilidadClase;
                    if (!TIPOS_SOPORTADOS.containsKey(clase)
                            || puntuacion < UMBRAL_SUGERENCIA
                            || (mejor != null && puntuacion <= mejor.puntuacion())) {
                        continue;
                    }

                    float centroX = (prediccion[0] + x) * stride / escala;
                    float centroY = (prediccion[1] + y) * stride / escala;
                    float ancho = (float) Math.exp(Math.max(-10, Math.min(10, prediccion[2])))
                            * stride / escala;
                    float alto = (float) Math.exp(Math.max(-10, Math.min(10, prediccion[3])))
                            * stride / escala;
                    if (!Float.isFinite(centroX) || !Float.isFinite(centroY)
                            || !Float.isFinite(ancho) || !Float.isFinite(alto)) {
                        continue;
                    }
                    mejor = new Candidato(clase, puntuacion,
                            centroX - ancho / 2, centroY - alto / 2,
                            centroX + ancho / 2, centroY + alto / 2);
                }
            }
        }
        return mejor;
    }

    private synchronized OrtSession obtenerSesion() throws Exception {
        if (sesion == null) {
            byte[] modelo;
            try (InputStream flujo = new ClassPathResource(
                    "models/yolox_nano.onnx").getInputStream()) {
                modelo = flujo.readAllBytes();
            }
            try (OrtSession.SessionOptions opciones = new OrtSession.SessionOptions()) {
                opciones.setIntraOpNumThreads(2);
                sesion = entorno.createSession(modelo, opciones);
            }
        }
        return sesion;
    }

    @PreDestroy
    public synchronized void cerrar() throws Exception {
        if (sesion != null) {
            sesion.close();
            sesion = null;
        }
    }

    private record Candidato(int clase, float puntuacion,
                             float x1, float y1, float x2, float y2) {}
}
