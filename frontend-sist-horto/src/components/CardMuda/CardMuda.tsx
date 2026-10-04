import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { Package, Info, Lock, ClipboardCheck, Leaf, Ban, CircleCheck } from "lucide-react";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import { extrairMensagemErro } from "../../utils/extrairMensagemErro";
import ModalDetalhesMuda from "../ModalDetalhesMuda/ModalDetalhesMuda";
import SeletorQuantidade from "../SeletorQuantidade/SeletorQuantidade";
import "../CardMudaVitrine/CardMudaVitrine.css";
import "./CardMuda.css";

interface CardMudaProps {
  muda: DadosMudaResumo;
  isAdmin: boolean;
  podeSolicitar: boolean;
  solicitacaoBloqueada?: boolean;
  quantidadeJaSolicitada?: number;
  aoAdicionarSolicitacao?: (muda: DadosMudaResumo, quantidade: number) => Promise<void>;
  aoAdicionarEstoque?: (muda: DadosMudaResumo, quantidade: number) => void;
  aoRemoverEstoque?: (muda: DadosMudaResumo, quantidade: number) => void;
  aoGerenciar?: (muda: DadosMudaResumo) => void;
  aoAlterarDisponibilidade?: (muda: DadosMudaResumo, disponivel: boolean, motivo?: string) => Promise<void>;
}

function textoUnidades(quantidade: number) {
  return quantidade === 1 ? "1 unidade disponível" : `${quantidade} unidades disponíveis`;
}

