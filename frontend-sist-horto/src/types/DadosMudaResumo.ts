import type { CategoriaMuda } from "./CategoriaMuda";

export interface DadosMudaResumo {
  id: number;
  nomesPopulares: string[];
  nomeCientifico: string;
  categoria: CategoriaMuda;
  familia: string;
  linkImagemArvore: string;
}