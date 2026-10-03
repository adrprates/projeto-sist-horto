import { Sprout } from "lucide-react";
import PaginaAviso from "../../components/PaginaAviso/PaginaAviso";

export const PaginaNotFound = () => {
  return (
    <PaginaAviso
      Icone={Sprout}
      codigo="Erro 404"
      titulo="Página não encontrada"
      texto="Essa muda ainda não brotou por aqui. Confira o endereço ou volte para o início."
    />
  );
};
