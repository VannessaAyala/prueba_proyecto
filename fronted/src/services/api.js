const BASE_URL = '/api';

const request = async (url, options = {}) => {
  const response = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });
  if (!response.ok) {
    throw new Error(`API Error: ${response.statusText}`);
  }
  if (response.status === 204) return null;
  return response.json();
};

export const api = {
  // Auth
  auth: {
    login: (correo, contrasena) => request('/auth/login', { method: 'POST', body: JSON.stringify({ correo, contrasena }) }),
  },
  // Productos
  productos: {
    getAll: () => request('/productos'),
    getById: (id) => request(`/productos/${id}`),
    create: (data) => request('/productos', { method: 'POST', body: JSON.stringify(data) }),
    update: (id, data) => request(`/productos/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    deactivate: (id) => request(`/productos/${id}/deactivate`, { method: 'PATCH' }),
  },
  // Categorias
  categorias: {
    getAll: () => request('/categorias'),
    getById: (id) => request(`/categorias/${id}`),
    create: (data) => request('/categorias', { method: 'POST', body: JSON.stringify(data) }),
    update: (id, data) => request(`/categorias/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id) => request(`/categorias/${id}`, { method: 'DELETE' }),
  },
  // Usuarios
  usuarios: {
    getAll: () => request('/usuarios'),
    getById: (id) => request(`/usuarios/${id}`),
    create: (data) => request('/usuarios', { method: 'POST', body: JSON.stringify(data) }),
    update: (id, data) => request(`/usuarios/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    deactivate: (id) => request(`/usuarios/${id}/deactivate`, { method: 'PATCH' }),
  },
  // Pedidos
  pedidos: {
    getAll: () => request('/pedidos'),
    getById: (id) => request(`/pedidos/${id}`),
    create: (data) => request('/pedidos', { method: 'POST', body: JSON.stringify(data) }),
    updateEstado: (id, estado) => request(`/pedidos/${id}/estado`, { method: 'PATCH', body: JSON.stringify({ estado }) }),
  }
};

