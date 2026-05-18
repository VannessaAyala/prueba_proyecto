package com.tienda.ropa.backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor HTTP para logging y medición de tiempo de respuesta.
 *
 * Requisito Paso 8 del laboratorio:
 *  a. Marca el tiempo de inicio y registra el endpoint.
 *  b. Calcula la duración total y registra el status HTTP.
 *
 * Registrado en WebConfig para todas las rutas /api/**.
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    // Logger profesional (SLF4J) - escribe en consola con niveles INFO/WARN/ERROR
    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    // Nombre del atributo donde guardamos el timestamp de inicio
    private static final String START_TIME = "requestStartTime";

    /**
     * Se ejecuta ANTES de que el request llegue al controlador.
     * Guarda el timestamp y loguea el método + URI.
     *
     * @return true para continuar con la cadena; false para cortar aquí.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        request.setAttribute(START_TIME, System.currentTimeMillis());

        log.info("[REQUEST]  {} {} | IP: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        return true;
    }

    /**
     * Se ejecuta DESPUÉS de que el controlador termina (éxito o excepción).
     * Calcula la duración y loguea el status HTTP.
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        Long startTime = (Long) request.getAttribute(START_TIME);
        long elapsed   = (startTime != null)
                ? System.currentTimeMillis() - startTime
                : -1L;

        if (ex != null) {
            log.error("[RESPONSE] {} {} | Status: {} | Tiempo: {}ms | Error: {}",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsed, ex.getMessage());
        } else {
            log.info("[RESPONSE] {} {} | Status: {} | Tiempo: {}ms",
                    request.getMethod(), request.getRequestURI(),
                    response.getStatus(), elapsed);
        }
    }
}