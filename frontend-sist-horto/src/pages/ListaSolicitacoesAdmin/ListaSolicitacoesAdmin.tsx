import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import type { SolicitacaoFilter } from "../../types/SolicitacaoFilter";
import { listarSolicitacoes } from "../../api/solicitacaoAdminService";
import { useValorAtrasado } from "../../hooks/useValorAtrasado";
import FiltrosSolicitacoesAdmin from "../../components/FiltrosSolicitacoesAdmin/FiltrosSolicitacoesAdmin";
import BadgeStatusSolicitacao from "../../components/BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import "./ListaSolicitacoesAdmin.css";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";

function ListaSolicitacoesAdmin() {
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoAdmin[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [filtro, setFiltro] = useState<SolicitacaoFilter>({});
  const filtroAtrasado = useValorAtrasado(filtro);
  const [versaoLista, setVersaoLista] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    setCarregando(true);
    setErro("");

    listarSolicitacoes(filtroAtrasado)
      .then((resultado) => {
        setSolicitacoes(resultado);
        setVersaoLista((versao) => versao + 1);
      })
      .catch(() => setErro("Não foi possível carregar as solicitações."))
      .finally(() => setCarregando(false));
  }, [filtroAtrasado]);

  return (
    <div>
      <CabecalhoPagina
        compacto
        titulo="Gerenciar solicitações"
        texto="Analise, aprove e acompanhe os pedidos de mudas dos beneficiários."
      />

      <section className="container-pagina container-solicitacoes-admin">

        <FiltrosSolicitacoesAdmin filtro={filtro} aoMudarFiltro={setFiltro} />

        {erro && <p className="solicitacoes-admin-erro">{erro}</p>}

        {carregando && versaoLista === 0 && (
          <p className="mensagem-central">Carregando solicitações...</p>
        )}

        {!carregando && !erro && solicitacoes.length === 0 && (
          <p className="mensagem-central surgir">Nenhuma solicitação encontrada com esses filtros.</p>
        )}

        {solicitacoes.length > 0 && (
          <div className={`tabela-wrapper conteudo-atualizavel${carregando ? " atualizando" : ""}`}>
            <table className="tabela">
              <thead>
                <tr>
                  <th>Beneficiário</th>
                  <th>CPF</th>
                  <th>Ano</th>
                  <th>Data</th>
                  <th>Status</th>
                  <th className="tabela-coluna-acoes">Ações</th>
                </tr>
              </thead>
              <tbody key={versaoLista} className="lista-animada">
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
                        className="botao-tabela"
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
      </section>

    </div>
  );
}

export default ListaSolicitacoesAdmin;