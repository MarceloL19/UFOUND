package pe.edu.ulima.ufound.analisis;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.edu.ulima.ufound.model.Rol;
import pe.edu.ulima.ufound.model.Usuario;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class EnlaceAnalisisPerdidasFilter extends OncePerRequestFilter {

    static final String MARCA = "data-analisis-perdidas";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        String contexto = request.getContextPath() == null ? "" : request.getContextPath();
        String ruta = uri.startsWith(contexto) ? uri.substring(contexto.length()) : uri;
        return ruta.startsWith("/css/") || ruta.startsWith("/img/") || ruta.startsWith("/js/") || ruta.startsWith("/uploads/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioAutenticado");
        if (usuario == null || usuario.getRol() != Rol.OFICINA) {
            filterChain.doFilter(request, response);
            return;
        }

        RespuestaCapturada captura = new RespuestaCapturada(response);
        filterChain.doFilter(request, captura);
        byte[] cuerpo = captura.contenido();
        String tipo = captura.getContentType();
        if (cuerpo.length > 0 && tipo != null && tipo.contains("text/html")) {
            String html = new String(cuerpo, StandardCharsets.UTF_8);
            String actualizado = insertarEnlace(html, enlace(request));
            if (!actualizado.equals(html)) {
                cuerpo = actualizado.getBytes(StandardCharsets.UTF_8);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            }
        }
        if (cuerpo.length > 0 && !response.isCommitted()) {
            response.setContentLength(cuerpo.length);
            response.getOutputStream().write(cuerpo);
        }
    }

    static String insertarEnlace(String html, String enlace) {
        if (html == null || html.contains(MARCA)) {
            return html;
        }
        int cierre = html.indexOf("</nav>");
        if (cierre < 0) {
            return html;
        }
        return html.substring(0, cierre) + enlace + html.substring(cierre);
    }

    private String enlace(HttpServletRequest request) {
        String contexto = request.getContextPath() == null ? "" : request.getContextPath();
        String ruta = request.getRequestURI();
        String clase = ruta.contains("/analisis-perdidas") ? " class=\"active\"" : "";
        return "<a " + MARCA + " href=\"" + contexto + "/analisis-perdidas\"" + clase + ">Analisis de perdidas</a>";
    }

    private static final class RespuestaCapturada extends jakarta.servlet.http.HttpServletResponseWrapper {
        private final java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        private ServletOutputStream outputStream;
        private PrintWriter writer;

        private RespuestaCapturada(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (outputStream == null) {
                outputStream = new ServletOutputStream() {
                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                    }

                    @Override
                    public void write(int b) {
                        buffer.write(b);
                    }
                };
            }
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8), false);
            }
            return writer;
        }

        @Override
        public void flushBuffer() {
            if (writer != null) {
                writer.flush();
            }
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public void setContentLengthLong(long len) {
        }

        private byte[] contenido() {
            if (writer != null) {
                writer.flush();
            }
            return buffer.toByteArray();
        }
    }
}
