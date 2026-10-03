import type { ReactNode } from "react";
import { ArrowLeft } from "lucide-react";
import "./CabecalhoPagina.css";

interface CabecalhoPaginaProps {
  titulo: ReactNode;
  voltar?: { rotulo: string; aoClicar: () => void };
  texto?: ReactNode;
  acoes?: ReactNode;
  compacto?: boolean;
}

function CabecalhoPagina({ titulo, voltar, texto, acoes, compacto = false }: CabecalhoPaginaProps) {
  return (
    <section className={compacto ? "cabecalho-pagina cabecalho-pagina-compacto" : "cabecalho-pagina"}>
      <div className="container-pagina cabecalho-pagina-conteudo">
        <div className="cabecalho-pagina-textos">
          {voltar && (
            <button type="button" className="cabecalho-pagina-voltar" onClick={voltar.aoClicar}>
              <ArrowLeft size={16} />
              {voltar.rotulo}
            </button>
          )}
          <h1 className="cabecalho-pagina-titulo">{titulo}</h1>
          {texto && <p className="cabecalho-pagina-texto">{texto}</p>}
        </div>
        {acoes && <div className="cabecalho-pagina-acoes">{acoes}</div>}
      </div>

      <svg
        viewBox="0 0 1440 40"
        preserveAspectRatio="none"
        className="onda-divisoria"
        aria-hidden="true"
      >
        <path fill="currentColor" d="M0 40V22C240 -2 480 -2 720 18s480 20 720 0V40Z" />
      </svg>
    </section>
  );
}

export default CabecalhoPagina;
