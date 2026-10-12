package pe.edu.ulima.ufound.analisis;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pe.edu.ulima.ufound.config.SesionInterceptor;

@Configuration
public class AnalisisPerdidasWebConfig implements WebMvcConfigurer {

    private final SesionInterceptor sesionInterceptor;

    public AnalisisPerdidasWebConfig(SesionInterceptor sesionInterceptor) {
        this.sesionInterceptor = sesionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sesionInterceptor)
                .addPathPatterns("/analisis-perdidas/**");
    }
}
