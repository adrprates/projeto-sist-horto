import { BrowserRouter, Routes, Route } from 'react-router-dom';
import CatalogoMudas from '../pages/CatalogoMudas/CatalogoMudas';
import FormularioMuda from '../pages/FormularioMuda/FormularioMuda';
import { PaginaLogin } from '../pages/PaginaLogin';
import { PaginaForbidden } from '../pages/PaginaForbidden';
import { PaginaNotFound } from '../pages/PaginaNotFound';
import { ProtectedRoute } from './ProtectedRoute';

export const AppRoutes = () => {
  return (
    <BrowserRouter>
  <Routes>

    {/* Públicas */}
    <Route path="/" element={<CatalogoMudas />} />
    <Route path="/login" element={<PaginaLogin />} />
    <Route path="/forbidden" element={<PaginaForbidden />} />

    {/* Beneficiário e Admin */}
    <Route
      element={
        <ProtectedRoute
          allowedRoles={['BENEFICIARIO', 'ADMINISTRADOR']}
        />
      }
    >
      {/* <Route path="/solicitacoes" element={<PaginaSolicitacoes />} /> */}
      {/* <Route path="/meu-perfil" element={<PaginaPerfil />} /> */}
    </Route>

    {/* Apenas Admin */}
    <Route
      element={
        <ProtectedRoute
          allowedRoles={['ADMINISTRADOR']}
        />
      }
    >
      <Route path="/mudas/nova" element={<FormularioMuda />} />
      <Route path="/mudas/editar/:id" element={<FormularioMuda />} />
    </Route>

    <Route path="*" element={<PaginaNotFound />} />

  </Routes>
</BrowserRouter>
  );
};