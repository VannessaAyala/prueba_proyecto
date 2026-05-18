package com.tienda.ropa.backend.service.impl;

import com.tienda.ropa.backend.domain.DetallePedido;
import com.tienda.ropa.backend.domain.Pedido;
import com.tienda.ropa.backend.domain.Producto;
import com.tienda.ropa.backend.domain.Usuario;
import com.tienda.ropa.backend.dto.pedido.PedidoCreateRequest;
import com.tienda.ropa.backend.dto.pedido.PedidoResponse;
import com.tienda.ropa.backend.repository.PedidoRepository;
import com.tienda.ropa.backend.repository.ProductoRepository;
import com.tienda.ropa.backend.repository.UsuarioRepository;
import com.tienda.ropa.backend.service.PedidoService;
import com.tienda.ropa.backend.web.advice.ConflictException;
import com.tienda.ropa.backend.web.advice.NotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación del servicio de Pedidos.
 *
 * Reglas de negocio:
 *  - El usuario debe existir y estar activo.
 *  - Cada producto debe existir, estar activo y tener stock suficiente.
 *  - El subtotal de cada ítem = precio * cantidad.
 *  - El total del pedido = suma de todos los subtotales.
 *  - Estado inicial: "PENDIENTE".
 *  - Se descuenta el stock al confirmar el pedido.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository   pedidoRepo;
    private final UsuarioRepository  usuarioRepo;
    private final ProductoRepository productoRepo;

    // Inyección de dependencias por constructor
    public PedidoServiceImpl(PedidoRepository pedidoRepo,
                             UsuarioRepository usuarioRepo,
                             ProductoRepository productoRepo) {
        this.pedidoRepo   = pedidoRepo;
        this.usuarioRepo  = usuarioRepo;
        this.productoRepo = productoRepo;
    }

    // ── Crear un pedido ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public PedidoResponse create(PedidoCreateRequest request) {

        // 1. Validar usuario existe y está activo
        Usuario usuario = usuarioRepo.findById(request.getIdUsuario())
                .orElseThrow(() -> new NotFoundException(
                        "Usuario no encontrado con id: " + request.getIdUsuario()));

        if (!Boolean.TRUE.equals(usuario.getActive())) {
            throw new ConflictException(
                    "El usuario está desactivado y no puede hacer pedidos.");
        }

        // 2. Construir el pedido con valores iniciales
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("PENDIENTE");

        double total = 0.0;

        // 3. Procesar cada ítem
        for (PedidoCreateRequest.ProductoItemRequest item : request.getProductos()) {

            Producto producto = productoRepo.findById(item.getIdProducto())
                    .orElseThrow(() -> new NotFoundException(
                            "Producto no encontrado con id: " + item.getIdProducto()));

            // Validar producto activo
            if (!Boolean.TRUE.equals(producto.getActive())) {
                throw new ConflictException(
                        "El producto '" + producto.getNombre() + "' no está disponible.");
            }

            // Validar stock suficiente
            if (producto.getStock() < item.getCantidad()) {
                throw new ConflictException(
                        "Stock insuficiente para '" + producto.getNombre() +
                                "'. Disponible: " + producto.getStock() +
                                ", solicitado: " + item.getCantidad());
            }

            // Calcular subtotal
            double subtotal = producto.getPrecio() * item.getCantidad();
            total += subtotal;

            // Armar el detalle y vincularlo al pedido
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setSubtotal(subtotal);
            pedido.addDetalle(detalle);

            // Descontar stock
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepo.save(producto);
        }

        pedido.setTotal(total);
        return toResponse(pedidoRepo.save(pedido));
    }

    // ── Obtener pedido por ID ─────────────────────────────────────────────────

    @Override
    public PedidoResponse getById(Long id) {
        return toResponse(
                pedidoRepo.findById(id)
                        .orElseThrow(() -> new NotFoundException(
                                "Pedido no encontrado con id: " + id)));
    }

    // ── Listar todos ──────────────────────────────────────────────────────────

    @Override
    public List<PedidoResponse> list() {
        return pedidoRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Actualizar estado ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public PedidoResponse updateEstado(Long id, String nuevoEstado) {

        List<String> estadosValidos =
                List.of("PENDIENTE", "APROBADO", "RECHAZADO", "ENVIADO", "ENTREGADO");

        if (!estadosValidos.contains(nuevoEstado.toUpperCase())) {
            throw new ConflictException(
                    "Estado inválido: '" + nuevoEstado +
                            "'. Valores permitidos: " + estadosValidos);
        }

        Pedido pedido = pedidoRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Pedido no encontrado con id: " + id));

        pedido.setEstado(nuevoEstado.toUpperCase());
        return toResponse(pedidoRepo.save(pedido));
    }

    // ── Mapeo Entidad -> DTO de salida ────────────────────────────────────────

    private PedidoResponse toResponse(Pedido p) {
        PedidoResponse r = new PedidoResponse();
        r.setId(p.getId());
        r.setUsuario(p.getUsuario().getNombre());
        r.setFecha(p.getFecha());
        r.setTotal(p.getTotal());
        r.setEstado(p.getEstado());
        return r;
    }
}