import { isAxiosError } from "axios";

const MENSAGEM_PADRAO = "Ocorreu um erro. Tente novamente.";

export function extrairMensagemErro(erro: unknown): string {
  if (!isAxiosError(erro)) {
    return MENSAGEM_PADRAO;
  }

  const dados = erro.response?.data;

  if (typeof dados === "string" && dados.trim().length > 0) {
    return dados;
  }

  if (dados && typeof dados === "object") {
    const possivelMensagem =
      (dados as Record<string, unknown>).message ??
      (dados as Record<string, unknown>).erro ??
      (dados as Record<string, unknown>).error;

    if (typeof possivelMensagem === "string" && possivelMensagem.trim().length > 0) {
      return possivelMensagem;
    }
  }

  return MENSAGEM_PADRAO;
}