import { Clock, ExternalLink, MapPin, Phone } from "lucide-react";
import type { DadosLocal } from "../../config/site";
import "./BlocoLocalizacao.css";

interface BlocoLocalizacaoProps {
  local: DadosLocal;
}

function BlocoLocalizacao({ local }: BlocoLocalizacaoProps) {
  const busca = encodeURIComponent(local.mapaBusca);

  return (
    <div className="bloco-localizacao">
      <div className="bloco-localizacao-mapa">
        <iframe
          title={`Mapa: ${local.nome}`}
          loading="lazy"
          src={`https://www.google.com/maps?q=${busca}&output=embed`}
        />
      </div>

      <div className="bloco-localizacao-info">
        <h3 className="bloco-localizacao-titulo">{local.nome}</h3>
        <ul className="bloco-localizacao-lista">
          <li>
            <MapPin size={20} />
            <span>
              {local.endereco}
              {local.cep ? ` — CEP ${local.cep}` : ""}, Patrocínio/MG
            </span>
          </li>
          <li>
            <Clock size={20} />
            <span>{local.horario}</span>
          </li>
          <li>
            <Phone size={20} />
            <span>{local.telefone}</span>
          </li>
        </ul>
        <a
          href={`https://www.google.com/maps/search/?api=1&query=${busca}`}
          target="_blank"
          rel="noopener noreferrer"
          className="link-sublinhado bloco-localizacao-link"
        >
          Abrir no Google Maps <ExternalLink size={16} />
        </a>
      </div>
    </div>
  );
}

export default BlocoLocalizacao;
