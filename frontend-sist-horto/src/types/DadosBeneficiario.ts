import type { Role } from "./Role";

export interface DadosBeneficiario {
    id?: number;
    cpf?: string;
    celular?: string;
    role?: Role;
    telefone?: string;
    email?: string;
    nome?: string;
    endereo?: string;
}