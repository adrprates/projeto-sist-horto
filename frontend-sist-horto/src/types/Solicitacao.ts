import type { ItemSolicitacao } from "./ItemSolicitacao";
import type { StatusSolicitacao } from "./StatusSolicitacao";

export interface Solicitacao {
  id: number;
  dataSolicitacao: string;
  statusSolicitacao: StatusSolicitacao;
  itens: ItemSolicitacao[];
}