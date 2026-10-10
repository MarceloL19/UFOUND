package pe.edu.ulima.ufound.service;

import org.springframework.stereotype.Service;
import pe.edu.ulima.ufound.model.Rol;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AyudaService {

    // El catálogo describe únicamente pantallas y acciones que ya existen en UFOUND.
    private static final Map<Rol, List<Tema>> TEMAS = Map.of(
            Rol.ESTUDIANTE, List.of(
                    new Tema("reportar-perdida", "Reportar una pérdida", "¿Cómo registro un objeto perdido?",
                            "En el menú, abre Reportar objeto (/objetos-perdidos/registrar). Escribe el título y la descripción, elige categoría y ubicación y, si tienes una, adjunta una foto. La fecha y hora se asignan automáticamente. Envía el formulario; después podrás ver el reporte en Mis objetos perdidos.",
                            List.of("registrar un objeto perdido", "registrar objeto perdido", "registrar mi objeto perdido", "registro un objeto perdido", "registro mi objeto perdido", "registro de objeto perdido", "reportar objeto perdido", "reportar una perdida", "reportar perdida", "reporto una perdida", "perdi un objeto", "extravie un objeto")),
                    new Tema("buscar-hallazgos", "Buscar hallazgos", "¿Cómo busco un objeto encontrado?",
                            "Abre Buscar (/buscar) desde el menú. Puedes buscar por nombre o descripción y filtrar por categoría, ubicación o fechas. En los resultados, selecciona Ver detalle para consultar un hallazgo.",
                            List.of("buscar", "busco", "busqueda", "filtrar", "filtros", "encontrar un objeto", "hallazgos", "objeto encontrado")),
                    new Tema("mis-reportes", "Mis reportes", "¿Dónde veo mis reportes?",
                            "Abre Mis objetos perdidos (/objetos-perdidos/mis-reportes) desde el menú. Allí aparecen los reportes registrados por tu cuenta y el estado de cada uno.",
                            List.of("mis reportes", "mi reporte", "mis objetos perdidos", "historial de reportes")),
                    new Tema("seguimiento", "Seguimiento", "¿Cómo hago seguimiento de mi objeto?",
                            "Abre Mis objetos perdidos (/objetos-perdidos/mis-reportes) y pulsa Ver estado en tu reporte. Verás su estado y la línea de tiempo. También puedes revisar Coincidencias (/coincidencias) y Notificaciones (/notificaciones) si se detectó un posible hallazgo.",
                            List.of("seguimiento", "seguir mi objeto", "estado", "rastrear", "progreso"))
            ),
            Rol.SEGURIDAD, List.of(
                    new Tema("registrar-hallazgo", "Registrar un hallazgo", "¿Cómo registro un hallazgo?",
                            "Abre Registrar hallazgo (/objetos-encontrados/registrar). Escribe nombre y descripción, elige categoría y ubicación del hallazgo y, si tienes una, adjunta una foto. La fecha y hora se asignan automáticamente. Envía el formulario para abrir el detalle del objeto encontrado.",
                            List.of("registrar un hallazgo", "registrar hallazgo", "registro un hallazgo", "registro de hallazgo", "registrar un objeto encontrado", "registrar objeto encontrado", "encontre un objeto", "halle un objeto")),
                    new Tema("consultar-hallazgos", "Consultar hallazgos", "¿Dónde consulto los hallazgos?",
                            "Abre Objetos encontrados (/objetos-encontrados) desde el menú. Allí puedes ver la lista de hallazgos y abrir Ver detalle en cada objeto.",
                            List.of("consultar hallazgos", "consulto los hallazgos", "ver hallazgos", "hallazgos registrados", "objetos encontrados", "listado de hallazgos", "buscar hallazgos")),
                    new Tema("seguimiento-hallazgo", "Seguimiento", "¿Cómo veo el estado de un hallazgo?",
                            "Abre Objetos encontrados (/objetos-encontrados) y pulsa Ver estado en el hallazgo. La pantalla muestra su estado y la línea de tiempo; el cambio de estado corresponde a Oficina.",
                            List.of("seguimiento", "estado", "rastrear", "progreso"))
            ),
            Rol.OFICINA, List.of(
                    new Tema("consultar-estados", "Consultar estados", "¿Dónde consulto los estados?",
                            "Abre Estados (/oficina/estados) desde el menú. Verás la actividad reciente de objetos perdidos y encontrados; abre un objeto para revisar su estado y línea de tiempo.",
                            List.of("consultar estados", "consultar los estados", "consulto los estados", "consultar el estado", "ver estados", "ver el estado", "listado de estados", "seguimiento")),
                    new Tema("gestionar-estados", "Gestionar estados", "¿Cómo cambio el estado de un objeto?",
                            "Abre Estados (/oficina/estados), selecciona un objeto, elige el nuevo estado en su detalle y guarda el cambio. La actualización queda registrada en el historial del objeto.",
                            List.of("gestionar estados", "gestionar los estados", "cambiar estado", "cambiar el estado", "cambio el estado", "actualizar estado", "actualizar el estado", "modificar estado"))
            )
    );

    public List<Tema> temasPara(Rol rol) {
        return TEMAS.getOrDefault(rol, List.of());
    }

    public String preguntaSugerida(Rol rol, String id) {
        return temasPara(rol).stream()
                .filter(tema -> tema.id().equals(id))
                .map(Tema::pregunta)
                .findFirst()
                .orElse("");
    }

    public String saludo(Rol rol) {
        return "Hola. Puedo orientarte sobre " + opciones(rol) + ". Escribe una pregunta o elige un tema.";
    }

    public String responder(Rol rol, String pregunta) {
        String texto = normalizar(pregunta);
        if (texto.isBlank() || mencionaCuenta(texto)) {
            return limite(rol);
        }

        List<Tema> coincidencias = temasPara(rol).stream()
                .filter(tema -> tema.sinonimos().stream().anyMatch(frase -> contieneFrase(texto, frase)))
                .toList();
        // «Mi reporte» identifica el objeto del seguimiento, no una segunda solicitud.
        if (rol == Rol.ESTUDIANTE && coincidencias.size() == 2 && !contieneFrase(texto, "y")
                && coincidencias.stream().anyMatch(tema -> tema.id().equals("mis-reportes"))
                && coincidencias.stream().anyMatch(tema -> tema.id().equals("seguimiento"))) {
            return coincidencias.stream().filter(tema -> tema.id().equals("seguimiento"))
                    .findFirst().orElseThrow().respuesta();
        }
        return coincidencias.size() == 1 ? coincidencias.get(0).respuesta() : limite(rol);
    }

    private String limite(Rol rol) {
        return "No tengo una respuesta segura para esa consulta. Solo puedo orientar sobre "
                + opciones(rol) + ". Elige uno de esos temas o formula una pregunta más específica.";
    }

    private String opciones(Rol rol) {
        return temasPara(rol).stream().map(Tema::titulo).collect(Collectors.joining(", "));
    }

    private boolean mencionaCuenta(String texto) {
        return List.of("crear cuenta", "registrar usuario", "registro de usuario", "dar de alta", "contrasena", "password", "correo", "iniciar sesion")
                .stream().anyMatch(frase -> contieneFrase(texto, frase));
    }

    private boolean contieneFrase(String texto, String frase) {
        return (" " + texto + " ").contains(" " + normalizar(frase) + " ");
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return sinAcentos.replaceAll("[^a-z0-9]+", " ").trim().replaceAll(" +", " ");
    }

    public record Tema(String id, String titulo, String pregunta, String respuesta, List<String> sinonimos) {
    }
}
