export const SituacaoEstoque = {
  DISPONIVEL: "DISPONIVEL",
  ESTOQUE_BAIXO: "ESTOQUE_BAIXO",
  ESGOTADA: "ESGOTADA",
  INDISPONIVEL: "INDISPONIVEL",
} as const;

export type SituacaoEstoque =
  typeof SituacaoEstoque[keyof typeof SituacaoEstoque];

export const rotuloSituacaoEstoque: Record<SituacaoEstoque, string> = {
  [SituacaoEstoque.DISPONIVEL]: "Disponível",
  [SituacaoEstoque.ESTOQUE_BAIXO]: "Estoque baixo",
  [SituacaoEstoque.ESGOTADA]: "Esgotada",
  [SituacaoEstoque.INDISPONIVEL]: "Indisponível",
};
