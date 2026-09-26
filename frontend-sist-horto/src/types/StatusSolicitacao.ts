export const StatusSolicitacao = {
  RASCUNHO: "RASCUNHO",
  PENDENTE: "PENDENTE",
  APROVADA: "APROVADA",
  REJEITADA: "REJEITADA",
  AGUARDANDO_CONFIRMACAO: "AGUARDANDO_CONFIRMACAO",
  PRONTA_PARA_RETIRADA: "PRONTA_PARA_RETIRADA",
  ENTREGUE: "ENTREGUE",
  EXPIRADA: "EXPIRADA",
} as const;

export type StatusSolicitacao = typeof StatusSolicitacao[keyof typeof StatusSolicitacao];

export const rotuloStatusSolicitacao: Record<StatusSolicitacao, string> = {
  [StatusSolicitacao.RASCUNHO]: "Rascunho",
  [StatusSolicitacao.PENDENTE]: "Pendente",
  [StatusSolicitacao.APROVADA]: "Aprovada",
  [StatusSolicitacao.REJEITADA]: "Rejeitada",
  [StatusSolicitacao.AGUARDANDO_CONFIRMACAO]: "Aguardando Confirmação",
  [StatusSolicitacao.PRONTA_PARA_RETIRADA]: "Pronta para Retirada",
  [StatusSolicitacao.ENTREGUE]: "Entregue",
  [StatusSolicitacao.EXPIRADA]: "Expirada",
};

export const transicoesPermitidas: Record<StatusSolicitacao, StatusSolicitacao[]> = {
  [StatusSolicitacao.RASCUNHO]: [StatusSolicitacao.PENDENTE],
  [StatusSolicitacao.PENDENTE]: [StatusSolicitacao.APROVADA, StatusSolicitacao.REJEITADA],
  [StatusSolicitacao.APROVADA]: [
    StatusSolicitacao.PRONTA_PARA_RETIRADA,
    StatusSolicitacao.AGUARDANDO_CONFIRMACAO,
  ],
  [StatusSolicitacao.AGUARDANDO_CONFIRMACAO]: [
    StatusSolicitacao.APROVADA,
    StatusSolicitacao.REJEITADA,
  ],
  [StatusSolicitacao.PRONTA_PARA_RETIRADA]: [
    StatusSolicitacao.ENTREGUE,
    StatusSolicitacao.EXPIRADA,
  ],
  [StatusSolicitacao.ENTREGUE]: [],
  [StatusSolicitacao.REJEITADA]: [],
  [StatusSolicitacao.EXPIRADA]: [],
};

export const statusExigeDescricao: StatusSolicitacao[] = [
  StatusSolicitacao.REJEITADA,
  StatusSolicitacao.AGUARDANDO_CONFIRMACAO,
];

export const statusExigeDataLimite: StatusSolicitacao[] = [
  StatusSolicitacao.PRONTA_PARA_RETIRADA,
];