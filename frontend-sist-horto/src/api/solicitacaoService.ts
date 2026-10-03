import apiClient from "./axiosConfig";
import type { Solicitacao } from "../types/Solicitacao";
import type { SolicitacaoBeneficiarioResumo } from "../types/SolicitacaoBeneficiarioResumo";
import type { SolicitacaoAdmin } from "../types/SolicitacaoAdmin";
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

export async function buscarSolicitacaoAtual(): Promise<SolicitacaoAdmin | null> {
  const response = await apiClient.get(`/solicitacoes/minha-solicitacao-atual`, {
    validateStatus: (status) => status === 200 || status === 204,
  });

  if (response.status === 204) {
    return null;
  }

  return response.data;
}

export async function listarMinhasSolicitacoes(): Promise<SolicitacaoBeneficiarioResumo[]> {
  const response = await apiClient.get(`/solicitacoes/minhas-solicitacoes`);
  return response.data;
}

export async function buscarMinhaSolicitacaoDetalhada(id: number): Promise<SolicitacaoAdmin> {
  const response = await apiClient.get(`/solicitacoes/minhas-solicitacoes/${id}`);
  return response.data;
}

export async function responderConfirmacao(
  id: number,
  aceitar: boolean
): Promise<SolicitacaoAdmin> {
  const response = await apiClient.post(`/solicitacoes/${id}/confirmar`, { aceitar });
  return response.data;
}