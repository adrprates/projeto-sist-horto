import { ChevronLeft, ChevronRight } from "lucide-react";
import "./Paginacao.css";

interface PaginacaoProps {
  pagina: number;
  totalPaginas: number;
  aoMudarPagina: (pagina: number) => void;
}

function paginasVisiveis(atual: number, total: number): (number | "...")[] {
  if (total <= 7) {
    return Array.from({ length: total }, (_, indice) => indice);
  }

  const paginas = new Set([0, total - 1, atual - 1, atual, atual + 1]);
  const ordenadas = [...paginas].filter((pagina) => pagina >= 0 && pagina < total).sort((a, b) => a - b);

  const resultado: (number | "...")[] = [];
  ordenadas.forEach((pagina, indice) => {
    if (indice > 0 && pagina - ordenadas[indice - 1] > 1) {
      resultado.push("...");
    }
    resultado.push(pagina);
  });

  return resultado;
}

function Paginacao({ pagina, totalPaginas, aoMudarPagina }: PaginacaoProps) {
  if (totalPaginas <= 1) {
    return null;
  }

  return (
    <nav className="paginacao" aria-label="Paginação">
      <button
        type="button"
        className="paginacao-botao"
        onClick={() => aoMudarPagina(pagina - 1)}
        disabled={pagina === 0}
        aria-label="Página anterior"
      >
        <ChevronLeft size={16} />
      </button>

      {paginasVisiveis(pagina, totalPaginas).map((item, indice) =>
        item === "..." ? (
          <span key={`reticencias-${indice}`} className="paginacao-reticencias">
            …
          </span>
        ) : (
          <button
            key={item}
            type="button"
            className={item === pagina ? "paginacao-botao paginacao-botao-atual" : "paginacao-botao"}
            onClick={() => aoMudarPagina(item)}
            aria-current={item === pagina ? "page" : undefined}
          >
            {item + 1}
          </button>
        )
      )}

      <button
        type="button"
        className="paginacao-botao"
        onClick={() => aoMudarPagina(pagina + 1)}
        disabled={pagina >= totalPaginas - 1}
        aria-label="Próxima página"
      >
        <ChevronRight size={16} />
      </button>
    </nav>
  );
}

export default Paginacao;
