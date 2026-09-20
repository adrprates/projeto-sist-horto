import apiClient from "./axiosConfig";
import type { Solicitacao } from "../types/Solicitacao";
import type { MontarSolicitacaoRequest } from "../types/MontarSolicitacaoRequest";
import type { ParametroAnualDisponivel } from "../types/ParametroAnualDisponivel";

export async function buscarRascunho(): Promise<Solicitacao> {
  const response = await apiClient.get(`/solicitacoes/rascunho`);
  return response.data;
}

export async function adicionarItemRascunho(
  dados: MontarSolicitacaoRequest
): Promise<Solicitacao> {
  const response = await apiClient.post(`/solicitacoes/rascunho/adicionar`, dados);
  return response.data;
}

export async function atualizarQuantidadeItemRascunho(
  itemId: number,
  quantidade: number
 ): Promise<Solicitacao> {
  const response = await apiClient.put(`/solicitacoes/rascunho/itens/${itemId}`, { quantidade });
  return response.data;
}

export async function removerItemRascunho(itemId: number): Promise<Solicitacao> {
  const response = await apiClient.delete(`/solicitacoes/rascunho/itens/${itemId}`);
  return response.data;
}

export async function buscarSaldoAtual(): Promise<ParametroAnualDisponivel> {
  const response = await apiClient.get(`/solicitacoes/rascunho/saldo`);
  return response.data;
}

export async function enviarSolicitacao(id: number): Promise<Solicitacao> {
  const response = await apiClient.post(`/solicitacoes/${id}/enviar`);
  return response.data;
}