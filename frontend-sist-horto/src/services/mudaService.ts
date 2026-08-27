import type { DadosMuda } from "../types/DadosMuda";

export async function listarMudas(): Promise<DadosMuda[]> {

  const resposta = await fetch(
    "http://localhost:8080/mudas"
  );

  return resposta.json();
}