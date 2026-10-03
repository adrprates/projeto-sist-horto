import { useEffect } from "react";
import { Outlet, useLocation } from "react-router-dom";
import Cabecalho from "../Cabecalho/Cabecalho";
import Rodape from "../Rodape/Rodape";

function RolarParaTopo() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo({ top: 0, left: 0, behavior: "instant" });
  }, [pathname]);

  return null;
}

function Layout() {
  return (
    <div className="layout-aplicacao">
      <RolarParaTopo />
      <Cabecalho />
      <main className="layout-conteudo">
        <Outlet />
      </main>
      <Rodape />
    </div>
  );
}

export default Layout;
