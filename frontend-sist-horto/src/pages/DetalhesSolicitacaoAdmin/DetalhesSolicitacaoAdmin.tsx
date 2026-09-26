import { useEffect, useState, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import {
  StatusSolicitacao,
  rotuloStatusSolicitacao,
  transicoesPermitidas,
  statusExigeDescricao,
  statusExigeDataLimite,
} from "../../types/StatusSolicitacao";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import { buscarSolicitacaoPorId, atualizarEtapa } from "../../api/solicitacaoAdminService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import BadgeStatusSolicitacao from "../../components/BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import "./DetalhesSolicitacaoAdmin.css";

function DetalhesSolicitacaoAdmin() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [solicitacao, setSolicitacao] = useState<SolicitacaoAdmin | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  const [novoStatus, setNovoStatus] = useState<StatusSolicitacao | "">("");
  const [descricao, setDescricao] = useState("");
  const [dataLimiteRetirada, setDataLimiteRetirada] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [erroEtapa, setErroEtapa] = useState("");

  useEffect(() => {
    if (!id) {
      return;
    }
    carregarSolicitacao(Number(id));
  }, [id]);

  function extrairMensagemErro(err: any): string {
    if (err?.response?.data) {
      const data = err.response.data;
      if (typeof data === "string") return data;
      return data.message || data.erro || data.error || "Erro ao processar solicitação.";
    }

    if (err?.request && !err?.response) {
      return "Servidor indisponível. Verifique a conexão.";
    }

    return err?.message || "Ocorreu um erro inesperado.";
  }

  function carregarSolicitacao(idSolicitacao: number) {
    setCarregando(true);
    setErro("");

    buscarSolicitacaoPorId(idSolicitacao)
      .then(setSolicitacao)
      .catch((err) => setErro(extrairMensagemErro(err)))
      .finally(() => setCarregando(false));
  }

  function handleMudarNovoStatus(status: StatusSolicitacao | "") {
    setNovoStatus(status);
    setDescricao("");
    setDataLimiteRetirada("");
    setErroEtapa("");
  }

  async function handleSubmit(evento: FormEvent) {
    evento.preventDefault();
    setErroEtapa("");

    if (!solicitacao || !novoStatus) {
      return;
    }

    if (statusExigeDescricao.includes(novoStatus) && !descricao.trim()) {
      setErroEtapa("Informe uma descrição para essa mudança de status.");
      return;
    }

    if (statusExigeDataLimite.includes(novoStatus) && !dataLimiteRetirada) {
      setErroEtapa("Informe a data limite de retirada.");
      return;
    }

    setEnviando(true);

    try {
      const solicitacaoAtualizada = await atualizarEtapa(solicitacao.id, {
        status: novoStatus,
        descricao: descricao.trim() || undefined,
        dataLimiteRetirada: dataLimiteRetirada || undefined,
      });

      setSolicitacao(solicitacaoAtualizada);
      setNovoStatus("");
      setDescricao("");
      setDataLimiteRetirada("");
    } catch (err) {
      setErroEtapa(extrairMensagemErro(err));
    } finally {
      setEnviando(false);
    }
  }

  if (carregando) {
    return (
      <div>
        <Cabecalho />
        <p className="detalhes-carregando">Carregando solicitação...</p>
        <Rodape />
      </div>
    );
  }

  if (erro || !solicitacao) {
    return (
      <div>
        <Cabecalho />
        <p className="detalhes-erro">{erro || "Solicitação não encontrada."}</p>
        <Rodape />
      </div>
    );
  }

  const opcoesProximoStatus = transicoesPermitidas[solicitacao.statusAtual];
  const quantidadeTotal = solicitacao.itens.reduce((total, item) => total + item.quantidade, 0);

  return (
    <div>
      <Cabecalho />

      <main className="container-detalhes-solicitacao">
        <button
          type="button"
          className="botao-voltar"
          onClick={() => navigate("/admin/solicitacoes")}
        >
          <ArrowLeft size={18} />
          Voltar para a lista
        </button>

        <div className="detalhes-cabecalho">
          <div>
            <h2 className="detalhes-titulo">Solicitação #{solicitacao.id}</h2>
            <p className="detalhes-subtitulo">
              {solicitacao.beneficiario.nome} — CPF {solicitacao.beneficiario.cpf}
            </p>
          </div>
          <BadgeStatusSolicitacao status={solicitacao.statusAtual} />
        </div>

        <section className="detalhes-secao">
          <h3>Informações</h3>
          <div className="detalhes-grade">
            <p>
              <strong>Ano:</strong> {solicitacao.parametroAnual.ano}
            </p>
            <p>
              <strong>Data da solicitação:</strong>{" "}
              {solicitacao.dataSolicitacao}
            </p>
            {solicitacao.beneficiario.email && (
              <p>
                <strong>E-mail:</strong> {solicitacao.beneficiario.email}
              </p>
            )}
            {solicitacao.beneficiario.celular && (
              <p>
                <strong>Celular:</strong> {solicitacao.beneficiario.celular}
              </p>
            )}
          </div>
        </section>

        <section className="detalhes-secao">
          <h3>Itens da solicitação</h3>
          <div className="lista-itens-admin">
            {solicitacao.itens.map((item) => (
              <div key={item.id} className="item-admin">
                <span className="item-admin-nome">
                  {item.muda.nomesPopulares.slice(0, 3).join(", ")}
                </span>
                <span className="tag">{rotuloCategoria[item.muda.categoria]}</span>
                <span className="item-admin-quantidade">{item.quantidade} un.</span>
              </div>
            ))}
            <div className="item-admin-total">
              <span>Total de mudas</span>
              <strong>{quantidadeTotal}</strong>
            </div>
          </div>
        </section>

        {solicitacao.etapas && solicitacao.etapas.length > 0 && (
          <section className="detalhes-secao">
            <h3>Histórico</h3>
            <div className="linha-do-tempo">
              {solicitacao.etapas.map((etapa) => (
                <div key={etapa.id} className="etapa-timeline">
                  <div className="etapa-timeline-topo">
                    <BadgeStatusSolicitacao status={etapa.status} />
                    <span className="etapa-timeline-data">
                      {etapa.dataHora}
                    </span>
                  </div>
                  <p className="etapa-timeline-descricao">{etapa.descricao}</p>
                  {etapa.dataLimiteRetirada && (
                    <p className="etapa-timeline-prazo">
                      Prazo de retirada:{" "}
                      {(etapa.dataLimiteRetirada)}
                    </p>
                  )}
                </div>
              ))}
            </div>
          </section>
        )}

        {opcoesProximoStatus.length > 0 ? (
          <section className="detalhes-secao">
            <h3>Atualizar status</h3>

            <form onSubmit={handleSubmit} className="formulario-etapa">
              <div className="campo">
                <label>Novo status</label>
                <select
                  value={novoStatus}
                  onChange={(evento) =>
                    handleMudarNovoStatus(evento.target.value as StatusSolicitacao | "")
                  }
                >
                  <option value="">Selecione o novo status</option>
                  {opcoesProximoStatus.map((status) => (
                    <option key={status} value={status}>
                      {rotuloStatusSolicitacao[status]}
                    </option>
                  ))}
                </select>
              </div>

              {novoStatus && statusExigeDataLimite.includes(novoStatus) && (
                <div className="campo">
                  <label>Data limite de retirada *</label>
                  <input
                    type="date"
                    value={dataLimiteRetirada}
                    onChange={(evento) => setDataLimiteRetirada(evento.target.value)}
                  />
                </div>
              )}

              {novoStatus && (
                <div className="campo">
                  <label>
                    Descrição{" "}
                    {statusExigeDescricao.includes(novoStatus) ? "*" : "(opcional)"}
                  </label>
                  <textarea
                    rows={3}
                    value={descricao}
                    placeholder={
                      novoStatus === StatusSolicitacao.REJEITADA
                        ? "Explique o motivo da rejeição"
                        : novoStatus === StatusSolicitacao.AGUARDANDO_CONFIRMACAO
                        ? "Explique a alteração proposta ao beneficiário"
                        : "Mensagem opcional para o beneficiário"
                    }
                    onChange={(evento) => setDescricao(evento.target.value)}
                  />
                </div>
              )}

              {erroEtapa && <p className="formulario-etapa-erro">{erroEtapa}</p>}

              {novoStatus && (
                <div className="formulario-etapa-acoes">
                  <button type="submit" className="botao-salvar-etapa" disabled={enviando}>
                    {enviando ? "Salvando..." : "Confirmar atualização"}
                  </button>
                </div>
              )}
            </form>
          </section>
        ) : (
          <p className="detalhes-finalizada">
            Esta solicitação está finalizada e não possui mais transições disponíveis.
          </p>
        )}
      </main>

      <Rodape />
    </div>
  );
}

export default DetalhesSolicitacaoAdmin;