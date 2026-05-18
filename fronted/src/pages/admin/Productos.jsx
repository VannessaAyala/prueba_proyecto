import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';

export default function Productos() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [editingId, setEditingId] = useState(null);
  
  const [form, setForm] = useState({
    nombre: '',
    precio: '',
    stock: '',
    idCategoria: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = () => {
    api.productos.getAll().then(setProductos);
    api.categorias.getAll().then(setCategorias);
  };

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      nombre: form.nombre,
      precio: parseFloat(form.precio),
      stock: parseInt(form.stock),
      categoriaId: parseInt(form.idCategoria)
    };

    try {
      if (editingId) {
        await api.productos.update(editingId, payload);
      } else {
        await api.productos.create(payload);
      }
      setForm({ nombre: '', precio: '', stock: '', idCategoria: '' });
      setEditingId(null);
      loadData();
    } catch (err) {
      alert("Error al guardar el producto. Verifica que todos los campos sean válidos.");
    }
  };

  const handleEdit = (p) => {
    setEditingId(p.id);
    // El backend devuelve "categoria" como nombre (string), buscamos el ID correspondiente
    const cat = categorias.find(c => c.nombre === p.categoria);
    setForm({
      nombre: p.nombre,
      precio: p.precio,
      stock: p.stock,
      idCategoria: cat ? cat.id : ''
    });
  };

  const handleToggleEstado = async (id, currentActive) => {
    try {
      if (currentActive) {
        // Desactivar: usa el endpoint PATCH /deactivate
        await api.productos.deactivate(id);
      } else {
        // Reactivar: usa el endpoint PUT con active: true
        await api.productos.update(id, { active: true });
      }
      loadData();
    } catch (e) {
      alert("Error al cambiar el estado");
    }
  };

  return (
    <div>
      <div className="page-header">
        <h2>Gestión de Productos</h2>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
        <div className="cart-summary">
          <h3>{editingId ? 'Editar' : 'Nuevo'} Producto</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Categoría</label>
              <select className="form-control" name="idCategoria" value={form.idCategoria} onChange={handleChange} required>
                <option value="">-- Seleccionar --</option>
                {categorias.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Nombre</label>
              <input className="form-control" name="nombre" value={form.nombre} onChange={handleChange} required />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div className="form-group">
                <label>Precio ($)</label>
                <input className="form-control" type="number" step="0.01" name="precio" value={form.precio} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label>Stock</label>
                <input className="form-control" type="number" name="stock" value={form.stock} onChange={handleChange} required />
              </div>
            </div>
            
            <div style={{ display: 'flex', gap: '1rem' }}>
              <button type="submit" className="btn" style={{ flex: 1 }}>Guardar</button>
              {editingId && (
                <button type="button" className="btn btn-outline" onClick={() => { setEditingId(null); setForm({ nombre: '', precio: '', stock: '', idCategoria: '' }); }}>Cancelar</button>
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
                <th>Precio / Stock</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {productos.map(p => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td><strong>{p.nombre}</strong></td>
                  <td>${p.precio} / {p.stock} u.</td>
                  <td>
                    <span className={`status-badge ${!p.active ? 'inactive' : ''}`}>
                      {p.active ? 'Disponible' : 'Agotado'}
                    </span>
                  </td>
                  <td>
                    <div className="action-buttons">
                      <button className="btn" style={{ padding: '0.25rem 0.5rem' }} onClick={() => handleEdit(p)}>Editar</button>
                      <button className={`btn ${p.active ? 'btn-danger' : 'btn-outline'}`} style={{ padding: '0.25rem 0.5rem' }} onClick={() => handleToggleEstado(p.id, p.active)}>
                        {p.active ? 'Desactivar' : 'Activar'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
