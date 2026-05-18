package com.tienda.ropa.backend.web.controller;

import com.tienda.ropa.backend.domain.Usuario;
import com.tienda.ropa.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación básica.
 *
 * Nota: Este endpoint implementa autenticación simple por nombre + contraseña.
 * Para producción se recomienda migrar a Spring Security + JWT (actividad 3 del lab).
 *
 * POST /api/auth/login
 *  - Body: { "nombre": "...", "contrasena": "..." }
 *  - Respuesta exitosa (200): datos públicos del usuario
 *  - 401: credenciales inválidas
 *  - 403: usuario desactivado
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {

        String nombre    = credentials.get("nombre");
        String contrasena = credentials.get("contrasena");

        // Validación básica de campos
        if (nombre == null || nombre.isBlank() ||
                contrasena == null || contrasena.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Los campos 'nombre' y 'contrasena' son obligatorios."));
        }

        // Buscar usuario por nombre
        Usuario usuario = usuarioRepository.findByNombre(nombre)
                .orElse(null);

        // Credenciales inválidas (usuario no existe o contraseña incorrecta)
        // Retornamos el mismo mensaje para no dar pistas de seguridad
        if (usuario == null || !usuario.getContrasena().equals(contrasena)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas."));
        }

        // Usuario desactivado
        if (!Boolean.TRUE.equals(usuario.getActive())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Cuenta desactivada. Contacta al administrador."));
        }

        // Login exitoso: devolver solo datos públicos (nunca la contraseña)
        Map<String, Object> response = Map.of(
                "id",      usuario.getId(),
                "nombre",  usuario.getNombre(),
                "correo",  usuario.getCorreo(),
                "rol",     usuario.getRol(),
                "active",  usuario.getActive()
        );

        return ResponseEntity.ok(response);
    }
}