import apiClient from './axiosConfig';

export interface LoginRequest {
  login: string;
  senha?: string;
}

export interface RegisterRequest {
  cpf: string;
  celular: string;
  telefone?: string;
  email: string;
  nome: string;
  endereco: string;
  login: string;
  senha: string;
}

export interface TokenResponse {
  token: string;
}

export const loginUser = async (credentials: LoginRequest): Promise<TokenResponse> => {
  const response = await apiClient.post('/auth/login', credentials);
  return response.data;
};

export async function registrarUsuario(
  dados: RegisterRequest
): Promise<void> {
  await apiClient.post("/auth/registro", dados);
}