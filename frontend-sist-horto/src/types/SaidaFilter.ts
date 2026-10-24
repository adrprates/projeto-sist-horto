import type { CategoriaMuda } from "./CategoriaMuda";

export interface SaidaFilter {
  dataInicial?: string;
  dataFinal?: string;
  categoria?: CategoriaMuda;
  mudaId?: number;
}
