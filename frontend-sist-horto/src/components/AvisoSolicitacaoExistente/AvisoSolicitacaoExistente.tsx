import { useNavigate } from "react-router-dom";
import { ClipboardCheck } from "lucide-react";
import type { SolicitacaoBeneficiarioResumo } from "../../types/SolicitacaoBeneficiarioResumo";
import BadgeStatusSolicitacao from "../BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import "./AvisoSolicitacaoExistente.css";

interface AvisoSolicitacaoExistenteProps {
  solicitacao: SolicitacaoBeneficiarioResumo;
}

function AvisoSolicitacaoExistente({ solicitacao }: AvisoSolicitacaoExistenteProps) {
  const navigate = useNavigate();

  return (
    <div className="aviso-solicitacao-existente">
      <ClipboardCheck size={20} className="aviso-solicitacao-existente-icone" />

      <div className="aviso-solicitacao-existente-texto">
        <p className="aviso-solicitacao-existente-titulo">
          Você já possui uma solicitação em andamento para o ano de {solicitacao.ano}.
        </p>
        <div className="aviso-solicitacao-existente-resumo">
          <span>
            Enviada em {new Date(solicitacao.dataSolicitacao).toLocaleDateString("pt-BR")}
          </span>
          <BadgeStatusSolicitacao status={solicitacao.statusAtual} />
        </div>
      </div>

      <button
        type="button"
        className="aviso-solicitacao-existente-botao"
        onClick={() => navigate("/solicitacao")}
      >
        Ver detalhes
      </button>
    </div>
  );
}

export default AvisoSolicitacaoExistente;