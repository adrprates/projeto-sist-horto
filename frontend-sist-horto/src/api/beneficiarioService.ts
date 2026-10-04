import apiClient from "./axiosConfig";

import type { BeneficiarioFilter } from "../types/BeneficiarioFilter";
import type { DadosBeneficiario } from "../types/DadosBeneficiario";
import type { CadastroBeneficiario } from "../types/CadastroBeneficiario";
import type { Credenciais } from "../types/Credenciais";
import type { AtualizarPerfil } from "../types/AtualizarPerfil";

export async function listarBeneficiarios(
    filtro: BeneficiarioFilter
): Promise<DadosBeneficiario[]> {
  const response = await apiClient.get("/beneficiarios", {
    params: filtro,
  });

  return response.data;
}

export async function buscarBeneficiario(id: number): Promise<DadosBeneficiario> {
  const response = await apiClient.get(`/beneficiarios/${id}`);
  return response.data;
}

export async function cadastrarBeneficiario(dados: CadastroBeneficiario): Promise<Credenciais> {
  const response = await apiClient.post("/beneficiarios", dados);
  return response.data;
}

export async function redefinirSenhaBeneficiario(id: number): Promise<Credenciais> {
  const response = await apiClient.post(`/beneficiarios/${id}/redefinir-senha`);
  return response.data;
}

export async function atualizarBeneficiario(
  id: number,
  dados: AtualizarPerfil
): Promise<DadosBeneficiario> {
  const response = await apiClient.put(`/beneficiarios/${id}`, dados);
  return response.data;
}

export async function corrigirCpfBeneficiario(id: number, cpf: string): Promise<DadosBeneficiario> {
  const response = await apiClient.patch(`/beneficiarios/${id}/cpf`, { cpf });
  return response.data;
}
