import apiClient from "./axiosConfig";
import type { Solicitacao } from "../types/Solicitacao";
import type { SolicitacaoAdmin } from "../types/SolicitacaoAdmin";
import type { MontarSolicitacaoRequest } from "../types/MontarSolicitacaoRequest";
import type { ParametroAnualDisponivel } from "../types/ParametroAnualDisponivel";

const base = (beneficiarioId: number) => `/solicitacoes/beneficiarios/${beneficiarioId}`;

export async function buscarSolicitacaoAtualDoBeneficiario(
  beneficiarioId: number
): Promise<SolicitacaoAdmin | null> {
  const response = await apiClient.get(`${base(beneficiarioId)}/atual`, {
    validateStatus: (status) => status === 200 || status === 204,
  });

  return response.status === 204 ? null : response.data;
}

export async function buscarRascunhoDoBeneficiario(beneficiarioId: number): Promise<Solicitacao> {
  const response = await apiClient.get(`${base(beneficiarioId)}/rascunho`);
  return response.data;
}

export async function buscarSaldoDoBeneficiario(
  beneficiarioId: number
): Promise<ParametroAnualDisponivel> {
  const response = await apiClient.get(`${base(beneficiarioId)}/rascunho/saldo`);
  return response.data;
}

export async function adicionarItemParaBeneficiario(
  beneficiarioId: number,
  dados: MontarSolicitacaoRequest
): Promise<Solicitacao> {
  const response = await apiClient.post(`${base(beneficiarioId)}/rascunho/adicionar`, dados);
  return response.data;
}

export async function atualizarItemDoBeneficiario(
  beneficiarioId: number,
  itemId: number,
  quantidade: number
): Promise<Solicitacao> {
  const response = await apiClient.put(`${base(beneficiarioId)}/rascunho/itens/${itemId}`, {
    quantidade,
  });
  return response.data;
}

export async function removerItemDoBeneficiario(
  beneficiarioId: number,
  itemId: number
): Promise<Solicitacao> {
  const response = await apiClient.delete(`${base(beneficiarioId)}/rascunho/itens/${itemId}`);
  return response.data;
}

export async function enviarSolicitacaoDoBeneficiario(
  beneficiarioId: number
): Promise<SolicitacaoAdmin> {
  const response = await apiClient.post(`${base(beneficiarioId)}/enviar`);
  return response.data;
}
