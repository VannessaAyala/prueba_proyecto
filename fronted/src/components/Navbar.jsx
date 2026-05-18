import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { ShoppingBag, LogIn, LogOut, Settings, User } from 'lucide-react';

export default function Navbar() {
  const { totalItems } = useCart();
  const { user, isLoggedIn, isAdmin, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <Link to={isAdmin ? "/admin/productos" : "/"} className="navbar-brand">Nova Tienda</Link>
      <div className="navbar-links">
        {!isAdmin && (
          <Link to="/" className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}>Inicio</Link>
        )}

        {isLoggedIn && (
          <>
            {!isAdmin && (
              <>
                <Link to="/cart" className={`nav-link ${location.pathname === '/cart' ? 'active' : ''}`}>
                  <ShoppingBag size={18} style={{ verticalAlign: 'middle', marginRight: '4px' }} />
                  Carrito
                  {totalItems > 0 && <span className="cart-badge">{totalItems}</span>}
                </Link>
                <Link to="/mis-pedidos" className={`nav-link ${location.pathname === '/mis-pedidos' ? 'active' : ''}`}>
                  Mis Pedidos
                </Link>
              </>
            )}

            {isAdmin && (
              <Link to="/admin/productos" className={`nav-link ${location.pathname.startsWith('/admin') ? 'active' : ''}`}>
                <Settings size={18} style={{ verticalAlign: 'middle', marginRight: '4px' }} />
                Admin
              </Link>
            )}
          </>
        )}

        {isLoggedIn ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <span style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
              <User size={14} style={{ verticalAlign: 'middle', marginRight: '2px' }} />
              {user.nombre}
            </span>
            <button className="btn btn-outline" style={{ padding: '0.25rem 0.75rem', fontSize: '0.8rem' }} onClick={handleLogout}>
              <LogOut size={14} style={{ verticalAlign: 'middle', marginRight: '4px' }} />
              Salir
            </button>
          </div>
        ) : (
          <Link to="/login" className="btn" style={{ padding: '0.25rem 0.75rem', fontSize: '0.8rem' }}>
            <LogIn size={14} style={{ verticalAlign: 'middle', marginRight: '4px' }} />
            Ingresar
          </Link>
        )}
      </div>
    </nav>
  );
}
