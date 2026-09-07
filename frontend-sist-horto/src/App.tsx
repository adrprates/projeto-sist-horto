import { useState } from "react";
import CatalogoMudas from "./pages/CatalogoMudas/CatalogoMudas";
import FormularioMuda from "./pages/FormularioMuda/FormularioMuda";
import "./App.css";

type Tela = "catalogo" | "formulario";

function App() {
  const [tela, setTela] = useState<Tela>("catalogo");
  const [idMudaEmEdicao, setIdMudaEmEdicao] = useState<number | undefined>(undefined);

  function abrirNovaMuda() {
    setIdMudaEmEdicao(undefined);
    setTela("formulario");
  }

  function abrirEdicaoMuda(id: number) {
    setIdMudaEmEdicao(id);
    setTela("formulario");
  }

  function voltarParaCatalogo() {
    setTela("catalogo");
  }

  return (
    <div>
      {tela === "catalogo" && (
        <CatalogoMudas
          aoAbrirNovaMuda={abrirNovaMuda}
          aoAbrirEdicaoMuda={abrirEdicaoMuda}
        />
      )}

      {tela === "formulario" && (
        <FormularioMuda
          idMudaEdicao={idMudaEmEdicao}
          aoVoltar={voltarParaCatalogo}
          aoSalvarComSucesso={voltarParaCatalogo}
        />
      )}
    </div>
  );
}

export default App;