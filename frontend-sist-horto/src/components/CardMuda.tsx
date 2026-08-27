import { useState } from "react";
import type { DadosMuda } from "../types/DadosMuda";

interface CardMudaProps {
  muda: DadosMuda;
  aoVerDetalhes?: (muda: DadosMuda) => void;
}

function CardMuda({ muda, aoVerDetalhes }: CardMudaProps) {
  const [imagemComErro, setImagemComErro] = useState(false);

  return (
    <div className="card">
      <div className="card-imagem">
        {imagemComErro ? (
          <div className="card-imagem-vazia" role="img" aria-label={muda.nomePopular}>
            🌱
          </div>
        ) : (
          <img
            src={muda.urlImagem}
            alt={muda.nomePopular}
            onError={() => setImagemComErro(true)}
          />
        )}
      </div>

      <div className="card-conteudo flex coluna">
        <span className="tag alinhar-topo margem-baixo-2">
          {muda.familia}
        </span>

        <h3 className="card-titulo margem-baixo-1">{muda.nomePopular}</h3>
        <p className="card-nome-cientifico italico cinza margem-baixo-4">
          {muda.nomeCanonico}
        </p>

        <button
          type="button"
          className="botao margem-topo-auto"
          onClick={() => aoVerDetalhes?.(muda)}
        >
          Ver detalhes
        </button>
      </div>
    </div>
  );
}

export default CardMuda;