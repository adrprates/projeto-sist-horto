import { useCallback, useEffect, useState, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import {
  MOTIVOS_REJEICAO,
  StatusSolicitacao,
  acaoAdministrativa,
  mensagemPadraoEtapa,
  rotuloStatusSolicitacao,
  transicoesPermitidas,
} from "../../types/StatusSolicitacao";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import { buscarSolicitacaoPorId, atualizarEtapa } from "../../api/solicitacaoAdminService";
import { extrairMensagemErro } from "../../utils/extrairMensagemErro";
import BadgeStatusSolicitacao from "../../components/BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import LinhaDoTempoSolicitacao from "../../components/LinhaDoTempoSolicitacao/LinhaDoTempoSolicitacao";
import ComparativoProposta from "../../components/ComparativoProposta/ComparativoProposta";
import EditorPropostaItens, {
  type LinhaProposta,
} from "../../components/EditorPropostaItens/EditorPropostaItens";
import "./DetalhesSolicitacaoAdmin.css";

const hojeIso = () => new Date().toLocaleDateString("sv-SE");

function linhasIniciais(solicitacao: SolicitacaoAdmin): LinhaProposta[] {
  return solicitacao.itens.map((item) => ({
    mudaId: item.muda.id,
    nome: item.muda.nomesPopulares.slice(0, 2).join(", "),
    categoria: item.muda.categoria,
    quantidadeAtual: item.quantidade,
    quantidade: item.quantidade,
  }));
}

function DetalhesSolicitacaoAdmin() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [solicitacao, setSolicitacao] = useState<SolicitacaoAdmin | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  const [novoStatus, setNovoStatus] = useState<StatusSolicitacao | "">("");
  const [motivo, setMotivo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [dataLimiteRetirada, setDataLimiteRetirada] = useState("");
  const [linhasProposta, setLinhasProposta] = useState<LinhaProposta[]>([]);
  const [enviando, setEnviando] = useState(false);
  const [erroEtapa, setErroEtapa] = useState("");
  const [sucessoEtapa, setSucessoEtapa] = useState("");

  const carregarSolicitacao = useCallback((idSolicitacao: number) => {
    buscarSolicitacaoPorId(idSolicitacao)
      .then(setSolicitacao)
      .catch((err) => setErro(extrairMensagemErro(err, "Não foi possível carregar a solicitação.")))
      .finally(() => setCarregando(false));
  }, []);

  useEffect(() => {
    if (id) {
      carregarSolicitacao(Number(id));
    }
  }, [id, carregarSolicitacao]);

  function handleMudarNovoStatus(status: StatusSolicitacao | "") {
    setNovoStatus(status);
    setMotivo("");
    setDescricao("");
    setDataLimiteRetirada("");
    setErroEtapa("");
    setSucessoEtapa("");

    if (status === StatusSolicitacao.AGUARDANDO_CONFIRMACAO && solicitacao) {
      setLinhasProposta(linhasIniciais(solicitacao));
    }
  }

  async function handleSubmit(evento: FormEvent) {
    evento.preventDefault();
    setErroEtapa("");

    if (!solicitacao || !novoStatus) {
      return;
    }

    if (novoStatus === StatusSolicitacao.REJEITADA && !motivo) {
      setErroEtapa("Selecione o motivo da rejeição.");
      return;
    }

    if (novoStatus === StatusSolicitacao.REJEITADA && motivo === "Outro motivo" && !descricao.trim()) {
      setErroEtapa("Descreva o motivo da rejeição.");
      return;
    }

    if (novoStatus === StatusSolicitacao.PRONTA_PARA_RETIRADA && !dataLimiteRetirada) {
      setErroEtapa("Informe a data limite de retirada.");
      return;
    }

    const itensPropostos = linhasProposta
      .filter((linha) => linha.quantidade > 0)
      .map((linha) => ({ mudaId: linha.mudaId, quantidade: linha.quantidade }));

    if (novoStatus === StatusSolicitacao.AGUARDANDO_CONFIRMACAO) {
      if (itensPropostos.length === 0) {
        setErroEtapa("A proposta precisa ter ao menos uma muda. Para cancelar tudo, rejeite a solicitação.");
        return;
      }

      const semMudancas = linhasProposta.every((linha) => linha.quantidade === linha.quantidadeAtual);
      if (semMudancas) {
        setErroEtapa("Altere ao menos um item para enviar a proposta.");
        return;
      }
    }

    setEnviando(true);

    try {
      const atualizada = await atualizarEtapa(solicitacao.id, {
        status: novoStatus,
        motivo: novoStatus === StatusSolicitacao.REJEITADA ? motivo : undefined,
        descricao: descricao.trim() || undefined,
        dataLimiteRetirada: dataLimiteRetirada || undefined,
        itensPropostos: novoStatus === StatusSolicitacao.AGUARDANDO_CONFIRMACAO ? itensPropostos : undefined,
      });

      setSolicitacao(atualizada);
      setSucessoEtapa(`Etapa registrada: ${rotuloStatusSolicitacao[atualizada.statusAtual]}.`);
      limparFormulario();
    } catch (err) {
      setErroEtapa(extrairMensagemErro(err, "Não foi possível atualizar a etapa."));
    } finally {
      setEnviando(false);
    }
  }

  function limparFormulario() {
    setNovoStatus("");
    setMotivo("");
    setDescricao("");
    setDataLimiteRetirada("");
    setLinhasProposta([]);
  }

  if (carregando) {
    return (
      <div>
        <p className="detalhes-carregando">Carregando solicitação...</p>
      </div>
    );
  }

  if (erro || !solicitacao) {
    return (
      <div>
        <p className="detalhes-erro">{erro || "Solicitação não encontrada."}</p>
      </div>
    );
  }

  const opcoesProximoStatus = transicoesPermitidas[solicitacao.statusAtual];
  const quantidadeTotal = solicitacao.itens.reduce((total, item) => total + item.quantidade, 0);
  const propostaPendente =
    solicitacao.statusAtual === StatusSolicitacao.AGUARDANDO_CONFIRMACAO &&
    (solicitacao.itensPropostos?.length ?? 0) > 0;
  const placeholderMensagem = novoStatus ? mensagemPadraoEtapa[novoStatus] : undefined;

  return (
    <div>
      <CabecalhoPagina
        compacto
        voltar={{ rotulo: "Voltar para a lista", aoClicar: () => navigate("/admin/solicitacoes") }}
        titulo={`Solicitação #${solicitacao.id}`}
        texto={`${solicitacao.beneficiario.nome} — CPF ${solicitacao.beneficiario.cpf}`}
        acoes={<BadgeStatusSolicitacao status={solicitacao.statusAtual} />}
      />

      <section className="container-pagina container-detalhes-solicitacao">
        <section className="detalhes-secao">
          <h3>Informações</h3>
          <div className="detalhes-grade">
            <p>
              <strong>Ano:</strong> {solicitacao.parametroAnual.ano}
            </p>
            <p>
              <strong>Data da solicitação:</strong> {solicitacao.dataSolicitacao}
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

        {propostaPendente && (
          <section className="detalhes-secao detalhes-proposta-pendente">
            <h3>Proposta aguardando o beneficiário</h3>
            <p className="detalhes-proposta-texto">
              Se o beneficiário aceitar, os itens abaixo passam a valer. Você também pode encerrar a
              espera aprovando a solicitação original ou rejeitando-a.
            </p>
            <ComparativoProposta
              itensAtuais={solicitacao.itens}
              itensPropostos={solicitacao.itensPropostos ?? []}
            />
          </section>
        )}

        {solicitacao.etapas && solicitacao.etapas.length > 0 && (
          <section className="detalhes-secao">
            <h3>Histórico da solicitação</h3>
            <LinhaDoTempoSolicitacao etapas={solicitacao.etapas} statusAtual={solicitacao.statusAtual} />
          </section>
        )}

        {sucessoEtapa && <p className="formulario-etapa-sucesso surgir">{sucessoEtapa}</p>}

        {opcoesProximoStatus.length > 0 ? (
          <section className="detalhes-secao">
            <h3>Registrar próxima etapa</h3>

            <form onSubmit={handleSubmit} className="formulario-etapa">
              <div className="opcoes-etapa" role="radiogroup" aria-label="Próxima etapa">
                {opcoesProximoStatus.map((status) => (
                  <button
                    key={status}
                    type="button"
                    role="radio"
                    aria-checked={novoStatus === status}
                    className={
                      novoStatus === status
                        ? `opcao-etapa opcao-etapa-${status.toLowerCase()} opcao-etapa-selecionada`
                        : `opcao-etapa opcao-etapa-${status.toLowerCase()}`
                    }
                    onClick={() => handleMudarNovoStatus(novoStatus === status ? "" : status)}
                  >
                    {acaoAdministrativa[status] ?? rotuloStatusSolicitacao[status]}
                  </button>
                ))}
              </div>

              {novoStatus === StatusSolicitacao.REJEITADA && (
                <div className="campo surgir">
                  <label htmlFor="motivo">Motivo da rejeição *</label>
                  <select id="motivo" value={motivo} onChange={(evento) => setMotivo(evento.target.value)}>
                    <option value="">Selecione um motivo</option>
                    {MOTIVOS_REJEICAO.map((opcao) => (
                      <option key={opcao} value={opcao}>
                        {opcao}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              {novoStatus === StatusSolicitacao.AGUARDANDO_CONFIRMACAO && (
                <div className="campo surgir">
                  <label>Itens propostos *</label>
                  <p className="campo-ajuda">
                    Ajuste as quantidades, remova ou inclua mudas. O beneficiário verá a comparação e
                    decidirá se aceita.
                  </p>
                  <EditorPropostaItens
                    linhas={linhasProposta}
                    aoAlterar={setLinhasProposta}
                    desabilitado={enviando}
                  />
                </div>
              )}

              {novoStatus === StatusSolicitacao.PRONTA_PARA_RETIRADA && (
                <div className="campo surgir">
                  <label htmlFor="data-limite">Data limite de retirada *</label>
                  <input
                    id="data-limite"
                    type="date"
                    min={hojeIso()}
                    value={dataLimiteRetirada}
                    onChange={(evento) => setDataLimiteRetirada(evento.target.value)}
                  />
                </div>
              )}

              {novoStatus && (
                <div className="campo surgir">
                  <label htmlFor="descricao">
                    Mensagem ao beneficiário{" "}
                    {novoStatus === StatusSolicitacao.REJEITADA && motivo === "Outro motivo"
                      ? "*"
                      : "(opcional)"}
                  </label>
                  <textarea
                    id="descricao"
                    rows={3}
                    value={descricao}
                    placeholder={placeholderMensagem}
                    onChange={(evento) => setDescricao(evento.target.value)}
                  />
                  <p className="campo-ajuda">
                    Se deixar em branco, o beneficiário recebe a mensagem padrão que aparece no campo.
                  </p>
                </div>
              )}

              {erroEtapa && <p className="formulario-etapa-erro">{erroEtapa}</p>}

              {novoStatus && (
                <div className="formulario-etapa-acoes">
                  <button type="submit" className="botao-salvar-etapa" disabled={enviando}>
                    {enviando ? "Salvando..." : acaoAdministrativa[novoStatus] ?? "Confirmar"}
                  </button>
                </div>
              )}
            </form>
          </section>
        ) : (
          <p className="detalhes-finalizada">
            Esta solicitação está finalizada e não possui mais etapas disponíveis.
          </p>
        )}
      </section>
    </div>
  );
}

export default DetalhesSolicitacaoAdmin;
