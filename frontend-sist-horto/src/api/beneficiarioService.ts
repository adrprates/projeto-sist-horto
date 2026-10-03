import apiClient from "./axiosConfig";

import type { BeneficiarioFilter } from "../types/BeneficiarioFilter";
import type { DadosBeneficiario } from "../types/DadosBeneficiario";
import type { CadastroBeneficiario } from "../types/CadastroBeneficiario";
import type { Credenciais } from "../types/Credenciais";

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
