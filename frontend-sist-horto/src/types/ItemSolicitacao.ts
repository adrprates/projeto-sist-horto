import type { CategoriaMuda } from "./CategoriaMuda";

export interface MudaResumoItem {
  id: number;
  nomesPopulares: string[];
  categoria: CategoriaMuda;
  linkImagemArvore?: string;
}

export interface ItemSolicitacao {
  id: number;
  muda: MudaResumoItem;
  quantidade: number;
}