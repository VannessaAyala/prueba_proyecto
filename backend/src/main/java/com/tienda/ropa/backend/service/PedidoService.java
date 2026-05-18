package com.tienda.ropa.backend.service;

import com.tienda.ropa.backend.dto.pedido.PedidoCreateRequest;
import com.tienda.ropa.backend.dto.pedido.PedidoResponse;

import java.util.List;

/**
 * Contrato del servicio de Pedidos.
 * La interfaz permite intercambiar implementaciones y facilita pruebas unitarias.
 */
public interface PedidoService {

    /** Crea un nuevo pedido validando usuario, stock y precios. */
    PedidoResponse create(PedidoCreateRequest request);

    /** Busca un pedido por su ID. Lanza NotFoundException si no existe. */
    PedidoResponse getById(Long id);

    /** Retorna todos los pedidos registrados. */
    List<PedidoResponse> list();

    /** Cambia el estado del pedido (APROBADO, RECHAZADO, ENVIADO, ENTREGADO). */
    PedidoResponse updateEstado(Long id, String nuevoEstado);
}