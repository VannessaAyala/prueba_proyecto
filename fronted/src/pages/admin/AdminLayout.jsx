import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';

export default function AdminLayout() {
  const location = useLocation();
  const path = location.pathname;

  return (
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <h3 style={{ marginBottom: '2rem', paddingLeft: '1rem', color: 'var(--text-muted)' }}>Administración</h3>
        <Link to="/admin/categorias" className={path.includes('categorias') ? 'active' : ''}>Categorías</Link>
        <Link to="/admin/productos" className={path.includes('productos') ? 'active' : ''}>Productos</Link>
        <Link to="/admin/usuarios" className={path.includes('usuarios') ? 'active' : ''}>Usuarios</Link>
        <Link to="/admin/pedidos" className={path.includes('pedidos') ? 'active' : ''}>Pedidos</Link>
      </aside>
      
      <main className="admin-content">
        <Outlet />
      </main>
    </div>
  );
}
