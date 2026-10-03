import { isAxiosError } from "axios";

export function extrairMensagemErro(
  erro: unknown,
  mensagemPadrao = "Ocorreu um erro. Tente novamente."
): string {
  if (!isAxiosError(erro)) {
    return mensagemPadrao;
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

  return mensagemPadrao;
}
