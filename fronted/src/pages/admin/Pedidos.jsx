import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';

export default function Pedidos() {
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadPedidos();
  }, []);

  const loadPedidos = () => {
    api.pedidos.getAll()
      .then(setPedidos)
      .finally(() => setLoading(false));
  };

  const handleCambiarEstado = async (id, nuevoEstado) => {
    try {
      await api.pedidos.updateEstado(id, nuevoEstado);
      loadPedidos();
    } catch (e) {
      alert("Error al actualizar el estado del pedido");
    }
  };

  if (loading) return <p>Cargando pedidos...</p>;

  return (
    <div>
      <div className="page-header">
        <h2>Gestión de Pedidos</h2>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Usuario</th>
            <th>Total</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {pedidos.map(p => (
            <tr key={p.id}>
              <td>#{p.id}</td>
              <td>{p.fecha}</td>
              <td>{p.usuario}</td>
              <td><strong>${parseFloat(p.total).toFixed(2)}</strong></td>
              <td>
                <span className="status-badge" style={{
                  background: p.estado === 'APROBADO' ? 'var(--success-color)' :
                              p.estado === 'RECHAZADO' ? 'var(--danger-color)' : '#f59e0b'
                }}>
                  {p.estado}
                </span>
              </td>
              <td>
                <div className="action-buttons">
                  {p.estado === 'PENDIENTE' && (
                    <>
                      <button
                        className="btn"
                        style={{ padding: '0.25rem 0.5rem', background: 'var(--success-color)' }}
                        onClick={() => handleCambiarEstado(p.id, 'APROBADO')}
                      >
                        Aprobar
                      </button>
                      <button
                        className="btn btn-danger"
                        style={{ padding: '0.25rem 0.5rem' }}
                        onClick={() => handleCambiarEstado(p.id, 'RECHAZADO')}
                      >
                        Rechazar
                      </button>
                    </>
                  )}
                  {p.estado !== 'PENDIENTE' && (
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Sin acciones</span>
                  )}
                </div>
              </td>
            </tr>
          ))}
          {pedidos.length === 0 && (
            <tr><td colSpan="6" style={{ textAlign: 'center' }}>No hay pedidos registrados</td></tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
