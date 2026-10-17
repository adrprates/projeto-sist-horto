import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import type { SolicitacaoFilter } from "../../types/SolicitacaoFilter";
import { listarSolicitacoes, type OrdemSolicitacoes } from "../../api/solicitacaoAdminService";
import { useValorAtrasado } from "../../hooks/useValorAtrasado";
import { formatarCpfExibicao } from "../../utils/mascaras";
import FiltrosSolicitacoesAdmin from "../../components/FiltrosSolicitacoesAdmin/FiltrosSolicitacoesAdmin";
import BadgeStatusSolicitacao from "../../components/BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import BarraPaginacao from "../../components/Paginacao/BarraPaginacao";
import Paginacao from "../../components/Paginacao/Paginacao";
import "./ListaSolicitacoesAdmin.css";

function ultimaAtualizacao(solicitacao: SolicitacaoAdmin): string {
  return (
    solicitacao.dataAtualizacao ??
    solicitacao.etapas?.[solicitacao.etapas.length - 1]?.dataHora ??
    solicitacao.dataSolicitacao
  );
}

function ListaSolicitacoesAdmin() {
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoAdmin[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [filtro, setFiltro] = useState<SolicitacaoFilter>({});
  const filtroAtrasado = useValorAtrasado(filtro);
  const [ordem, setOrdem] = useState<OrdemSolicitacoes>("prioridade");
  const [pagina, setPagina] = useState(0);
  const [tamanho, setTamanho] = useState(10);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);
  const [versaoLista, setVersaoLista] = useState(0);
  const topoLista = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  useEffect(() => {
    setCarregando(true);
    setErro("");

    listarSolicitacoes(filtroAtrasado, pagina, tamanho, ordem)
      .then((resultado) => {
        setSolicitacoes(resultado.conteudo);
        setTotalPaginas(resultado.totalPaginas);
        setTotalElementos(resultado.totalElementos);
        setVersaoLista((versao) => versao + 1);
      })
      .catch(() => setErro("Não foi possível carregar as solicitações."))
      .finally(() => setCarregando(false));
  }, [filtroAtrasado, pagina, tamanho, ordem]);

  function handleMudarFiltro(novoFiltro: SolicitacaoFilter) {
    setFiltro(novoFiltro);
    setPagina(0);
  }

  function handleMudarPagina(novaPagina: number) {
    setPagina(novaPagina);
    topoLista.current?.scrollIntoView({ behavior: "smooth", block: "start" });
  }

  return (
    <div>
      <CabecalhoPagina
        compacto
        titulo="Gerenciar solicitações"
        texto="Analise, aprove e acompanhe os pedidos de mudas dos beneficiários."
      />

      <section className="container-pagina container-solicitacoes-admin">
        <FiltrosSolicitacoesAdmin filtro={filtro} aoMudarFiltro={handleMudarFiltro} />

        <div ref={topoLista}>
          <BarraPaginacao
            pagina={pagina}
            tamanho={tamanho}
            totalElementos={totalElementos}
            rotuloItens="solicitações"
            aoMudarTamanho={(novoTamanho) => {
              setTamanho(novoTamanho);
              setPagina(0);
            }}
          >
            <label className="solicitacoes-admin-ordem">
              Ordenar por
              <select
                value={ordem}
                onChange={(evento) => {
                  setOrdem(evento.target.value as OrdemSolicitacoes);
                  setPagina(0);
                }}
              >
                <option value="prioridade">Prioridade de atendimento</option>
                <option value="antigas">Mais antigas primeiro</option>
                <option value="recentes">Mais recentes primeiro</option>
              </select>
            </label>
          </BarraPaginacao>
        </div>

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
                  <th>Enviada em</th>
                  <th>Última atualização</th>
                  <th>Status</th>
                  <th className="tabela-coluna-acoes">Ações</th>
                </tr>
              </thead>
              <tbody key={versaoLista} className="lista-animada">
                {solicitacoes.map((solicitacao) => (
                  <tr key={solicitacao.id}>
                    <td>{solicitacao.beneficiario.nome}</td>
                    <td>{formatarCpfExibicao(solicitacao.beneficiario.cpf)}</td>
                    <td>{solicitacao.parametroAnual.ano}</td>
                    <td>{solicitacao.dataSolicitacao}</td>
                    <td>{ultimaAtualizacao(solicitacao)}</td>
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

        <Paginacao pagina={pagina} totalPaginas={totalPaginas} aoMudarPagina={handleMudarPagina} />
      </section>
    </div>
  );
}

export default ListaSolicitacoesAdmin;
