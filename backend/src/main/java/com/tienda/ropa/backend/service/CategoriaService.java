package com.tienda.ropa.backend.service;

import com.tienda.ropa.backend.dto.categoria.*;

import java.util.List;

public interface CategoriaService {

    CategoriaResponse create(CategoriaCreateRequest request);

    CategoriaResponse getById(Long id);

    List<CategoriaResponse> list();

    CategoriaResponse update(Long id, CategoriaUpdateRequest request);

    void delete(Long id);
}
