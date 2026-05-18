package com.tienda.ropa.backend.service;

import com.tienda.ropa.backend.dto.producto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductoService {

    ProductoResponse create(ProductoCreateRequest request);

    ProductoResponse getById(Long id);

    List<ProductoResponse> list();

    ProductoResponse deactivate(Long id);

    ProductoResponse update(Long id, ProductoUpdateRequest request);

    Page<ProductoResponse> searchByName(
            String name,
            int page,
            int size
    );
}
