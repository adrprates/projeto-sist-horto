import apiClient from "./axiosConfig";
import type { SaidaFilter } from "../types/SaidaFilter";
import type { RelatorioSaida } from "../types/RelatorioSaida";
import type { RelatorioEstoqueFilter } from "../types/RelatorioEstoqueFilter";
import type { RelatorioEstoque } from "../types/RelatorioEstoque";

export async function buscarRelatorioSaidas(filtro: SaidaFilter): Promise<RelatorioSaida> {
  const response = await apiClient.get("/relatorios/saidas", {
    params: filtro,
  });
  return response.data;
}

export async function buscarRelatorioEstoque(
  filtro: RelatorioEstoqueFilter
): Promise<RelatorioEstoque> {
  const response = await apiClient.get("/relatorios/estoque", {
    params: filtro,
  });
  return response.data;
}
