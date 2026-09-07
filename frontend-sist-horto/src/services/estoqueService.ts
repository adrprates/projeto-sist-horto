import type { Estoque } from "../types/Estoque";

const URL_BASE = "http://localhost:8080";

export async function buscarPorEstoque(idMuda: number): Promise<Estoque> {
  const resposta = await fetch(
    `${URL_BASE}/estoques/${idMuda}`
  );

  return resposta.json();
}

export async function atualizarQuantidade(
  idMuda: number,
  quantidade: number
): Promise<void> {
  await fetch(
    `${URL_BASE}/estoques/${idMuda}/atualizar?quantidade=${quantidade}`,
    {
      method: "POST",
    }
  );
}

export async function adicionarQuantidade(
  idMuda: number,
  quantidade: number
): Promise<void> {
  await fetch(
    `${URL_BASE}/estoques/${idMuda}/adicionar?quantidade=${quantidade}`,
    {
      method: "POST",
    }
  );
}

export async function removerQuantidade(
  idMuda: number,
  quantidade: number
): Promise<void> {
  await fetch(
    `${URL_BASE}/estoques/${idMuda}/remover?quantidade=${quantidade}`,
    {
      method: "PATCH",
    }
  );
}