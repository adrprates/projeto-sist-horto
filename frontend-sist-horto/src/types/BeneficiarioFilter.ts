import { Role } from "./Role";

export interface BeneficiarioFilter {
  cpf?: string;
  nome?: string;
  email?: string;
  role?: Role;
}