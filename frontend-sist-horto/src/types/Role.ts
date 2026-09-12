export const Role = {
  ADMINISTRADOR: "ADMINISTRADOR",
  BENEFICIARIO: "BENEFICIARIO",
} as const;

export type Role =
  typeof Role[keyof typeof Role];

export const rotuloRole: Record<Role, string> = {
  [Role.ADMINISTRADOR]: "Administrador",
  [Role.BENEFICIARIO]: "Beneficiário",
};