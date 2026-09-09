import apiClient from "./axiosConfig";

import type { DadosMudaResumo } from "../types/DadosMudaResumo";
import type { DadosMudaDetalhes } from "../types/DadosMudaDetalhes";
import type { MudaFilter } from "../types/MudaFilter";
import type { Muda } from "../types/Muda";

export async function listarMudas(
  filtro: MudaFilter
): Promise<DadosMudaResumo[]> {

  const response = await apiClient.get("/mudas", {
    params: filtro,
  });

  return response.data;
}

export async function buscarDetalhesMuda(
  id: string
): Promise<DadosMudaDetalhes> {

  const response = await apiClient.get(`/mudas/${id}`);

  return response.data;
}

export async function buscarMuda(
  id: number
): Promise<Muda> {

  const response = await apiClient.get(`/mudas/${id}`);

  return response.data;
}

export async function salvar(
  muda: Muda
): Promise<Muda> {

  const ehEdicao = muda.id !== undefined;

  const response = ehEdicao
    ? await apiClient.put(`/mudas/${muda.id}`, muda)
    : await apiClient.post("/mudas", muda);

  return response.data;
}

export async function deletarMuda(
  id: number
): Promise<void> {

  await apiClient.delete(`/mudas/${id}`);
}