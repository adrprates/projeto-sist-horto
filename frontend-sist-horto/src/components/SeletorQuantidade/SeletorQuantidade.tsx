import { Minus, Plus } from "lucide-react";
import "./SeletorQuantidade.css";

interface SeletorQuantidadeProps {
  valor: number;
  aoAlterar: (novoValor: number) => void;
  minimo?: number;
  maximo?: number;
  desabilitado?: boolean;
}

function SeletorQuantidade({
  valor,
  aoAlterar,
  minimo = 1,
  maximo,
  desabilitado = false,
}: SeletorQuantidadeProps) {
  function diminuir() {
    const novoValor = valor - 1;
    if (novoValor >= minimo) {
      aoAlterar(novoValor);
    }
  }

  function aumentar() {
    const novoValor = valor + 1;
    if (maximo === undefined || novoValor <= maximo) {
      aoAlterar(novoValor);
    }
  }

  return (
    <div className="seletor-quantidade">
      <button
        type="button"
        onClick={diminuir}
        disabled={desabilitado || valor <= minimo}
        aria-label="Diminuir quantidade"
      >
        <Minus size={14} />
      </button>

      <span className="seletor-quantidade-valor">{valor}</span>

      <button
        type="button"
        onClick={aumentar}
        disabled={desabilitado || (maximo !== undefined && valor >= maximo)}
        aria-label="Aumentar quantidade"
      >
        <Plus size={14} />
      </button>
    </div>
  );
}

export default SeletorQuantidade;