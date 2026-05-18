package com.tienda.ropa.backend.web.controller;

import com.tienda.ropa.backend.dto.producto.*;

import com.tienda.ropa.backend.service.ProductoService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // CREAR PRODUCTO
    @PostMapping
    public ResponseEntity<ProductoResponse> create(
            @Valid @RequestBody ProductoCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    // OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // LISTAR PRODUCTOS
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> getAll() {

        return ResponseEntity.ok(service.list());
    }

    // DESACTIVAR
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductoResponse> deactivate(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.deactivate(id));
    }

    // BUSCAR POR NOMBRE
    @GetMapping("/search")
    public ResponseEntity<Page<ProductoResponse>> search(
            @RequestParam String name,
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(
                service.searchByName(name, page, size)
        );
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateRequest request) {

        return ResponseEntity.ok(
                service.update(id, request)
        );
    }
}
