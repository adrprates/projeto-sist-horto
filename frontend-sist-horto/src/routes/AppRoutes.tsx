import { BrowserRouter, Routes, Route } from 'react-router-dom';
import CatalogoMudas from '../pages/CatalogoMudas/CatalogoMudas';
import FormularioMuda from '../pages/FormularioMuda/FormularioMuda';
import { PaginaLogin } from '../pages/PaginasAutenticacao/PaginaLogin/PaginaLogin';
import PaginaPerfil from '../pages/PaginaPerfil/PaginaPerfil';
import { PaginaForbidden } from '../pages/PaginasAutenticacao/PaginaForbidden';
import { PaginaNotFound } from '../pages/PaginasAutenticacao/PaginaNotFound';
import { ProtectedRoute } from './ProtectedRoute';
import ListaBeneficiarios from '../pages/ListaBeneficiarios/ListaBeneficiarios';
import { PaginaRegistro } from '../pages/PaginasAutenticacao/PaginaRegistro/PaginaRegistro';

export const AppRoutes = () => {
  return (
    <BrowserRouter>
  <Routes>

    {/* Públicas */}
    <Route path="/" element={<CatalogoMudas />} />
    <Route path="/login" element={<PaginaLogin />} />
    <Route path = "/registro" element = {<PaginaRegistro />} />
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
      {<Route path="/perfil" element={<PaginaPerfil/>} />}
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
      <Route path="/beneficiarios" element={<ListaBeneficiarios />} />
    </Route>

    <Route path="*" element={<PaginaNotFound />} />

  </Routes>
</BrowserRouter>
  );
};