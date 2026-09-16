import apiClient from "./axiosConfig";

import type { ParametroAnual } from "../types/ParametroAnual";

export async function listarParametrosAnuais(): Promise<ParametroAnual[]> {
    const response = await apiClient.get("/parametros");
    return response.data;
}

export async function salvar(
  parametro: ParametroAnual,
  ehEdicao: boolean
): Promise<ParametroAnual> {
  const response = ehEdicao
    ? await apiClient.put(`/parametros/${parametro.ano}`, parametro)
    : await apiClient.post("/parametros", parametro);

  return response.data;
}

export async function buscarParametroAnual(ano: number): Promise<ParametroAnual> {
  const response = await apiClient.get(`/parametros/${ano}`);
  return response.data;
}

export async function deletar(ano: number): Promise<void> {
    await apiClient.delete(`/parametros/${ano}`);
}