function CardMuda({
  muda,
  isAdmin,
  podeSolicitar,
  solicitacaoBloqueada = false,
  quantidadeJaSolicitada = 0,
  aoAdicionarSolicitacao,
  aoAdicionarEstoque,
  aoRemoverEstoque,
  aoGerenciar,
  aoAlterarDisponibilidade,
}: CardMudaProps) {
  const [imagemComErro, setImagemComErro] = useState(false);
  const [modalAberto, setModalAberto] = useState(false);

  const [quantidadeEstoque, setQuantidadeEstoque] = useState(1);
  const [quantidadeSolicitacao, setQuantidadeSolicitacao] = useState(1);
  const [adicionando, setAdicionando] = useState(false);
  const [erroAdicionar, setErroAdicionar] = useState("");

  const [informandoMotivo, setInformandoMotivo] = useState(false);
  const [motivo, setMotivo] = useState("");
  const [alterandoDisponibilidade, setAlterandoDisponibilidade] = useState(false);
  const [erroDisponibilidade, setErroDisponibilidade] = useState("");

  const nomesParaExibir = muda.nomesPopulares.slice(0, 3).join(", ");
  const indisponivel = !muda.disponivel;
  const semEstoque = muda.estoqueDisponivel <= 0;
  const podeAdicionar = !indisponivel && !semEstoque;
  const navigate = useNavigate();

  async function handleAdicionarSolicitacao() {
    if (!podeSolicitar) {
      navigate("/login");
      return;
    }

    if (!aoAdicionarSolicitacao) {
      return;
    }

    setErroAdicionar("");
    setAdicionando(true);

    try {
      await aoAdicionarSolicitacao(muda, quantidadeSolicitacao);
      navigate("/solicitacao");
    } catch (erro) {
      setErroAdicionar(extrairMensagemErro(erro));
    } finally {
      setAdicionando(false);
    }
  }

  async function alterarDisponibilidade(disponivel: boolean, motivoInformado?: string) {
    if (!aoAlterarDisponibilidade) {
      return;
    }

    setErroDisponibilidade("");
    setAlterandoDisponibilidade(true);

    try {
      await aoAlterarDisponibilidade(muda, disponivel, motivoInformado);
      setInformandoMotivo(false);
      setMotivo("");
    } catch (erro) {
      setErroDisponibilidade(extrairMensagemErro(erro, "Não foi possível alterar a disponibilidade."));
    } finally {
      setAlterandoDisponibilidade(false);
    }
  }

  function handleConfirmarIndisponibilidade(evento: FormEvent) {
    evento.preventDefault();
    alterarDisponibilidade(false, motivo);
  }

  function rotuloBotaoSolicitacao() {
    if (indisponivel) return "Indisponível no momento";
    if (semEstoque) return "Sem unidades disponíveis";
    if (adicionando) return "Adicionando...";
    return "Adicionar à Solicitação";
  }

  return (
    <article className={indisponivel ? "card etiqueta-muda card-indisponivel" : "card etiqueta-muda"}>
      <span className="etiqueta-muda-furo" aria-hidden="true" />

      <div className="card-imagem">
        {imagemComErro ? (
          <div className="card-imagem-vazia" role="img" aria-label={nomesParaExibir}>
            🌱
          </div>
        ) : (
          <img
            src={muda.linkImagemArvore}
            alt={nomesParaExibir}
            onError={() => setImagemComErro(true)}
          />
        )}
        <span className="tag">{rotuloCategoria[muda.categoria]}</span>
      </div>

      <div className="card-conteudo">
        <div className="card-cabecalho">
          <h3 className="card-titulo">{nomesParaExibir}</h3>
          <span
            className={
              indisponivel
                ? "etiqueta-selo etiqueta-selo-indisponivel"
                : semEstoque
                ? "etiqueta-selo"
                : "etiqueta-selo etiqueta-selo-disponivel"
            }
          >
            {indisponivel ? "Indisponível" : semEstoque ? "Esgotada" : "Disponível"}
          </span>
        </div>

        {indisponivel && muda.motivoIndisponibilidade && (
          <p className="card-motivo-indisponivel">{muda.motivoIndisponibilidade}</p>
        )}

        <ul className="etiqueta-muda-rodape">
          <li>
            <Leaf size={16} />
            Família {muda.familia}
          </li>
          <li>
            <Package size={16} />
            {indisponivel ? "Fora de distribuição" : textoUnidades(muda.estoqueDisponivel)}
          </li>
        </ul>

        {podeSolicitar && !solicitacaoBloqueada && quantidadeJaSolicitada > 0 && (
          <p className="card-ja-solicitado">
            <ClipboardCheck size={14} />
            Você já tem {quantidadeJaSolicitada} na solicitação
          </p>
        )}

        <div className="card-acoes">
          <button type="button" className="botao-detalhes" onClick={() => setModalAberto(true)}>
            <Info size={16} />
            Ver mais detalhes
          </button>

          {!podeSolicitar && (
            <button
              type="button"
              className="botao-solicitacao"
              onClick={() => navigate("/login")}
              title="Faça login para solicitar mudas"
            >
              <Lock size={14} />
              Faça login para solicitar
            </button>
          )}

          {podeSolicitar && solicitacaoBloqueada && (
            <button
              type="button"
              className="botao-solicitacao"
              onClick={() => navigate("/solicitacao")}
              title="Você já possui uma solicitação enviada este ano"
            >
              <Lock size={14} />
              Solicitação deste ano já enviada
            </button>
          )}

          {podeSolicitar && !solicitacaoBloqueada && (
            <>
              {podeAdicionar && (
                <div className="card-quantidade-linha">
                  <span className="card-quantidade-rotulo">Quantidade</span>
                  <SeletorQuantidade
                    valor={quantidadeSolicitacao}
                    aoAlterar={setQuantidadeSolicitacao}
                    minimo={1}
                    maximo={muda.estoqueDisponivel}
                    desabilitado={adicionando}
                  />
                </div>
              )}

              {erroAdicionar && <p className="card-erro">{erroAdicionar}</p>}

              <button
                type="button"
                className="botao-solicitacao"
                onClick={handleAdicionarSolicitacao}
                disabled={!podeAdicionar || adicionando}
              >
                {rotuloBotaoSolicitacao()}
              </button>
            </>
          )}
        </div>

        {isAdmin && (
          <div className="card-acoes-admin">
            <p className="card-acoes-admin-titulo">Ações administrativas</p>

            <p className="card-estoque-detalhe">
              Estoque físico <strong>{muda.estoqueTotal}</strong> · Reservadas{" "}
              <strong>{muda.quantidadeReservada}</strong>
            </p>

            <SeletorQuantidade valor={quantidadeEstoque} aoAlterar={setQuantidadeEstoque} minimo={1} />

            <div className="card-acoes-admin-botoes">
              <button
                type="button"
                className="botao-admin-adicionar"
                onClick={() => aoAdicionarEstoque?.(muda, quantidadeEstoque)}
              >
                + Estoque
              </button>
              <button
                type="button"
                className="botao-admin-remover"
                onClick={() => aoRemoverEstoque?.(muda, quantidadeEstoque)}
              >
                - Estoque
              </button>
              <button
                type="button"
                className="botao-admin-gerenciar"
                onClick={() => aoGerenciar?.(muda)}
              >
                Gerenciar
              </button>
            </div>

            {indisponivel ? (
              <button
                type="button"
                className="botao-disponibilidade botao-disponibilidade-ativar"
                onClick={() => alterarDisponibilidade(true)}
                disabled={alterandoDisponibilidade}
              >
                <CircleCheck size={15} />
                {alterandoDisponibilidade ? "Salvando..." : "Marcar como disponível"}
              </button>
            ) : informandoMotivo ? (
              <form className="card-form-motivo" onSubmit={handleConfirmarIndisponibilidade}>
                <input
                  type="text"
                  placeholder="Motivo (opcional): doença, perda..."
                  value={motivo}
                  maxLength={255}
                  onChange={(evento) => setMotivo(evento.target.value)}
                  autoFocus
                />
                <div className="card-form-motivo-acoes">
                  <button
                    type="button"
                    className="card-form-motivo-cancelar"
                    onClick={() => setInformandoMotivo(false)}
                    disabled={alterandoDisponibilidade}
                  >
                    Cancelar
                  </button>
                  <button
                    type="submit"
                    className="botao-disponibilidade"
                    disabled={alterandoDisponibilidade}
                  >
                    {alterandoDisponibilidade ? "Salvando..." : "Confirmar"}
                  </button>
                </div>
              </form>
            ) : (
              <button
                type="button"
                className="botao-disponibilidade"
                onClick={() => setInformandoMotivo(true)}
              >
                <Ban size={15} />
                Marcar como indisponível
              </button>
            )}

            {erroDisponibilidade && <p className="card-erro">{erroDisponibilidade}</p>}
          </div>
        )}
      </div>

      {modalAberto && (
        <ModalDetalhesMuda idMuda={String(muda.id)} aoFechar={() => setModalAberto(false)} />
      )}
    </article>
  );
}

export default CardMuda;
