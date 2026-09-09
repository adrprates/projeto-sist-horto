import apiClient from "./axiosConfig";
import type { Estoque } from "../types/Estoque";

export async function buscarPorEstoque(
  idMuda: number
): Promise<Estoque> {

  const response = await apiClient.get(
    `/estoques/${idMuda}`
  );

  return response.data;
}

export async function atualizarQuantidade(
  idMuda: number,
  quantidade: number
): Promise<void> {

  await apiClient.post(
    `/estoques/${idMuda}/atualizar`,
    null,
    {
      params: { quantidade }
    }
  );
}

export async function adicionarQuantidade(
  idMuda: number,
  quantidade: number
): Promise<void> {

  await apiClient.post(
    `/estoques/${idMuda}/adicionar`,
    null,
    {
      params: { quantidade }
    }
  );
}

export async function removerQuantidade(
  idMuda: number,
  quantidade: number
): Promise<void> {

  await apiClient.patch(
    `/estoques/${idMuda}/remover`,
    null,
    {
      params: { quantidade }
    }
  );
}