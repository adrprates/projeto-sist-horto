import type { CategoriaMuda } from "./CategoriaMuda";

export interface ItemRelatorioSaida {
  dataSaida: string;
  solicitacaoId: number;
  nomeMuda: string;
  categoria: CategoriaMuda;
  quantidade: number;
  nomeBeneficiario: string;
}

export interface ResumoSaidaMuda {
  mudaId: number;
  nomeMuda: string;
  categoria: CategoriaMuda;
  quantidade: number;
  totalBeneficiarios: number;
}

export interface RelatorioSaida {
  totalMudas: number;
  totalRetiradas: number;
  totalBeneficiarios: number;
  resumo: ResumoSaidaMuda[];
  itens: ItemRelatorioSaida[];
}
