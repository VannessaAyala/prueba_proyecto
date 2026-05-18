import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';

export default function Categorias() {
  const [categorias, setCategorias] = useState([]);
  const [nombre, setNombre] = useState('');
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    loadCategorias();
  }, []);

  const loadCategorias = () => {
    api.categorias.getAll().then(setCategorias);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!nombre) return;
    
    try {
      if (editingId) {
        await api.categorias.update(editingId, { nombre });
      } else {
        await api.categorias.create({ nombre });
      }
      setNombre('');
      setEditingId(null);
      loadCategorias();
    } catch (e) {
      alert("Error al guardar categoría");
    }
  };

  const handleEdit = (cat) => {
    setEditingId(cat.id);
    setNombre(cat.nombre);
  };

  const handleDelete = async (id) => {
    if (window.confirm("¿Seguro que deseas eliminar esta categoría?")) {
      try {
        await api.categorias.delete(id);
        loadCategorias();
      } catch (e) {
        alert("No se puede eliminar (podría tener productos asociados).");
      }
    }
  };

  return (
    <div>
      <div className="page-header">
        <h2>Gestión de Categorías</h2>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
        <div className="cart-summary">
          <h3>{editingId ? 'Editar' : 'Nueva'} Categoría</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Nombre</label>
              <input className="form-control" value={nombre} onChange={e => setNombre(e.target.value)} required />
            </div>
            <div style={{ display: 'flex', gap: '1rem' }}>
              <button type="submit" className="btn" style={{ flex: 1 }}>Guardar</button>
              {editingId && (
                <button type="button" className="btn btn-outline" onClick={() => { setEditingId(null); setNombre(''); }}>Cancelar</button>
              )}
            </div>
          </form>
        </div>

        <div>
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {categorias.map(cat => (
                <tr key={cat.id}>
                  <td>{cat.id}</td>
                  <td><strong>{cat.nombre}</strong></td>
                  <td>
                    <div className="action-buttons">
                      <button className="btn" style={{ padding: '0.25rem 0.5rem' }} onClick={() => handleEdit(cat)}>Editar</button>
                      <button className="btn btn-danger" style={{ padding: '0.25rem 0.5rem' }} onClick={() => handleDelete(cat.id)}>Eliminar</button>
                    </div>
                  </td>
                </tr>
              ))}
              {categorias.length === 0 && <tr><td colSpan="3" style={{ textAlign: 'center' }}>No hay categorías registradas</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
