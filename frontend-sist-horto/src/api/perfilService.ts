import apiClient from "./axiosConfig";

import type { Perfil } from "../types/Perfil";
import type { AtualizarPerfil } from "../types/AtualizarPerfil";

export async function obterPerfil(): Promise<Perfil> {
  const response = await apiClient.get("/perfil");
  return response.data;
}

export async function atualizarPerfil(
  dados: AtualizarPerfil
): Promise<Perfil> {
  const response = await apiClient.put("/perfil", dados);
  return response.data;
}