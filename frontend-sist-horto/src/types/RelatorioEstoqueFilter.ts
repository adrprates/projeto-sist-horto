import type { CategoriaMuda } from "./CategoriaMuda";
import type { SituacaoEstoque } from "./SituacaoEstoque";

export interface RelatorioEstoqueFilter {
  categoria?: CategoriaMuda;
  situacao?: SituacaoEstoque;
}
