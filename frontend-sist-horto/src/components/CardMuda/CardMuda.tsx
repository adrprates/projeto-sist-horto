import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Package, Info } from "lucide-react";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import ModalDetalhesMuda from "../ModalDetalhesMuda/ModalDetalhesMuda";
import "./CardMuda.css";

interface CardMudaProps {
  muda: DadosMudaResumo;
  estaNaSolicitacao: boolean;
  aoAlternarSolicitacao: (muda: DadosMudaResumo) => void;
  isAdmin: boolean;
  podeSolicitar: boolean;
  aoAdicionarEstoque?: (muda: DadosMudaResumo, quantidade: number) => void;
  aoRemoverEstoque?: (muda: DadosMudaResumo, quantidade: number) => void;
  aoGerenciar?: (muda: DadosMudaResumo) => void;
}

function CardMuda({
  muda,
  estaNaSolicitacao,
  aoAlternarSolicitacao,
  isAdmin,
  podeSolicitar,
  aoAdicionarEstoque,
  aoRemoverEstoque,
  aoGerenciar,
}: CardMudaProps) {
  const [imagemComErro, setImagemComErro] = useState(false);
  const [modalAberto, setModalAberto] = useState(false);
  const [quantidadeEstoque, setQuantidadeEstoque] = useState<number>(1);
  const nomesParaExibir = muda.nomesPopulares.slice(0, 3).join(", ");
  const navigate = useNavigate();

  return (
    <div className="card">
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
        <h3 className="card-titulo">{nomesParaExibir}</h3>
        <p className="card-familia">Família: {muda.familia}</p>

        <p className="card-estoque">
          <Package size={16} />
          Disponíveis: {muda.estoqueDisponivel}
        </p>

        <div className="card-acoes">
          <button
            type="button"
            className="botao-detalhes"
            onClick={() => setModalAberto(true)}
          >
            <Info size={16} />
            Ver mais detalhes
          </button>

          <button
            type="button"
            className={
              estaNaSolicitacao
                ? "botao-solicitacao botao-solicitacao-ativo"
                : "botao-solicitacao"
            }
            onClick={() => {
              if (!podeSolicitar) {
                navigate("/login");
                return;
              }

              aoAlternarSolicitacao(muda);
            }}
          >
            {estaNaSolicitacao ? "Remover da Solicitação" : "Adicionar à Solicitação"}
          </button>
        </div>

        {isAdmin && (
          <div className="card-acoes-admin">
            <input
              type="number"
              min="1"
              value={quantidadeEstoque}
              onChange={(e) => setQuantidadeEstoque(Number(e.target.value))}
              className="input-admin-quantidade-estoque"
            />
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
        )}
      </div>

      {modalAberto && (
        <ModalDetalhesMuda idMuda={String(muda.id)} aoFechar={() => setModalAberto(false)} />
      )}
    </div>
  );
}

export default CardMuda;