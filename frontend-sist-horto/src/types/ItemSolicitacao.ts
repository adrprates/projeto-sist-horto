import type { CategoriaMuda } from "./CategoriaMuda";

export interface MudaResumoItem {
  id: number;
  nomesPopulares: string[];
  categoria: CategoriaMuda;
  linkImagemArvore?: string;
  disponivel?: boolean;
}

export interface ItemSolicitacao {
  id: number;
  muda: MudaResumoItem;
  quantidade: number;
}