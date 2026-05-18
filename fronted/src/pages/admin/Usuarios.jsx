import React, { useState, useEffect } from 'react';
import { api } from '../../services/api';

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [form, setForm] = useState({ nombre: '', correo: '', contrasena: '', rol: 'USER' });
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    loadUsuarios();
  }, []);

  const loadUsuarios = () => api.usuarios.getAll().then(setUsuarios);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = { ...form };
      if (editingId) {
        if (!payload.contrasena) delete payload.contrasena;
        await api.usuarios.update(editingId, payload);
      } else {
        await api.usuarios.create(payload);
      }
      setForm({ nombre: '', correo: '', contrasena: '', rol: 'USER' });
      setEditingId(null);
      loadUsuarios();
    } catch (err) {
      alert("Error al guardar usuario. Verifica que el correo sea válido y todos los campos estén llenos.");
    }
  };

  const handleEdit = (u) => {
    setEditingId(u.id);
    setForm({ nombre: u.nombre, correo: u.correo, contrasena: '', rol: u.rol || 'USER' });
  };

  const handleToggleEstado = async (id, currentActive) => {
    try {
      if (currentActive) {
        await api.usuarios.deactivate(id);
      } else {
        await api.usuarios.update(id, { active: true });
      }
      loadUsuarios();
    } catch (e) {
      alert("Error al cambiar estado");
    }
  };

  return (
    <div>
      <div className="page-header">
        <h2>Gestión de Usuarios</h2>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
        <div className="cart-summary">
          <h3>{editingId ? 'Editar' : 'Nuevo'} Usuario</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Nombre</label>
              <input className="form-control" name="nombre" value={form.nombre} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Correo</label>
              <input className="form-control" type="email" name="correo" value={form.correo} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Contraseña {editingId && '(Opcional para no cambiar)'}</label>
              <input className="form-control" type="password" name="contrasena" value={form.contrasena} onChange={handleChange} required={!editingId} />
            </div>
            <div className="form-group">
              <label>Rol</label>
              <select className="form-control" name="rol" value={form.rol} onChange={handleChange} required>
                <option value="USER">Usuario Normal</option>
                <option value="ADMIN">Administrador</option>
              </select>
            </div>
            <div style={{ display: 'flex', gap: '1rem' }}>
              <button type="submit" className="btn" style={{ flex: 1 }}>Guardar</button>
              {editingId && (
                <button type="button" className="btn btn-outline" onClick={() => { setEditingId(null); setForm({ nombre: '', correo: '', contrasena: '', rol: 'USER' }); }}>Cancelar</button>
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
                <th>Correo</th>
                <th>Rol</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map(u => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td><strong>{u.nombre}</strong></td>
                  <td>{u.correo}</td>
                  <td>{u.rol}</td>
                  <td>
                    <span className={`status-badge ${!u.active ? 'inactive' : ''}`}>
                      {u.active ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td>
                    <div className="action-buttons">
                      <button className="btn" style={{ padding: '0.25rem 0.5rem' }} onClick={() => handleEdit(u)}>Editar</button>
                      <button className={`btn ${u.active ? 'btn-danger' : 'btn-outline'}`} style={{ padding: '0.25rem 0.5rem' }} onClick={() => handleToggleEstado(u.id, u.active)}>
                        {u.active ? 'Desactivar' : 'Activar'}
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
