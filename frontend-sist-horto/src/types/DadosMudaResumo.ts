import type { CategoriaMuda } from "./CategoriaMuda";

export interface DadosMudaResumo {
  id: string;
  nomesPopulares: string[];
  nomeCientifico: string;
  categoria: CategoriaMuda;
  familia: string;
  linkImagemArvore: string;
  estoqueDisponivel: number;
}