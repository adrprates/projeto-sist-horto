import { useState } from "react";
import { Leaf, Menu, X } from "lucide-react";
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
        <a href="#">Início</a>
        <a href="#">Catálogo de Mudas</a>
        <a href="#">Minhas Mudas</a>
        <a href="#">Relatórios</a>
        <a href="#">Meu Perfil</a>
      </nav>
    </header>
  );
}

export default Cabecalho;