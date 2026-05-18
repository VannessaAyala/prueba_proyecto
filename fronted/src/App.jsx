import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { CartProvider } from './context/CartContext';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';

// Páginas Públicas
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Cart from './pages/Cart';
import MisPedidos from './pages/MisPedidos';

// Páginas de Administración
import AdminLayout from './pages/admin/AdminLayout';
import Categorias from './pages/admin/Categorias';
import Productos from './pages/admin/Productos';
import Usuarios from './pages/admin/Usuarios';
import Pedidos from './pages/admin/Pedidos';

// Componente para proteger rutas que requieren login
function ProtectedRoute({ children }) {
  const { isLoggedIn } = useAuth();
  if (!isLoggedIn) return <Navigate to="/login" replace />;
  return children;
}

// Componente para proteger rutas que requieren login y solo para usuarios normales (no ADMIN)
function UserRoute({ children }) {
  const { isLoggedIn, isAdmin } = useAuth();
  if (!isLoggedIn) return <Navigate to="/login" replace />;
  if (isAdmin) return <Navigate to="/admin/productos" replace />;
  return children;
}

// Componente para proteger rutas solo para ADMIN
function AdminRoute({ children }) {
  const { isLoggedIn, isAdmin } = useAuth();
  if (!isLoggedIn) return <Navigate to="/login" replace />;
  if (!isAdmin) return <Navigate to="/" replace />;
  return children;
}

function AppRoutes() {
  return (
    <Routes>
      {/* Rutas Públicas */}
      <Route path="/" element={<Home />} />
    <Route path="/register" element={<Register />} />
      <Route path="/login" element={<Login />} />

      {/* Rutas que requieren login (solo para usuarios comunes, no ADMIN) */}
      <Route path="/cart" element={<UserRoute><Cart /></UserRoute>} />
      <Route path="/mis-pedidos" element={<UserRoute><MisPedidos /></UserRoute>} />

      {/* Rutas de Administración (solo ADMIN) */}
      <Route path="/admin" element={<AdminRoute><AdminLayout /></AdminRoute>}>
        <Route index element={<Navigate to="productos" replace />} />
        <Route path="categorias" element={<Categorias />} />
        <Route path="productos" element={<Productos />} />
        <Route path="usuarios" element={<Usuarios />} />
        <Route path="pedidos" element={<Pedidos />} />
      </Route>
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <BrowserRouter>
          <Navbar />
          <AppRoutes />
        </BrowserRouter>
      </CartProvider>
    </AuthProvider>
  );
}

export default App;
