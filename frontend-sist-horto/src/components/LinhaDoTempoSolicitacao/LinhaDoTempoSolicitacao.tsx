import type { LucideIcon } from "lucide-react";
import {
  CalendarClock,
  CircleCheck,
  CircleX,
  Clock,
  PackageCheck,
  RefreshCw,
  Send,
  Sprout,
} from "lucide-react";
import type { EtapaSolicitacao } from "../../types/EtapaSolicitacao";
import { StatusSolicitacao, tituloEtapa } from "../../types/StatusSolicitacao";
import "./LinhaDoTempoSolicitacao.css";

interface LinhaDoTempoSolicitacaoProps {
  etapas: EtapaSolicitacao[];
  statusAtual: StatusSolicitacao;
}

const ICONE_POR_STATUS: Record<StatusSolicitacao, LucideIcon> = {
  [StatusSolicitacao.RASCUNHO]: Clock,
  [StatusSolicitacao.PENDENTE]: Send,
  [StatusSolicitacao.APROVADA]: CircleCheck,
  [StatusSolicitacao.REJEITADA]: CircleX,
  [StatusSolicitacao.AGUARDANDO_CONFIRMACAO]: RefreshCw,
  [StatusSolicitacao.PRONTA_PARA_RETIRADA]: PackageCheck,
  [StatusSolicitacao.ENTREGUE]: Sprout,
  [StatusSolicitacao.EXPIRADA]: CalendarClock,
};

const TOM_POR_STATUS: Record<StatusSolicitacao, string> = {
  [StatusSolicitacao.RASCUNHO]: "neutro",
  [StatusSolicitacao.PENDENTE]: "neutro",
  [StatusSolicitacao.APROVADA]: "sucesso",
  [StatusSolicitacao.REJEITADA]: "erro",
  [StatusSolicitacao.AGUARDANDO_CONFIRMACAO]: "atencao",
  [StatusSolicitacao.PRONTA_PARA_RETIRADA]: "sucesso",
  [StatusSolicitacao.ENTREGUE]: "concluido",
  [StatusSolicitacao.EXPIRADA]: "erro",
};

const PASSOS = [
  { rotulo: "Enviada", Icone: Send },
  { rotulo: "Aprovada", Icone: CircleCheck },
  { rotulo: "Pronta para retirada", Icone: PackageCheck },
  { rotulo: "Entregue", Icone: Sprout },
];

function calcularProgresso(etapas: EtapaSolicitacao[], statusAtual: StatusSolicitacao) {
  const passouPor = (status: StatusSolicitacao) => etapas.some((etapa) => etapa.status === status);

  switch (statusAtual) {
    case StatusSolicitacao.PENDENTE:
      return { indice: 0, situacao: "normal" };
    case StatusSolicitacao.AGUARDANDO_CONFIRMACAO:
      return { indice: 1, situacao: "atencao" };
    case StatusSolicitacao.APROVADA:
      return { indice: 1, situacao: "normal" };
    case StatusSolicitacao.PRONTA_PARA_RETIRADA:
      return { indice: 2, situacao: "normal" };
    case StatusSolicitacao.ENTREGUE:
      return { indice: 3, situacao: "normal" };
    case StatusSolicitacao.EXPIRADA:
      return { indice: 2, situacao: "encerrada" };
    case StatusSolicitacao.REJEITADA:
      return { indice: passouPor(StatusSolicitacao.APROVADA) ? 1 : 0, situacao: "encerrada" };
    default:
      return { indice: -1, situacao: "normal" };
  }
}

function LinhaDoTempoSolicitacao({ etapas, statusAtual }: LinhaDoTempoSolicitacaoProps) {
  const { indice, situacao } = calcularProgresso(etapas, statusAtual);
  const etapasRecentesPrimeiro = [...etapas].reverse();

  const passosExibidos =
    situacao === "encerrada"
      ? [
          ...PASSOS.slice(0, indice + 1),
          {
            rotulo: statusAtual === StatusSolicitacao.EXPIRADA ? "Expirada" : "Rejeitada",
            Icone: statusAtual === StatusSolicitacao.EXPIRADA ? CalendarClock : CircleX,
          },
        ]
      : PASSOS;

  return (
    <div className="linha-tempo">
      <ol className="linha-tempo-progresso" aria-label="Progresso da solicitação">
        {passosExibidos.map((passo, posicao) => {
          const final = situacao === "encerrada" && posicao === passosExibidos.length - 1;
          const concluido = posicao <= indice;
          const atual = posicao === indice && !final;

          let classe = "linha-tempo-passo";
          if (final) classe += " linha-tempo-passo-erro";
          else if (atual && situacao === "atencao") classe += " linha-tempo-passo-atencao";
          else if (concluido) classe += " linha-tempo-passo-concluido";
          if (atual || final) classe += " linha-tempo-passo-atual";

          return (
            <li key={passo.rotulo} className={classe}>
              <span className="linha-tempo-passo-icone">
                <passo.Icone size={16} />
              </span>
              <span className="linha-tempo-passo-rotulo">
                {atual && situacao === "atencao" ? "Aguardando confirmação" : passo.rotulo}
              </span>
            </li>
          );
        })}
      </ol>

      {etapasRecentesPrimeiro.length > 0 && (
        <ol className="linha-tempo-etapas">
          {etapasRecentesPrimeiro.map((etapa, posicao) => {
            const Icone = ICONE_POR_STATUS[etapa.status];

            return (
              <li
                key={etapa.id}
                className={`linha-tempo-etapa linha-tempo-tom-${TOM_POR_STATUS[etapa.status]}${
                  posicao === 0 ? " linha-tempo-etapa-recente" : ""
                }`}
              >
                <span className="linha-tempo-etapa-icone">
                  <Icone size={16} />
                </span>

                <div className="linha-tempo-etapa-conteudo">
                  <div className="linha-tempo-etapa-topo">
                    <p className="linha-tempo-etapa-titulo">{tituloEtapa[etapa.status]}</p>
                    <time className="linha-tempo-etapa-data">{etapa.dataHora}</time>
                  </div>

                  {etapa.motivo && <span className="linha-tempo-etapa-motivo">{etapa.motivo}</span>}

                  {etapa.descricao && <p className="linha-tempo-etapa-descricao">{etapa.descricao}</p>}

                  {etapa.dataLimiteRetirada && (
                    <p className="linha-tempo-etapa-prazo">
                      <CalendarClock size={14} />
                      Retirar até {etapa.dataLimiteRetirada}
                    </p>
                  )}
                </div>
              </li>
            );
          })}
        </ol>
      )}
    </div>
  );
}

export default LinhaDoTempoSolicitacao;
