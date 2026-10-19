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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class DeteccionVisualService {

    private static final int TAM = 416;
    private static final float UMBRAL = 0.35f;
    private static final float UMBRAL_NMS = 0.45f;
    private static final long MAX_PIXELES = 20_000_000L;

    private final OrtEnvironment entorno = OrtEnvironment.getEnvironment();
    private volatile OrtSession sesion;

    public record Resultado(
        String estado,
        String tipoProbable,
        String colorProbable,
        float confianza,
        boolean requiereConfirmacion
    ) {}

    record Deteccion(
        int clase,
        float puntuacion,
        float x1,
        float y1,
        float x2,
        float y2
    ) {}

    public Resultado analizar(byte[] datos) throws Exception {

        if (datos == null || datos.length == 0) {
            throw new IllegalArgumentException("Imagen vacia.");
        }

        BufferedImage imagen = ImageIO.read(
            new ByteArrayInputStream(datos)
        );

        if (imagen == null) {
            throw new IOException("No se pudo decodificar la imagen.");
        }

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        if ((long) ancho * alto > MAX_PIXELES) {
            throw new IllegalArgumentException(
                "La imagen supera el limite de pixeles."
            );
        }

        float escala = Math.min(
            (float) TAM / ancho,
            (float) TAM / alto
        );

        int nuevoAncho = Math.max(1, (int) (ancho * escala));
        int nuevoAlto = Math.max(1, (int) (alto * escala));

        BufferedImage redimensionada = new BufferedImage(
            nuevoAncho,
            nuevoAlto,
            BufferedImage.TYPE_INT_RGB
        );

        Graphics2D grafico = redimensionada.createGraphics();
        try {
            grafico.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            grafico.drawImage(
                imagen, 0, 0, nuevoAncho, nuevoAlto, null
            );
        } finally {
            grafico.dispose();
        }

        float[][][][] tensor = new float[1][3][TAM][TAM];

        for (int y = 0; y < TAM; y++) {
            for (int x = 0; x < TAM; x++) {
                tensor[0][0][y][x] = 114;
                tensor[0][1][y][x] = 114;
                tensor[0][2][y][x] = 114;
            }
        }

        for (int y = 0; y < nuevoAlto; y++) {
            for (int x = 0; x < nuevoAncho; x++) {
                int rgb = redimensionada.getRGB(x, y);

                tensor[0][0][y][x] = rgb & 255;
                tensor[0][1][y][x] = (rgb >> 8) & 255;
                tensor[0][2][y][x] = (rgb >> 16) & 255;
            }
        }

        List<Deteccion> candidatos = new ArrayList<>();

        try (OnnxTensor entrada =
                 OnnxTensor.createTensor(entorno, tensor);
             OrtSession.Result resultado =
                 obtenerSesion().run(Map.of("images", entrada))) {

            float[][][] salida =
                (float[][][]) resultado.get(0).getValue();

            float[][] predicciones = salida[0];
            int indice = 0;

            for (int stride : new int[]{8, 16, 32}) {
                int lado = TAM / stride;

                for (int gy = 0; gy < lado; gy++) {
                    for (int gx = 0; gx < lado; gx++) {

                        float[] p = predicciones[indice++];

                        float cx = (p[0] + gx) * stride;
                        float cy = (p[1] + gy) * stride;

                        float w = (float) Math.exp(
                            Math.max(-15, Math.min(15, p[2]))
                        ) * stride;

                        float h = (float) Math.exp(
                            Math.max(-15, Math.min(15, p[3]))
                        ) * stride;

                        int mejorClase = -1;
                        float mejorPuntuacion = 0;

                        for (int clase = 0; clase < 80; clase++) {
                            float puntuacion =
                                p[4] * p[5 + clase];

                            if (puntuacion > mejorPuntuacion) {
                                mejorPuntuacion = puntuacion;
                                mejorClase = clase;
                            }
                        }

                        if (mejorPuntuacion >= UMBRAL
                                && nombreClase(mejorClase) != null) {

                            candidatos.add(new Deteccion(
                                mejorClase,
                                mejorPuntuacion,
                                (cx - w / 2) / escala,
                                (cy - h / 2) / escala,
                                (cx + w / 2) / escala,
                                (cy + h / 2) / escala
                            ));
                        }
                    }
                }
            }

            if (indice != predicciones.length) {
                throw new IllegalStateException(
                    "Salida ONNX inesperada."
                );
            }
        }

        candidatos.sort(
            Comparator.comparingDouble(
                Deteccion::puntuacion
            ).reversed()
        );

        List<Deteccion> seleccionados =
            aplicarNms(candidatos, UMBRAL_NMS);

        if (seleccionados.isEmpty()) {
            return new Resultado(
                "SIN_DETECCION",
                null,
                "NO DETERMINADO",
                0f,
                true
            );
        }

        Deteccion principal = seleccionados.get(0);

        return new Resultado(
            "SUGERENCIA",
            nombreClase(principal.clase()),
            estimarColor(imagen, principal),
            principal.puntuacion(),
            true
        );
    }

    private synchronized OrtSession obtenerSesion() throws Exception {

        if (sesion == null) {
            byte[] modelo = new ClassPathResource(
                "models/yolox_nano.onnx"
            ).getInputStream().readAllBytes();

            try (OrtSession.SessionOptions opciones =
                     new OrtSession.SessionOptions()) {

                opciones.setIntraOpNumThreads(2);
                sesion = entorno.createSession(modelo, opciones);
            }
        }

        return sesion;
    }

    private static String nombreClase(int clase) {
        return switch (clase) {
            case 24 -> "Mochila";
            case 26 -> "Bolso";
            case 28 -> "Maleta";
            case 39 -> "Botella";
            case 63 -> "Laptop";
            case 67 -> "Celular";
            default -> null;
        };
    }

    static List<Deteccion> aplicarNms(
            List<Deteccion> candidatos, float umbral) {

        List<Deteccion> seleccionados = new ArrayList<>();

        for (Deteccion candidato : candidatos) {
            boolean duplicado = false;

            for (Deteccion aceptado : seleccionados) {
                if (candidato.clase() == aceptado.clase()
                        && calcularIoU(candidato, aceptado) >= umbral) {
                    duplicado = true;
                    break;
                }
            }

            if (!duplicado) {
                seleccionados.add(candidato);
            }
        }

        return seleccionados;
    }

    static float calcularIoU(Deteccion a, Deteccion b) {
        float izquierda = Math.max(a.x1(), b.x1());
        float arriba = Math.max(a.y1(), b.y1());
        float derecha = Math.min(a.x2(), b.x2());
        float abajo = Math.min(a.y2(), b.y2());

        float inter = Math.max(0f, derecha - izquierda)
                    * Math.max(0f, abajo - arriba);

        float areaA = Math.max(0f, a.x2() - a.x1())
                    * Math.max(0f, a.y2() - a.y1());

        float areaB = Math.max(0f, b.x2() - b.x1())
                    * Math.max(0f, b.y2() - b.y1());

        float union = areaA + areaB - inter;

        return union > 0 ? inter / union : 0f;
    }

    static String estimarColor(
            BufferedImage imagen, Deteccion deteccion) {

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        int x1 = Math.max(0, Math.min(ancho,
            Math.round(deteccion.x1())));
        int y1 = Math.max(0, Math.min(alto,
            Math.round(deteccion.y1())));
        int x2 = Math.max(0, Math.min(ancho,
            Math.round(deteccion.x2())));
        int y2 = Math.max(0, Math.min(alto,
            Math.round(deteccion.y2())));

        int margenX = (x2 - x1) / 5;
        int margenY = (y2 - y1) / 5;

        x1 += margenX;
        x2 -= margenX;
        y1 += margenY;
        y2 -= margenY;

        if (x2 <= x1 || y2 <= y1) {
            return "NO DETERMINADO";
        }

        int paso = 8;
        int capacidad =
            ((x2 - x1 + paso - 1) / paso)
          * ((y2 - y1 + paso - 1) / paso);

        int[] rojos = new int[capacidad];
        int[] verdes = new int[capacidad];
        int[] azules = new int[capacidad];

        int total = 0;

        for (int y = y1; y < y2; y += paso) {
            for (int x = x1; x < x2; x += paso) {
                int rgb = imagen.getRGB(x, y);
                rojos[total] = (rgb >> 16) & 255;
                verdes[total] = (rgb >> 8) & 255;
                azules[total] = rgb & 255;
                total++;
            }
        }

        Arrays.sort(rojos, 0, total);
        Arrays.sort(verdes, 0, total);
        Arrays.sort(azules, 0, total);

        int r = rojos[total / 2];
        int g = verdes[total / 2];
        int b = azules[total / 2];

        int maximo = Math.max(r, Math.max(g, b));
        int minimo = Math.min(r, Math.min(g, b));
        int diferencia = maximo - minimo;

        if (maximo < 80) return "NEGRO / MUY OSCURO";
        if (maximo > 205 && diferencia < 30)
            return "BLANCO / MUY CLARO";
        if (diferencia < 35) return "GRIS";

        return "NO DETERMINADO";
    }

    @PreDestroy
    public synchronized void cerrar() throws Exception {
        if (sesion != null) {
            sesion.close();
            sesion = null;
        }
    }
}
