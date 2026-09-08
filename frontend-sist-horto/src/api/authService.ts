import apiClient from './axiosConfig';

export interface LoginRequest {
  login: string;
  senha?: string;
}

export interface TokenResponse {
  token: string;
}

export const loginUser = async (credentials: LoginRequest): Promise<TokenResponse> => {
  const response = await apiClient.post('/auth/login', credentials);
  return response.data;
};