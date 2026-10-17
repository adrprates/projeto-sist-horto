import type { ReactNode } from "react";
import "./Paginacao.css";

interface BarraPaginacaoProps {
  pagina: number;
  tamanho: number;
  totalElementos: number;
  rotuloItens: string;
  opcoesTamanho?: number[];
  aoMudarTamanho: (tamanho: number) => void;
  children?: ReactNode;
}

function BarraPaginacao({
  pagina,
  tamanho,
  totalElementos,
  rotuloItens,
  opcoesTamanho = [10, 20, 50],
  aoMudarTamanho,
  children,
}: BarraPaginacaoProps) {
  const inicio = totalElementos === 0 ? 0 : pagina * tamanho + 1;
  const fim = Math.min((pagina + 1) * tamanho, totalElementos);

  return (
    <div className="barra-paginacao">
      <p className="paginacao-resumo">
        Mostrando <strong>{inicio}</strong>–<strong>{fim}</strong> de <strong>{totalElementos}</strong>{" "}
        {rotuloItens}
      </p>

      <div className="barra-paginacao-controles">
        {children}

        <label className="paginacao-tamanho">
          Por página
          <select value={tamanho} onChange={(evento) => aoMudarTamanho(Number(evento.target.value))}>
            {opcoesTamanho.map((opcao) => (
              <option key={opcao} value={opcao}>
                {opcao}
              </option>
            ))}
          </select>
        </label>
      </div>
    </div>
  );
}

export default BarraPaginacao;
