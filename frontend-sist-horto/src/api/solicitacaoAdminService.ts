import apiClient from "./axiosConfig";
import type { SolicitacaoAdmin } from "../types/SolicitacaoAdmin";
import type { SolicitacaoFilter } from "../types/SolicitacaoFilter";
import type { AtualizarEtapaRequest } from "../types/AtualizarEtapaRequest";

export async function listarSolicitacoes(
  filtro: SolicitacaoFilter
): Promise<SolicitacaoAdmin[]> {
  const response = await apiClient.get("/solicitacoes", { params: filtro });
  return response.data;
}

export async function buscarSolicitacaoPorId(id: number): Promise<SolicitacaoAdmin> {
  const response = await apiClient.get(`/solicitacoes/${id}`);
  return response.data;
}

export async function atualizarEtapa(
  id: number,
  dados: AtualizarEtapaRequest
): Promise<SolicitacaoAdmin> {
  const response = await apiClient.post(`/solicitacoes/${id}/etapas`, dados);
  return response.data;
}