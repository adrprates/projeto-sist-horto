import apiClient from "./axiosConfig";
import type { SolicitacaoAdmin } from "../types/SolicitacaoAdmin";
import type { SolicitacaoFilter } from "../types/SolicitacaoFilter";
import type { AtualizarEtapaRequest } from "../types/AtualizarEtapaRequest";
import type { PaginaResponse } from "../types/PaginaResponse";

export type OrdemSolicitacoes = "prioridade" | "antigas" | "recentes";

export async function listarSolicitacoes(
  filtro: SolicitacaoFilter,
  pagina: number,
  tamanho: number,
  ordem: OrdemSolicitacoes
): Promise<PaginaResponse<SolicitacaoAdmin>> {
  const response = await apiClient.get("/solicitacoes", {
    params: { ...filtro, pagina, tamanho, ordem },
  });
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