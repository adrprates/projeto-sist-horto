import { Lock } from "lucide-react";
import PaginaAviso from "../../components/PaginaAviso/PaginaAviso";

export const PaginaForbidden = () => {
  return (
    <PaginaAviso
      Icone={Lock}
      codigo="Erro 403"
      titulo="Acesso negado"
      texto="Você não tem permissão para acessar esta página. Se acha que isso é um engano, fale com a Secretaria."
    />
  );
};
