import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Package, Info, Lock, ClipboardCheck, Leaf } from "lucide-react";
import { isAxiosError } from "axios";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import ModalDetalhesMuda from "../ModalDetalhesMuda/ModalDetalhesMuda";
import SeletorQuantidade from "../SeletorQuantidade/SeletorQuantidade";
import "../CardMudaVitrine/CardMudaVitrine.css";
import "./CardMuda.css";

const MENSAGEM_PADRAO = "Não foi possível adicionar essa muda à solicitação.";

function extrairMensagemErro(erro: unknown): string {
  if (!isAxiosError(erro)) {
    return MENSAGEM_PADRAO;
  }

  const dados = erro.response?.data;

  if (typeof dados === "string" && dados.trim().length > 0) {
    return dados;
  }

  if (dados && typeof dados === "object") {
    const possivelMensagem =
      (dados as Record<string, unknown>).message ??
      (dados as Record<string, unknown>).erro ??
      (dados as Record<string, unknown>).error;

    if (typeof possivelMensagem === "string" && possivelMensagem.trim().length > 0) {
      return possivelMensagem;
    }
  }

  return MENSAGEM_PADRAO;
}

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
}: CardMudaProps) {
  const [imagemComErro, setImagemComErro] = useState(false);
  const [modalAberto, setModalAberto] = useState(false);

  const [quantidadeEstoque, setQuantidadeEstoque] = useState(1);
  const [quantidadeSolicitacao, setQuantidadeSolicitacao] = useState(1);
  const [adicionando, setAdicionando] = useState(false);
  const [erroAdicionar, setErroAdicionar] = useState("");

  const nomesParaExibir = muda.nomesPopulares.slice(0, 3).join(", ");
  const semEstoque = muda.estoqueDisponivel <= 0;
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

  return (
    <article className="card etiqueta-muda">
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
          <div>
            <h3 className="card-titulo">{nomesParaExibir}</h3>
            <p className="card-cientifico">{muda.nomeCientifico}</p>
          </div>
          <span className={semEstoque ? "etiqueta-selo" : "etiqueta-selo etiqueta-selo-disponivel"}>
            {semEstoque ? "Em falta" : "Disponível"}
          </span>
        </div>

        <ul className="etiqueta-muda-rodape">
          <li>
            <Leaf size={16} />
            Família {muda.familia}
          </li>
          <li>
            <Package size={16} />
            {muda.estoqueDisponivel} em estoque
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
              className="botao-solicitacao botao-solicitacao-bloqueado"
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
              className="botao-solicitacao botao-solicitacao-bloqueado"
              onClick={() => navigate("/solicitacao")}
              title="Você já possui uma solicitação enviada este ano"
            >
              <Lock size={14} />
              Solicitação deste ano já enviada
            </button>
          )}

          {podeSolicitar && !solicitacaoBloqueada && (
            <>
              {!semEstoque && (
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
                disabled={semEstoque || adicionando}
              >
                {semEstoque
                  ? "Sem estoque disponível"
                  : adicionando
                  ? "Adicionando..."
                  : "Adicionar à Solicitação"}
              </button>
            </>
          )}
        </div>

        {isAdmin && (
          <div className="card-acoes-admin">
            <p className="card-acoes-admin-titulo">Ações administrativas</p>

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