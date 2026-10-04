import { ArrowRight } from "lucide-react";
import type { ItemSolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import "./ComparativoProposta.css";

interface ComparativoPropostaProps {
  itensAtuais: ItemSolicitacaoAdmin[];
  itensPropostos: ItemSolicitacaoAdmin[];
}

interface LinhaComparativo {
  mudaId: number;
  nome: string;
  atual: number;
  proposta: number;
}

function montarLinhas(
  itensAtuais: ItemSolicitacaoAdmin[],
  itensPropostos: ItemSolicitacaoAdmin[]
): LinhaComparativo[] {
  const linhas = new Map<number, LinhaComparativo>();

  for (const item of itensAtuais) {
    linhas.set(item.muda.id, {
      mudaId: item.muda.id,
      nome: item.muda.nomesPopulares.slice(0, 2).join(", "),
      atual: item.quantidade,
      proposta: 0,
    });
  }

  for (const item of itensPropostos) {
    const existente = linhas.get(item.muda.id);
    linhas.set(item.muda.id, {
      mudaId: item.muda.id,
      nome: existente?.nome ?? item.muda.nomesPopulares.slice(0, 2).join(", "),
      atual: existente?.atual ?? 0,
      proposta: item.quantidade,
    });
  }

  return [...linhas.values()];
}

function ComparativoProposta({ itensAtuais, itensPropostos }: ComparativoPropostaProps) {
  const linhas = montarLinhas(itensAtuais, itensPropostos);
  const totalAtual = linhas.reduce((total, linha) => total + linha.atual, 0);
  const totalProposta = linhas.reduce((total, linha) => total + linha.proposta, 0);

  return (
    <div className="comparativo">
      <div className="comparativo-cabecalho">
        <span>Muda</span>
        <span>Atual</span>
        <span />
        <span>Proposta</span>
      </div>

      {linhas.map((linha) => {
        let situacao = "";
        if (linha.atual === 0) situacao = "comparativo-linha-nova";
        else if (linha.proposta === 0) situacao = "comparativo-linha-removida";
        else if (linha.atual !== linha.proposta) situacao = "comparativo-linha-alterada";

        return (
          <div key={linha.mudaId} className={`comparativo-linha ${situacao}`}>
            <span className="comparativo-nome">{linha.nome}</span>
            <span className="comparativo-numero">{linha.atual > 0 ? linha.atual : "—"}</span>
            <ArrowRight size={14} className="comparativo-seta" />
            <span className="comparativo-numero comparativo-numero-proposta">
              {linha.proposta > 0 ? linha.proposta : "Removida"}
            </span>
          </div>
        );
      })}

      <div className="comparativo-linha comparativo-total">
        <span className="comparativo-nome">Total de mudas</span>
        <span className="comparativo-numero">{totalAtual}</span>
        <ArrowRight size={14} className="comparativo-seta" />
        <span className="comparativo-numero comparativo-numero-proposta">{totalProposta}</span>
      </div>
    </div>
  );
}

export default ComparativoProposta;
