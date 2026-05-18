package com.tienda.ropa.backend.repository;

import com.tienda.ropa.backend.domain.Categoria;
import com.tienda.ropa.backend.domain.Producto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
  Test de Repositorio - ProductoRepository
 
  Laboratorio 2 - Paso 6a:
   Valida que el mapeo JPA está bien (entidad → tabla)
   Valida que findByNombreContainingIgnoreCase funciona
   Usa H2 en memoria (perfil "test") — no toca MySQL real
 
  @DataJpaTest carga solo la capa JPA (sin controllers ni services).
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductoRepository – Tests de persistencia JPA")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaRopa;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos para que cada test sea independiente
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();

        // Crear una categoría base para los productos
        categoriaRopa = new Categoria();
        categoriaRopa.setNombre("Camisas");
        categoriaRopa = categoriaRepository.save(categoriaRopa);
    }

    // ── Test 1: Mapeo JPA correcto ────────────────────────────────────────────

    @Test
    @DisplayName("Debe persistir y recuperar un Producto correctamente (mapeo JPA OK)")
    void debeGuardarYRecuperarProducto() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Camisa Oxford Blanca");
        producto.setPrecio(29.99);
        producto.setStock(50);
        producto.setActive(true);
        producto.setCategoria(categoriaRopa);

        // Act
        Producto guardado = productoRepository.save(producto);
        Producto encontrado = productoRepository.findById(guardado.getId()).orElse(null);

        // Assert
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getNombre()).isEqualTo("Camisa Oxford Blanca");
        assertThat(encontrado.getPrecio()).isEqualTo(29.99);
        assertThat(encontrado.getStock()).isEqualTo(50);
        assertThat(encontrado.getActive()).isTrue();
        assertThat(encontrado.getCategoria().getNombre()).isEqualTo("Camisas");
    }

    // ── Test 2: Búsqueda por nombre (parcial, sin importar mayúsculas) ─────────

    @Test
    @DisplayName("findByNombreContainingIgnoreCase debe encontrar productos por nombre parcial")
    void debeBuscarProductosPorNombreParcial() {
        // Arrange – persistir 3 productos
        crearProducto("Camisa Oxford Blanca", 29.99);
        crearProducto("Camisa Polo Azul",     24.99);
        crearProducto("Pantalón Chino Beige", 49.99);

        // Act – buscar "camisa" (minúsculas) debe encontrar 2
        Page<Producto> resultado = productoRepository
                .findByNombreContainingIgnoreCase("camisa", PageRequest.of(0, 10));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getContent())
                .extracting(Producto::getNombre)
                .containsExactlyInAnyOrder("Camisa Oxford Blanca", "Camisa Polo Azul");
    }

    // ── Test 3: Búsqueda sin resultados ──────────────────────────────────────

    @Test
    @DisplayName("findByNombreContainingIgnoreCase debe retornar vacío si no hay coincidencias")
    void debeRetornarVacioSiNoHayCoincidencias() {
        // Arrange
        crearProducto("Camisa Oxford Blanca", 29.99);

        // Act
        Page<Producto> resultado = productoRepository
                .findByNombreContainingIgnoreCase("zapato", PageRequest.of(0, 10));

        // Assert
        assertThat(resultado.getTotalElements()).isZero();
    }

    // ── Test 4: H2 en memoria funciona correctamente ──────────────────────────

    @Test
    @DisplayName("La configuración de H2 para tests está OK (CRUD básico)")
    void configuracionH2EstaOk() {
        // Act
        List<Producto> inicial = productoRepository.findAll();
        assertThat(inicial).isEmpty(); // Base limpia antes de cada test

        crearProducto("Test Producto", 10.0);

        List<Producto> despues = productoRepository.findAll();
        assertThat(despues).hasSize(1);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void crearProducto(String nombre, double precio) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(10);
        p.setActive(true);
        p.setCategoria(categoriaRopa);
        productoRepository.save(p);
    }
}