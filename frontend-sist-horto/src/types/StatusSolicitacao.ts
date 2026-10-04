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

export const tituloEtapa: Record<StatusSolicitacao, string> = {
  [StatusSolicitacao.RASCUNHO]: "Solicitação em montagem",
  [StatusSolicitacao.PENDENTE]: "Solicitação enviada",
  [StatusSolicitacao.APROVADA]: "Solicitação aprovada",
  [StatusSolicitacao.REJEITADA]: "Solicitação rejeitada",
  [StatusSolicitacao.AGUARDANDO_CONFIRMACAO]: "Alteração proposta pela Secretaria",
  [StatusSolicitacao.PRONTA_PARA_RETIRADA]: "Pronta para retirada",
  [StatusSolicitacao.ENTREGUE]: "Mudas entregues",
  [StatusSolicitacao.EXPIRADA]: "Prazo de retirada expirado",
};

export const acaoAdministrativa: Partial<Record<StatusSolicitacao, string>> = {
  [StatusSolicitacao.APROVADA]: "Aprovar solicitação",
  [StatusSolicitacao.REJEITADA]: "Rejeitar solicitação",
  [StatusSolicitacao.AGUARDANDO_CONFIRMACAO]: "Propor alteração nos itens",
  [StatusSolicitacao.PRONTA_PARA_RETIRADA]: "Marcar como pronta para retirada",
  [StatusSolicitacao.ENTREGUE]: "Registrar entrega",
  [StatusSolicitacao.EXPIRADA]: "Marcar como expirada",
};

export const mensagemPadraoEtapa: Partial<Record<StatusSolicitacao, string>> = {
  [StatusSolicitacao.APROVADA]:
    "Boa notícia: sua solicitação foi aprovada! Agora as mudas serão separadas no Horto Florestal. Avisaremos por aqui assim que estiverem prontas para retirada.",
  [StatusSolicitacao.REJEITADA]:
    "Infelizmente não foi possível atender à sua solicitação desta vez. Em caso de dúvidas, procure a Secretaria de Meio Ambiente.",
  [StatusSolicitacao.AGUARDANDO_CONFIRMACAO]:
    "A Secretaria precisou ajustar os itens da sua solicitação. Confira as alterações abaixo e responda se aceita a nova proposta.",
  [StatusSolicitacao.PRONTA_PARA_RETIRADA]:
    "Suas mudas estão separadas e esperando por você no Horto Florestal. Leve um documento com foto e faça a retirada até a data limite.",
  [StatusSolicitacao.ENTREGUE]: "Mudas entregues! Obrigado por ajudar a deixar Patrocínio mais verde.",
  [StatusSolicitacao.EXPIRADA]:
    "O prazo de retirada terminou e as mudas voltaram para o estoque. Se ainda tiver interesse, procure a Secretaria de Meio Ambiente.",
};

export const MOTIVOS_REJEICAO = [
  "Falta de mudas em estoque",
  "Inconformidade nos dados cadastrais",
  "Espécie não indicada para o local de plantio",
  "Solicitação duplicada",
  "Outro motivo",
];

export const transicoesPermitidas: Record<StatusSolicitacao, StatusSolicitacao[]> = {
  [StatusSolicitacao.RASCUNHO]: [StatusSolicitacao.PENDENTE],
  [StatusSolicitacao.PENDENTE]: [
    StatusSolicitacao.APROVADA,
    StatusSolicitacao.AGUARDANDO_CONFIRMACAO,
    StatusSolicitacao.REJEITADA,
  ],
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
