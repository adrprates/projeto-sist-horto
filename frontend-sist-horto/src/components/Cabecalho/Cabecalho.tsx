import { useState } from "react";
import { Link, NavLink } from "react-router-dom";
import { ClipboardList, LogIn, LogOut, Menu, Sprout, User, X } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";
import { SITE } from "../../config/site";
import "./Cabecalho.css";

interface LinkNavegacao {
  para: string;
  rotulo: string;
  fim?: boolean;
}

const LINKS_PUBLICOS: LinkNavegacao[] = [
  { para: "/", rotulo: "Início", fim: true },
  { para: "/secretaria", rotulo: "Secretaria" },
  { para: "/horto-florestal", rotulo: "Horto" },
  { para: "/catalogo", rotulo: "Catálogo" },
  { para: "/como-solicitar", rotulo: "Como solicitar" },
];

const LINKS_ADMIN: LinkNavegacao[] = [
  { para: "/catalogo", rotulo: "Gestão de Mudas" },
  { para: "/admin/solicitacoes", rotulo: "Gerenciar Solicitações" },
  { para: "/beneficiarios", rotulo: "Beneficiários" },
  { para: "/parametros", rotulo: "Parâmetros Anuais" },
];

function classeLink({ isActive }: { isActive: boolean }) {
  return isActive ? "nav-link nav-link-ativo" : "nav-link";
}

function Cabecalho() {
  const [menuAberto, setMenuAberto] = useState(false);
  const { logout, isAuthenticated, hasRole } = useAuth();

  const isAdmin = isAuthenticated && hasRole(["ADMINISTRADOR"]);
  const links = isAdmin ? LINKS_ADMIN : LINKS_PUBLICOS;

  function fecharMenu() {
    setMenuAberto(false);
  }

  function handleSair() {
    fecharMenu();
    logout();
  }

  const acoesConta = isAuthenticated ? (
    <>
      <NavLink to="/solicitacao" className="botao-destaque cabecalho-cta" onClick={fecharMenu}>
        <ClipboardList size={16} />
        Minhas solicitações
      </NavLink>
      <NavLink to="/perfil" className={classeLink} onClick={fecharMenu} title="Meu perfil">
        <User size={18} />
        <span className="cabecalho-rotulo-icone">Meu perfil</span>
      </NavLink>
      <button type="button" className="nav-link cabecalho-sair" onClick={handleSair} title="Sair">
        <LogOut size={18} />
        <span className="cabecalho-rotulo-icone">Sair</span>
      </button>
    </>
  ) : (
    <>
      <NavLink to="/login" className={classeLink} onClick={fecharMenu}>
        <LogIn size={18} />
        Entrar
      </NavLink>
      <Link to="/catalogo" className="botao-destaque cabecalho-cta" onClick={fecharMenu}>
        Pedir minha muda
      </Link>
    </>
  );

  return (
    <header className={isAdmin ? "cabecalho cabecalho-admin" : "cabecalho"}>
      <nav className="container-pagina cabecalho-barra" aria-label="Principal">
        <Link to={isAdmin ? "/catalogo" : "/"} className="cabecalho-marca" onClick={fecharMenu}>
          <span className="cabecalho-marca-icone">
            <Sprout size={20} strokeWidth={2.4} />
          </span>
          <span className="cabecalho-marca-texto">{SITE.nome}</span>
        </Link>

        <div className="cabecalho-links">
          {links.map((link) => (
            <NavLink key={link.para} to={link.para} end={link.fim} className={classeLink}>
              {link.rotulo}
            </NavLink>
          ))}
        </div>

        <div className="cabecalho-conta">{acoesConta}</div>

        <button
          type="button"
          className="cabecalho-botao-menu"
          onClick={() => setMenuAberto(!menuAberto)}
          aria-label={menuAberto ? "Fechar menu" : "Abrir menu"}
          aria-expanded={menuAberto}
        >
          {menuAberto ? <X size={24} /> : <Menu size={24} />}
        </button>
      </nav>

      {menuAberto && (
        <div className="cabecalho-menu-movel">
          <div className="container-pagina cabecalho-menu-movel-conteudo">
            {links.map((link) => (
              <NavLink
                key={link.para}
                to={link.para}
                end={link.fim}
                className={classeLink}
                onClick={fecharMenu}
              >
                {link.rotulo}
              </NavLink>
            ))}
            <div className="cabecalho-menu-movel-conta">{acoesConta}</div>
          </div>
        </div>
      )}
    </header>
  );
}

export default Cabecalho;
