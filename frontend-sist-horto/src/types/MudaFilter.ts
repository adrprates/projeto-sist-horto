import type { CategoriaMuda } from "./CategoriaMuda";

export interface MudaFilter {
  nomePopular?: string;
  categoria?: CategoriaMuda;
  perdeMuitasFolhas?: boolean;
  possuiFlores?: boolean;
  possuiFrutos?: boolean;
}