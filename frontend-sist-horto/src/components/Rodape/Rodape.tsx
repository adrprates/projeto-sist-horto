import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import { Clock, Mail, MapPin, Phone } from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { SITE } from "../../config/site";
import "./Rodape.css";

function LinhaContato({ Icone, children }: { Icone: LucideIcon; children: ReactNode }) {
  return (
    <li className="rodape-contato">
      <Icone size={16} className="rodape-contato-icone" />
      <span>{children}</span>
    </li>
  );
}

function Rodape() {
  return (
    <footer className="rodape">
      <div className="container-pagina rodape-grade">
        <div>
          <p className="rodape-marca">{SITE.nome}</p>
          <p className="rodape-descricao">
            Programa de doação de mudas da {SITE.secretaria.nome}.
          </p>
          <ul className="rodape-lista">
            <li><Link to="/catalogo">Catálogo de mudas</Link></li>
            <li><Link to="/como-solicitar">Como solicitar</Link></li>
            <li><Link to="/secretaria">Onde fica a Secretaria</Link></li>
            <li><Link to="/horto-florestal">Onde fica o Horto</Link></li>
          </ul>
        </div>

        <div>
          <h2 className="rodape-titulo">{SITE.horto.nome}</h2>
          <ul className="rodape-lista">
            <LinhaContato Icone={MapPin}>{SITE.horto.endereco}</LinhaContato>
            <LinhaContato Icone={Clock}>{SITE.horto.horario}</LinhaContato>
            <LinhaContato Icone={Phone}>{SITE.horto.telefone}</LinhaContato>
          </ul>
        </div>

        <div>
          <h2 className="rodape-titulo">{SITE.secretaria.nome}</h2>
          <ul className="rodape-lista">
            <LinhaContato Icone={MapPin}>{SITE.secretaria.endereco}</LinhaContato>
            <LinhaContato Icone={Clock}>{SITE.secretaria.horario}</LinhaContato>
            <LinhaContato Icone={Mail}>{SITE.secretaria.email}</LinhaContato>
          </ul>
        </div>
      </div>

      <div className="rodape-base">
        <p className="container-pagina">
          © {new Date().getFullYear()} {SITE.prefeitura.nome} — {SITE.secretaria.nome}
        </p>
      </div>
    </footer>
  );
}

export default Rodape;
