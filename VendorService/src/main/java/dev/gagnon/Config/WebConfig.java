package dev.gagnon.Config;

// Expose /uploads/** URL to point to the physical directory
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // This tells Spring Boot to serve anything under /uploads/** from the real folder
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:/var/www/bdic_virtual_market_BackEnd/uploads/");
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = Paths.get("uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir);
    }
}
