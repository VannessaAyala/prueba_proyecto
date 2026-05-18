import React, { useState } from 'react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { api } from '../services/api';
import { useNavigate } from 'react-router-dom';

export default function Cart() {
  const { cart, removeFromCart, updateQuantity, totalPrice, clearCart } = useCart();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleCheckout = async () => {
    setLoading(true);
    try {
      const payload = {
        idUsuario: user.id,
        productos: cart.map(item => ({
          idProducto: item.id,
          cantidad: item.quantity
        }))
      };

      await api.pedidos.create(payload);
      alert("¡Pedido realizado con éxito! Espera la aprobación del administrador.");
      clearCart();
      navigate('/mis-pedidos');
    } catch (error) {
      alert("Error al procesar el pedido.");
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (cart.length === 0) {
    return (
      <div className="container" style={{ textAlign: 'center', padding: '4rem' }}>
        <h2>Tu carrito está vacío</h2>
        <p style={{ marginTop: '1rem', color: 'var(--text-muted)' }}>Explora nuestro catálogo y añade algunos productos.</p>
        <button className="btn" style={{ marginTop: '2rem' }} onClick={() => navigate('/')}>Ir a la Tienda</button>
      </div>
    );
  }

  return (
    <div className="container">
      <h2 style={{ marginBottom: '2rem' }}>Carrito de Compras</h2>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '2rem' }}>
        <div>
          {cart.map(item => (
            <div key={item.id} className="cart-item">
              <div className="cart-item-info">
                <div className="cart-item-image">👕</div>
                <div>
                  <h4 style={{ margin: 0 }}>{item.nombre}</h4>
                  <p style={{ margin: 0, color: 'var(--text-muted)' }}>${parseFloat(item.precio).toFixed(2)} c/u</p>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <div className="qty-controls">
                  <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>-</button>
                  <span style={{ width: '20px', textAlign: 'center' }}>{item.quantity}</span>
                  <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</button>
                </div>
                <button
                  className="btn btn-danger"
                  style={{ padding: '0.25rem 0.5rem', fontSize: '0.75rem' }}
                  onClick={() => removeFromCart(item.id)}
                >
                  Eliminar
                </button>
              </div>
            </div>
          ))}
        </div>

        <div>
          <div className="cart-summary">
            <h3>Resumen</h3>
            <p style={{ color: 'var(--text-muted)', marginBottom: '1rem' }}>
              Comprando como: <strong>{user?.nombre}</strong>
            </p>

            <div className="total">
              <span>Total:</span>
              <span>${totalPrice.toFixed(2)}</span>
            </div>

            <button
              className="btn"
              style={{ width: '100%', padding: '1rem' }}
              onClick={handleCheckout}
              disabled={loading}
            >
              {loading ? 'Procesando...' : 'Confirmar Pedido'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
