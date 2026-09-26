import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import BadgeStatusSolicitacao from "../BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import "./DetalhesSolicitacaoCartao.css";

interface DetalhesSolicitacaoCartaoProps {
  solicitacao: SolicitacaoAdmin;
}

function DetalhesSolicitacaoCartao({ solicitacao }: DetalhesSolicitacaoCartaoProps) {
  const quantidadeTotal = solicitacao.itens.reduce((total, item) => total + item.quantidade, 0);

  return (
    <div className="cartao-detalhes-solicitacao">
      <div className="cartao-detalhes-topo">
        <div>
          <h3 className="cartao-detalhes-titulo">
            Solicitação #{solicitacao.id} — {solicitacao.parametroAnual.ano}
          </h3>
          <p className="cartao-detalhes-data">
            Enviada em {solicitacao.dataSolicitacao}
          </p>
        </div>
        <BadgeStatusSolicitacao status={solicitacao.statusAtual} />
      </div>

      <div className="cartao-detalhes-itens">
        {solicitacao.itens.map((item) => (
          <div key={item.id} className="cartao-detalhes-item">
            <span className="cartao-detalhes-item-nome">
              {item.muda.nomesPopulares.slice(0, 3).join(", ")}
            </span>
            <span className="tag">{rotuloCategoria[item.muda.categoria]}</span>
            <span className="cartao-detalhes-item-quantidade">{item.quantidade} un.</span>
          </div>
        ))}
        <div className="cartao-detalhes-total">
          <span>Total de mudas</span>
          <strong>{quantidadeTotal}</strong>
        </div>
      </div>

      {solicitacao.etapas && solicitacao.etapas.length > 0 && (
        <div className="cartao-detalhes-timeline">
          <p className="cartao-detalhes-timeline-titulo">Histórico</p>
          {solicitacao.etapas.map((etapa) => (
            <div key={etapa.id} className="etapa-timeline">
              <div className="etapa-timeline-topo">
                <BadgeStatusSolicitacao status={etapa.status} />
                <span className="etapa-timeline-data">
                  {etapa.dataHora}
                </span>
              </div>
              <p className="etapa-timeline-descricao">{etapa.descricao}</p>
              {etapa.dataLimiteRetirada && (
                <p className="etapa-timeline-prazo">
                  Prazo de retirada: {etapa.dataLimiteRetirada}
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default DetalhesSolicitacaoCartao;