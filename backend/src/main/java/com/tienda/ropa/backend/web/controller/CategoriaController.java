package com.tienda.ropa.backend.web.controller;

import com.tienda.ropa.backend.dto.categoria.*;

import com.tienda.ropa.backend.service.CategoriaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    // CREAR
    @PostMapping
    public ResponseEntity<CategoriaResponse> create(
            @Valid @RequestBody CategoriaCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    // OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // LISTAR TODAS
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> getAll() {

        return ResponseEntity.ok(service.list());
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaUpdateRequest request) {

        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
