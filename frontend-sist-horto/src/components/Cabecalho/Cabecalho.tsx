import { useState } from "react";
import { Leaf, Menu, X, ClipboardList, User } from "lucide-react";
import "./Cabecalho.css";

function Cabecalho() {
  const [menuAberto, setMenuAberto] = useState(false);

  return (
    <header className="cabecalho">
      <div className="logo">
        <Leaf className="logo-icone" size={26} />
        <h1 className="logo-texto">Sistema Horto</h1>
      </div>

      <button
        type="button"
        className="botao-menu"
        onClick={() => setMenuAberto(!menuAberto)}
        aria-label="Abrir menu"
      >
        {menuAberto ? <X size={24} /> : <Menu size={24} />}
      </button>

      <nav className={menuAberto ? "nav nav-aberto" : "nav"}>
        <a href="#">
          <Leaf size={18} />
          Catálogo de Mudas
        </a>
        <a href="#">
          <ClipboardList size={18} />
          Minhas Solicitações
        </a>
        <a href="#">
          <User size={18} />
          Meu Perfil
        </a>
      </nav>
    </header>
  );
}

export default Cabecalho;