import { useState } from "react";
import { Package, Tag } from "lucide-react";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import "./CardMudaVitrine.css";

interface CardMudaVitrineProps {
  muda: DadosMudaResumo;
}

function CardMudaVitrine({ muda }: CardMudaVitrineProps) {
  const [imagemComErro, setImagemComErro] = useState(false);
  const nome = muda.nomesPopulares.slice(0, 2).join(", ");
  const disponivel = muda.disponivel && muda.estoqueDisponivel > 0;
  const unidades =
    muda.estoqueDisponivel === 1 ? "1 unidade disponível" : `${muda.estoqueDisponivel} unidades disponíveis`;

  return (
    <article className="etiqueta-muda">
      <span className="etiqueta-muda-furo" aria-hidden="true" />

      <div className="etiqueta-muda-imagem">
        {imagemComErro || !muda.linkImagemArvore ? (
          <span role="img" aria-label={nome}>🌱</span>
        ) : (
          <img src={muda.linkImagemArvore} alt={nome} onError={() => setImagemComErro(true)} />
        )}
      </div>

      <div className="etiqueta-muda-topo">
        <h3 className="etiqueta-muda-nome">{nome}</h3>
        <span className={disponivel ? "etiqueta-selo etiqueta-selo-disponivel" : "etiqueta-selo"}>
          {disponivel ? "Disponível" : muda.disponivel ? "Esgotada" : "Indisponível"}
        </span>
      </div>

      <ul className="etiqueta-muda-rodape">
        <li>
          <Tag size={16} />
          {rotuloCategoria[muda.categoria]}
        </li>
        <li>
          <Package size={16} />
          {muda.disponivel ? unidades : "Fora de distribuição"}
        </li>
      </ul>
    </article>
  );
}

export default CardMudaVitrine;
