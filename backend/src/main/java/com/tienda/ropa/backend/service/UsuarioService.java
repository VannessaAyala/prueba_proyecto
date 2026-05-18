package com.tienda.ropa.backend.service;


import com.tienda.ropa.backend.dto.usuario.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UsuarioService {

    UsuarioResponse create(UsuarioCreateRequest request);

    UsuarioResponse getById(Long id);

    List<UsuarioResponse> list();

    UsuarioResponse deactivate(Long id);

    UsuarioResponse update(Long id, UsuarioUpdateRequest request);

    Page<UsuarioResponse> searchByName(
            String name,
            int page,
            int size
    );
}
