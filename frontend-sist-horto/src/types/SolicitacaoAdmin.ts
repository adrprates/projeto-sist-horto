import type { StatusSolicitacao } from "./StatusSolicitacao";
import type { EtapaSolicitacao } from "./EtapaSolicitacao";
import type { CategoriaMuda } from "./CategoriaMuda";

export interface BeneficiarioResumo {
  id: number;
  nome: string;
  cpf: string;
  email?: string;
  celular?: string;
}

export interface ItemSolicitacaoAdmin {
  id: number;
  muda: {
    id: number;
    nomesPopulares: string[];
    categoria: CategoriaMuda;
  };
  quantidade: number;
}

export interface SolicitacaoAdmin {
  id: number;
  dataSolicitacao: string;
  statusAtual: StatusSolicitacao;
  beneficiario: BeneficiarioResumo;
  itens: ItemSolicitacaoAdmin[];
  parametroAnual: {
    ano: number;
  };
  etapas?: EtapaSolicitacao[];
}