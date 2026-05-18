package com.tienda.ropa.backend.service.impl;

import com.tienda.ropa.backend.dto.usuario.UsuarioCreateRequest;
import com.tienda.ropa.backend.dto.usuario.UsuarioResponse;
import com.tienda.ropa.backend.domain.Usuario;
import com.tienda.ropa.backend.repository.UsuarioRepository;
import com.tienda.ropa.backend.web.advice.ConflictException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test de Servicio – UsuarioServiceImpl
 *
 * Laboratorio 2 - Paso 6b:
 *  ✅ Demuestra CI con lógica de negocio
 *  ✅ Valida la regla de correo único (ConflictException si ya existe)
 *  ✅ Usa Mockito: no toca base de datos real
 *
 * @ExtendWith(MockitoExtension.class) inicializa los mocks automáticamente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl – Tests de lógica de negocio")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioCreateRequest requestValido;

    @BeforeEach
    void setUp() {
        requestValido = new UsuarioCreateRequest();
        requestValido.setNombre("Irving Martinez");
        requestValido.setCorreo("irving@gmail.com");
        requestValido.setContrasena("pass1234");
        requestValido.setRol("CLIENTE");
    }

    // ── Test 1: Crear usuario exitosamente ────────────────────────────────────

    @Test
    @DisplayName("Debe crear un usuario cuando el correo no existe")
    void debeCrearUsuarioCuandoCorreoNoExiste() {
        // Arrange – el correo NO existe en la BD
        when(usuarioRepository.existsByCorreo(requestValido.getCorreo()))
                .thenReturn(false);

        Usuario usuarioGuardado = buildUsuario(1L, requestValido);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuarioGuardado);

        // Act
        UsuarioResponse response = usuarioService.create(requestValido);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo("Irving Martinez");
        assertThat(response.getCorreo()).isEqualTo("irving@gmail.com");
        assertThat(response.getRol()).isEqualTo("CLIENTE");
        assertThat(response.getActive()).isTrue();

        // Verifica que sí se llamó a save() exactamente una vez
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // ── Test 2: Regla de correo único ─────────────────────────────────────────

    @Test
    @DisplayName("Debe lanzar ConflictException cuando el correo ya está registrado")
    void debeLanzarConflictExceptionSiCorreoYaExiste() {
        // Arrange – el correo YA existe en la BD
        when(usuarioRepository.existsByCorreo(requestValido.getCorreo()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.create(requestValido))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("correo");  // El mensaje menciona "correo"

        // Verifica que NO se llamó a save() (no debe persistir nada)
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ── Test 3: Desactivar usuario ────────────────────────────────────────────

    @Test
    @DisplayName("Debe desactivar un usuario cambiando active a false")
    void debeDesactivarUsuario() {
        // Arrange
        Usuario usuario = buildUsuario(1L, requestValido);
        when(usuarioRepository.findById(1L))
                .thenReturn(java.util.Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        UsuarioResponse response = usuarioService.deactivate(1L);

        // Assert
        assertThat(response.getActive()).isFalse();
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // ── Test 4: Obtener usuario por ID inexistente ────────────────────────────

    @Test
    @DisplayName("Debe lanzar NotFoundException si el usuario no existe")
    void debeLanzarNotFoundSiUsuarioNoExiste() {
        // Arrange
        when(usuarioRepository.findById(99L))
                .thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.getById(99L))
                .isInstanceOf(com.tienda.ropa.backend.web.advice.NotFoundException.class)
                .hasMessageContaining("Usuario");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Usuario buildUsuario(Long id, UsuarioCreateRequest req) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre(req.getNombre());
        u.setCorreo(req.getCorreo());
        u.setContrasena(req.getContrasena());
        u.setRol(req.getRol());
        u.setActive(true);
        return u;
    }
}