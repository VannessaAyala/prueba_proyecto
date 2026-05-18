import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function MisPedidos() {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    api.pedidos.getAll()
      .then(data => {
        // Filtrar solo los pedidos del usuario logueado
        const misPedidos = data.filter(p => p.usuario === user.nombre);
        setPedidos(misPedidos);
      })
      .finally(() => setLoading(false));
  }, [user]);

  if (loading) return <div className="container"><p>Cargando tus pedidos...</p></div>;

  return (
    <div className="container">
      <h2 style={{ marginBottom: '2rem' }}>Mis Pedidos</h2>

      {pedidos.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '3rem', background: 'var(--card-bg)', borderRadius: '8px', border: '1px solid var(--border-color)' }}>
          <p>Aún no tienes pedidos realizados.</p>
        </div>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Fecha</th>
              <th>Total</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            {pedidos.map(p => (
              <tr key={p.id}>
                <td>#{p.id}</td>
                <td>{p.fecha}</td>
                <td><strong>${parseFloat(p.total).toFixed(2)}</strong></td>
                <td>
                  <span className="status-badge" style={{
                    background: p.estado === 'APROBADO' ? 'var(--success-color)' : 
                                p.estado === 'RECHAZADO' ? 'var(--danger-color)' : '#f59e0b'
                  }}>
                    {p.estado}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
