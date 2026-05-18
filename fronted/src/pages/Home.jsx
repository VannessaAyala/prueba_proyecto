import React, { useEffect, useState } from 'react';
import { api } from '../services/api';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Home() {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const { addToCart } = useCart();
  const { isLoggedIn, isAdmin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isLoggedIn && isAdmin) {
      navigate('/admin/productos', { replace: true });
      return;
    }
    api.productos.getAll()
      .then(setProductos)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [isLoggedIn, isAdmin, navigate]);

  const handleAddToCart = (producto) => {
    if (!isLoggedIn) {
      alert('Debes iniciar sesión para agregar productos al carrito');
      navigate('/login');
      return;
    }
    addToCart(producto);
  };

  return (
    <div>
      {/* HERO / PORTADA */}
      <div style={{
        background: 'linear-gradient(135deg, #0f172a 0%, #334155 100%)',
        color: 'white',
        padding: '6rem 2rem',
        textAlign: 'center',
        position: 'relative',
        overflow: 'hidden'
      }}>
        <div style={{ position: 'relative', zIndex: 1, maxWidth: '800px', margin: '0 auto' }}>
          <p style={{ textTransform: 'uppercase', letterSpacing: '4px', fontSize: '0.875rem', marginBottom: '1rem', opacity: 0.7 }}>
            Nueva Temporada 2026
          </p>
          <h1 style={{ fontSize: '3.5rem', fontWeight: 800, marginBottom: '1.5rem', lineHeight: 1.1 }}>
            Estilo que Define<br />tu Personalidad
          </h1>
          <p style={{ fontSize: '1.125rem', opacity: 0.8, marginBottom: '2rem', maxWidth: '500px', margin: '0 auto 2rem' }}>
            Descubre nuestra colección exclusiva de ropa con las últimas tendencias de moda.
          </p>
          <a href="#catalogo" style={{
            background: 'white', color: '#0f172a', padding: '1rem 2.5rem', borderRadius: '6px',
            textDecoration: 'none', fontWeight: 700, fontSize: '1rem', display: 'inline-block'
          }}>
            Ver Catálogo ↓
          </a>
        </div>
        {/* Decoración */}
        <div style={{
          position: 'absolute', top: '-50%', right: '-10%', width: '500px', height: '500px',
          borderRadius: '50%', background: 'rgba(255,255,255,0.03)'
        }} />
        <div style={{
          position: 'absolute', bottom: '-30%', left: '-5%', width: '300px', height: '300px',
          borderRadius: '50%', background: 'rgba(255,255,255,0.05)'
        }} />
      </div>

      {/* CATÁLOGO DE PRODUCTOS */}
      <div id="catalogo" className="container" style={{ paddingTop: '3rem' }}>
        <div className="page-header" style={{ marginBottom: '2rem' }}>
          <div>
            <h2>Nuestros Productos</h2>
          </div>
        </div>

        {loading ? (
          <p>Cargando productos...</p>
        ) : (
          <div className="products-grid">
            {productos.filter(p => p.active).map(producto => (
              <div key={producto.id} className="product-card">
                <div className="product-image">
                  👕
                </div>
                <div className="product-info">
                  <span style={{ fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '1px', color: 'var(--text-muted)', fontWeight: 600 }}>
                    {producto.categoria}
                  </span>
                  <h3>{producto.nombre}</h3>
                  <div className="price" style={{ marginTop: 'auto' }}>${parseFloat(producto.precio).toFixed(2)}</div>
                  <button
                    className="btn"
                    style={{ width: '100%', marginTop: '0.5rem' }}
                    onClick={() => handleAddToCart(producto)}
                  >
                    Añadir al Carrito
                  </button>
                </div>
              </div>
            ))}
            {productos.filter(p => p.active).length === 0 && (
              <p style={{ gridColumn: '1/-1', textAlign: 'center', padding: '2rem' }}>No hay productos disponibles por el momento.</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
