package com.tienda.ropa.backend.web.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la API.
 *
 * Requisito Paso 7 del laboratorio:
 *  - 404: NotFoundException (recurso no encontrado)
 *  - 409: ConflictException (duplicados, stock insuficiente, etc.)
 *  - 400: Validaciones fallidas de @Valid en los DTOs
 *  - 500: Última red de seguridad para errores inesperados
 *  - JSON estándar de error en todas las respuestas
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── 404 – Recurso no encontrado ───────────────────────────────────────────

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        log.warn("NotFoundException: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 409 – Conflicto de negocio ────────────────────────────────────────────

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        log.warn("ConflictException: {}", ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    // ── 400 – Validación fallida (@Valid) ─────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        // Recolecta todos los errores campo → mensaje
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        Map<String, Object> body = buildErrorBody(
                HttpStatus.BAD_REQUEST,
                "Validación fallida. Revisa los campos enviados.");
        body.put("fields", fieldErrors); // Detalle de qué campos fallaron

        log.warn("ValidationException - campos inválidos: {}", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ── 500 – Última red de seguridad ─────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor. Contacta al administrador.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Construye el JSON estándar de error:
     * {
     *   "timestamp": "2024-...",
     *   "status":    404,
     *   "error":     "Not Found",
     *   "message":   "..."
     * }
     */
    private ResponseEntity<Map<String, Object>> buildError(
            HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(buildErrorBody(status, message));
    }

    private Map<String, Object> buildErrorBody(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        return body;
    }
}