package com.tienda.ropa.backend.config;

import com.tienda.ropa.backend.interceptor.RequestLoggingInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de Spring MVC.
 *
 * Responsabilidades:
 *  1. CORS: permite peticiones desde el frontend en Vite (puerto 5173).
 *  2. Interceptores: registra el RequestLoggingInterceptor para /api/**.
 *
 * Requisito Paso 8c del laboratorio: registrar el interceptor en /api/**.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestLoggingInterceptor requestLoggingInterceptor;

    // Inyección por constructor
    public WebConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    // ── CORS ──────────────────────────────────────────────────────────────────

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // ── Interceptores ─────────────────────────────────────────────────────────

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/api/**"); // Solo intercepta rutas de la API
    }
}