import { useNavigate } from "react-router-dom";
import { AlertCircle } from "lucide-react";
import "./AvisoAutenticacaoNecessaria.css";

interface AvisoAutenticacaoNecessariaProps {
  mensagem: string;
}

function AvisoAutenticacaoNecessaria({ mensagem }: AvisoAutenticacaoNecessariaProps) {
  const navigate = useNavigate();

  return (
    <div className="aviso-autenticacao">
      <AlertCircle size={18} className="aviso-autenticacao-icone" />
      <p className="aviso-autenticacao-texto">{mensagem}</p>
      <button type="button" className="aviso-autenticacao-botao" onClick={() => navigate("/login")}>
        Fazer login
      </button>
    </div>
  );
}

export default AvisoAutenticacaoNecessaria;