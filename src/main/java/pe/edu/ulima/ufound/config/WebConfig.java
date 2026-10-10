package pe.edu.ulima.ufound.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SesionInterceptor sesionInterceptor;

    private final Path uploadsPath;

    public WebConfig(SesionInterceptor sesionInterceptor, @Value("${ufound.upload-dir:uploads}") String uploadDir) {
        this.sesionInterceptor = sesionInterceptor;
        this.uploadsPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sesionInterceptor)
                .addPathPatterns("/home/**", "/objetos-perdidos/**", "/objetos-encontrados/**", "/estados/**", "/oficina/**")
                .addPathPatterns("/coincidencias/**", "/notificaciones/**", "/buscar/**", "/ayuda", "/ayuda/**")
                .excludePathPatterns("/", "/login", "/css/**", "/img/**", "/js/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ubicacion = uploadsPath.toUri().toString();
        // Spring resuelve /uploads/** dentro de este directorio, aunque se cree después del arranque.
        if (!ubicacion.endsWith("/")) {
            ubicacion += "/";
        }
        // Las fotos predeterminadas conservan sus nombres; los uploads reales tienen prioridad.
        registry.addResourceHandler("/uploads/objetos-perdidos/**")
                .addResourceLocations(ubicacion + "objetos-perdidos/", "classpath:/static/fotos/");
        registry.addResourceHandler("/uploads/objetos-encontrados/**")
                .addResourceLocations(ubicacion + "objetos-encontrados/", "classpath:/static/fotos/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(ubicacion);
    }
}

