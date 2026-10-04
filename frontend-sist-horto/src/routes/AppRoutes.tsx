import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from '../components/Layout/Layout';
import PaginaInicio from '../pages/PaginaInicio/PaginaInicio';
import PaginaSecretaria from '../pages/PaginaSecretaria/PaginaSecretaria';
import PaginaHortoFlorestal from '../pages/PaginaHortoFlorestal/PaginaHortoFlorestal';
import PaginaComoSolicitar from '../pages/PaginaComoSolicitar/PaginaComoSolicitar';
import CatalogoMudas from '../pages/CatalogoMudas/CatalogoMudas';
import FormularioMuda from '../pages/FormularioMuda/FormularioMuda';
import { PaginaLogin } from '../pages/PaginasAutenticacao/PaginaLogin/PaginaLogin';
import PaginaPerfil from '../pages/PaginaPerfil/PaginaPerfil';
import { PaginaForbidden } from '../pages/PaginasAutenticacao/PaginaForbidden';
import { PaginaNotFound } from '../pages/PaginasAutenticacao/PaginaNotFound';
import { ProtectedRoute } from './ProtectedRoute';
import ListaBeneficiarios from '../pages/ListaBeneficiarios/ListaBeneficiarios';
import { PaginaRegistro } from '../pages/PaginasAutenticacao/PaginaRegistro/PaginaRegistro';
import FormularioParametroAnual from '../pages/FormularioParametroAnual/FormularioParametroAnual';
import ListaParametrosAnuais from '../pages/ListaParametrosAnuais/ListaParametrosAnuais';
import PaginaSolicitacao from '../pages/PaginaSolicitacao/PaginaSolicitacao';
import DetalhesSolicitacaoAdmin from '../pages/DetalhesSolicitacaoAdmin/DetalhesSolicitacaoAdmin';
import FormularioBeneficiario from '../pages/FormularioBeneficiario/FormularioBeneficiario';
import AtendimentoSolicitacao from '../pages/AtendimentoSolicitacao/AtendimentoSolicitacao';
import ListaSolicitacoesAdmin from '../pages/ListaSolicitacoesAdmin/ListaSolicitacoesAdmin';

export const AppRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>

          {/* Públicas */}
          <Route path="/" element={<PaginaInicio />} />
          <Route path="/secretaria" element={<PaginaSecretaria />} />
          <Route path="/horto-florestal" element={<PaginaHortoFlorestal />} />
          <Route path="/como-solicitar" element={<PaginaComoSolicitar />} />
          <Route path="/catalogo" element={<CatalogoMudas />} />
          <Route path="/login" element={<PaginaLogin />} />
          <Route path="/registro" element={<PaginaRegistro />} />
          <Route path="/forbidden" element={<PaginaForbidden />} />

          {/* Beneficiário e Admin */}
          <Route
            element={
              <ProtectedRoute
                allowedRoles={['BENEFICIARIO', 'ADMINISTRADOR']}
              />
            }
          >
            <Route path="/solicitacao" element={<PaginaSolicitacao />} />
            <Route path="/perfil" element={<PaginaPerfil />} />
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
            <Route path="/beneficiarios/novo" element={<FormularioBeneficiario />} />
            <Route path="/beneficiarios/:id/editar" element={<FormularioBeneficiario />} />
            <Route path="/beneficiarios/:id/solicitacao" element={<AtendimentoSolicitacao />} />
            <Route path="/parametros" element={<ListaParametrosAnuais />} />
            <Route path="/parametros/novo" element={<FormularioParametroAnual />} />
            <Route path="/parametros/:ano/editar" element={<FormularioParametroAnual />} />
            <Route path="/admin/solicitacoes" element={<ListaSolicitacoesAdmin />} />
            <Route path="/admin/solicitacoes/:id" element={<DetalhesSolicitacaoAdmin />} />
          </Route>

          <Route path="*" element={<PaginaNotFound />} />

        </Route>
      </Routes>
    </BrowserRouter>
  );
};
