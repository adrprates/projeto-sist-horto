import { Link } from "react-router-dom";
import { Check } from "lucide-react";
import "./PainelAutenticacao.css";

const VANTAGENS = [
  "Montar sua solicitação de mudas direto pelo catálogo",
  "Ver quanto ainda cabe no limite do ano",
  "Acompanhar o status do pedido até a retirada",
  "Consultar as solicitações dos anos anteriores",
];

function PainelAutenticacao() {
  return (
    <aside className="painel-autenticacao">
      <h2 className="painel-autenticacao-titulo">Com a sua conta você pode</h2>
      <ul className="painel-autenticacao-lista">
        {VANTAGENS.map((vantagem) => (
          <li key={vantagem}>
            <Check size={18} />
            {vantagem}
          </li>
        ))}
      </ul>
      <Link to="/como-solicitar" className="botao-contorno-claro">
        Ver como solicitar
      </Link>
    </aside>
  );
}

export default PainelAutenticacao;
