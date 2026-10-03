import { Link } from "react-router-dom";
import type { LucideIcon } from "lucide-react";
import "./PaginaAviso.css";

interface PaginaAvisoProps {
  Icone: LucideIcon;
  codigo: string;
  titulo: string;
  texto: string;
}

function PaginaAviso({ Icone, codigo, titulo, texto }: PaginaAvisoProps) {
  return (
    <section className="container-pagina pagina-aviso">
      <span className="pagina-aviso-icone">
        <Icone size={32} />
      </span>
      <p className="pagina-aviso-codigo">{codigo}</p>
      <h1 className="pagina-aviso-titulo">{titulo}</h1>
      <p className="pagina-aviso-texto">{texto}</p>
      <div className="pagina-aviso-acoes">
        <Link to="/" className="botao-primario">
          Voltar para o início
        </Link>
        <Link to="/catalogo" className="link-sublinhado">
          Ver o catálogo
        </Link>
      </div>
    </section>
  );
}

export default PaginaAviso;
