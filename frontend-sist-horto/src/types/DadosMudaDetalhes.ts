import type { DadosMudaResumo } from "./DadosMudaResumo";

export interface DadosMudaDetalhes extends DadosMudaResumo {
  reino: string;
  filo: string;
  classe: string;
  ordem: string;
  perdeMuitasFolhas: boolean;
  possuiFlores: boolean;
  possuiFrutos: boolean;
  corFlor?: string;
  epocaFlores?: string;
  epocaFrutos?: string;
  formato?: string;
  raizes?: string;
  tamanho?: string;
  tiposFlores?: string;
  tiposFrutos?: string;
  linkImagemFlores?: string;
  linkImagemFrutos?: string;
  imagensAdicionais?: string[];
}