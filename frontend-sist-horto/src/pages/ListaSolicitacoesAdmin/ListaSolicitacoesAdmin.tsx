import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import type { SolicitacaoFilter } from "../../types/SolicitacaoFilter";
import { listarSolicitacoes } from "../../api/solicitacaoAdminService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import FiltrosSolicitacoesAdmin from "../../components/FiltrosSolicitacoesAdmin/FiltrosSolicitacoesAdmin";
import BadgeStatusSolicitacao from "../../components/BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import "./ListaSolicitacoesAdmin.css";

function ListaSolicitacoesAdmin() {
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoAdmin[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [filtro, setFiltro] = useState<SolicitacaoFilter>({});
  const navigate = useNavigate();

  useEffect(() => {
    setCarregando(true);
    setErro("");

    listarSolicitacoes(filtro)
      .then(setSolicitacoes)
      .catch(() => setErro("Não foi possível carregar as solicitações."))
      .finally(() => setCarregando(false));
  }, [filtro]);

  return (
    <div>
      <Cabecalho />

      <main className="container-solicitacoes-admin">
        <h2 className="titulo-solicitacoes-admin">Gerenciar Solicitações</h2>

        <FiltrosSolicitacoesAdmin filtro={filtro} aoMudarFiltro={setFiltro} />

        {erro && <p className="solicitacoes-admin-erro">{erro}</p>}

        {carregando && <p className="mensagem-central">Carregando solicitações...</p>}

        {!carregando && solicitacoes.length === 0 && (
          <p className="mensagem-central">Nenhuma solicitação encontrada com esses filtros.</p>
        )}

        {!carregando && solicitacoes.length > 0 && (
          <div className="tabela-solicitacoes-wrapper">
            <table className="tabela-solicitacoes">
              <thead>
                <tr>
                  <th>Beneficiário</th>
                  <th>CPF</th>
                  <th>Ano</th>
                  <th>Data</th>
                  <th>Status</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {solicitacoes.map((solicitacao) => (
                  <tr key={solicitacao.id}>
                    <td>{solicitacao.beneficiario.nome}</td>
                    <td>{solicitacao.beneficiario.cpf}</td>
                    <td>{solicitacao.parametroAnual.ano}</td>
                    <td>{solicitacao.dataSolicitacao}</td>
                    <td>
                      <BadgeStatusSolicitacao status={solicitacao.statusAtual} />
                    </td>
                    <td>
                      <button
                        type="button"
                        className="botao-gerenciar-solicitacao"
                        onClick={() => navigate(`/admin/solicitacoes/${solicitacao.id}`)}
                      >
                        Gerenciar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>

      <Rodape />
    </div>
  );
}

export default ListaSolicitacoesAdmin;