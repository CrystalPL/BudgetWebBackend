package pl.crystalek.budgetweb.configuration.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://budgetapp.pl", "http://localhost:8091/")
                .allowedMethods("PATCH", "DELETE", "POST", "GET")
                .allowedHeaders("Content-Type", "accept",
                        "Access-Control-Request-Method", "Access-Control-Request-Headers")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                .allowCredentials(true);

//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedMethods("PUT", "DELETE", "POST", "GET")
//                .allowedHeaders("Content-Type", "accept",
//                        "Access-Control-Request-Method", "Access-Control-Request-Headers")
//                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials");
    }

}
