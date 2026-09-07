import type { CategoriaMuda } from "./CategoriaMuda";

export interface Muda {
    id?: number;
    perdeMuitasFolhas?: boolean;
    possuiFlores?: boolean;
    possuiFrutos?: boolean;
    categoria?: CategoriaMuda;
    classe?: string;
    corFlor?: string;
    familia?: string;
    filo?: string;
    ordem?: string;
    reino?: string;
    epocaFlores?: string;
    epocaFrutos?: string;
    formato?: string;
    raizes?: string;
    tamanho?: string;
    nomesPopulares: string[];
    tiposFlores?: string;
    tiposFrutos?: string;
    linkImagemArvore?: string;
    linkImagemFlores?: string;
    linkImagemFrutos?: string;
}