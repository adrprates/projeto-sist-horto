import type { CategoriaMuda } from "./CategoriaMuda";
import type { SituacaoEstoque } from "./SituacaoEstoque";

export interface ItemRelatorioEstoque {
  mudaId: number;
  nomeMuda: string;
  categoria: CategoriaMuda;
  estoqueFisico: number;
  quantidadeReservada: number;
  estoqueDisponivel: number;
  situacao: SituacaoEstoque;
  motivoIndisponibilidade?: string | null;
}

export interface ResumoEstoqueCategoria {
  categoria: CategoriaMuda;
  totalEspecies: number;
  estoqueFisico: number;
  quantidadeReservada: number;
  estoqueDisponivel: number;
}

export interface RelatorioEstoque {
  totalEspecies: number;
  estoqueFisico: number;
  quantidadeReservada: number;
  estoqueDisponivel: number;
  categorias: ResumoEstoqueCategoria[];
  itens: ItemRelatorioEstoque[];
}
