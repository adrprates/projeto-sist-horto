import type { DadosMudaResumo } from "../types/DadosMudaResumo";
import type { DadosMudaDetalhes } from "../types/DadosMudaDetalhes";
import type { MudaFilter } from "../types/MudaFilter";
import type { Muda } from "../types/Muda";

const URL_BASE = "http://localhost:8080";

export async function listarMudas(filtro: MudaFilter): Promise<DadosMudaResumo[]> {
  const parametros = new URLSearchParams();

  if (filtro.nomePopular) {
    parametros.append("nomePopular", filtro.nomePopular);
  }
  if (filtro.categoria) {
    parametros.append("categoria", filtro.categoria);
  }
  if (filtro.perdeMuitasFolhas !== undefined) {
    parametros.append("perdeMuitasFolhas", String(filtro.perdeMuitasFolhas));
  }
  if (filtro.possuiFlores !== undefined) {
    parametros.append("possuiFlores", String(filtro.possuiFlores));
  }
  if (filtro.possuiFrutos !== undefined) {
    parametros.append("possuiFrutos", String(filtro.possuiFrutos));
  }

  const resposta = await fetch(`${URL_BASE}/mudas?${parametros.toString()}`);
  return resposta.json();
}

export async function buscarDetalhesMuda(id: string): Promise<DadosMudaDetalhes> {
  const resposta = await fetch(`${URL_BASE}/mudas/${id}`);
  return resposta.json();
}

export async function buscarMuda(id: number): Promise<Muda> {
  const resposta = await fetch(`${URL_BASE}/mudas/${id}`);

  if (!resposta.ok) {
    throw new Error("Erro ao buscar muda");
  }

  return resposta.json();
}

export async function salvar(muda: Muda): Promise<Muda> {
  const ehEdicao = muda.id !== undefined;

  const resposta = await fetch(
    ehEdicao
      ? `${URL_BASE}/mudas/${muda.id}`
      : `${URL_BASE}/mudas`,
    {
      method: ehEdicao ? "PUT" : "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(muda),
    }
  );

  if (!resposta.ok) {
    throw new Error("Erro ao salvar muda");
  }

  return resposta.json();
}

export async function deletarMuda(id: number): Promise<void> {
  const resposta = await fetch(`${URL_BASE}/mudas/${id}`, {
    method: "DELETE",
  });

  if (!resposta.ok) {
    throw new Error("Erro ao deletar muda");
  }
}