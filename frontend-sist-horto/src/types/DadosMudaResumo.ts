import type { CategoriaMuda } from "./CategoriaMuda";

export interface DadosMudaResumo {
  id: number;
  nomesPopulares: string[];
  categoria: CategoriaMuda;
  familia: string;
  linkImagemArvore: string;
  estoqueDisponivel: number;
  estoqueTotal: number;
  quantidadeReservada: number;
  disponivel: boolean;
  motivoIndisponibilidade?: string | null;
}
