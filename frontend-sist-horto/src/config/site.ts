export interface DadosLocal {
  nome: string;
  endereco: string;
  cep?: string;
  horario: string;
  telefone: string;
  email?: string;
  mapaBusca: string;
}

export const SITE = {
  nome: "Horto Municipal de Patrocínio",
  cidade: "Patrocínio, Minas Gerais",
  secretaria: {
    nome: "Secretaria Municipal de Meio Ambiente",
    sigla: "SEMMA",
    endereco: "Av. Marciano Pires, 629 — Distrito Industrial",
    cep: "38740-500",
    horario: "Segunda a sexta, 8h às 16h",
    telefone: "(34) 3832-0656",
    email: "meioambiente@patrocinio.mg.gov.br",
    mapaBusca: "Av. Marciano Pires, 629, Distrito Industrial, Patrocínio - MG",
  },
  horto: {
    nome: "Horto Florestal de Patrocínio",
    endereco: "Bairro São Judas Tadeu",
    horario: "Segunda a sexta, 7h às 16h",
    telefone: "(34) 3832-0656",
    mapaBusca: "Horto Florestal, São Judas Tadeu, Patrocínio - MG",
  },
  prefeitura: {
    nome: "Prefeitura Municipal de Patrocínio",
  },
} satisfies {
  nome: string;
  cidade: string;
  secretaria: DadosLocal & { sigla: string };
  horto: DadosLocal;
  prefeitura: { nome: string };
};
