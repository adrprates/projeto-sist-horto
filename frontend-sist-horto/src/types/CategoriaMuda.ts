export const CategoriaMuda = {
  FRUTIFERAS: "FRUTIFERAS",
  ARBORIZACAO: "ARBORIZACAO",
  REFLORESTAMENTO: "REFLORESTAMENTO",
  MEDICINAIS: "MEDICINAIS",
  ORNAMENTAIS: "ORNAMENTAIS",
  IPES: "IPES",
} as const;

export type CategoriaMuda =
  typeof CategoriaMuda[keyof typeof CategoriaMuda];

export const rotuloCategoria: Record<CategoriaMuda, string> = {
  [CategoriaMuda.FRUTIFERAS]: "Frutíferas",
  [CategoriaMuda.ARBORIZACAO]: "Arborização",
  [CategoriaMuda.REFLORESTAMENTO]: "Reflorestamento",
  [CategoriaMuda.MEDICINAIS]: "Medicinais",
  [CategoriaMuda.ORNAMENTAIS]: "Ornamentais",
  [CategoriaMuda.IPES]: "Ipês",
};