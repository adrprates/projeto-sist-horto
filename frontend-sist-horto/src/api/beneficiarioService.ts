import apiClient from "./axiosConfig";

import type { BeneficiarioFilter } from "../types/BeneficiarioFilter";
import type { DadosBeneficiario } from "../types/DadosBeneficiario";

export async function listarBeneficiarios(
    filtro: BeneficiarioFilter
): Promise<DadosBeneficiario[]> {
  const response = await apiClient.get("/beneficiarios", {
    params: filtro,
  });

  return response.data;
}