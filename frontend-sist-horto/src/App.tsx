import { useEffect, useState } from "react";
import type { DadosMuda } from "./types/DadosMuda";
import { listarMudas } from "./services/mudaService";
import CardMuda from "./components/CardMuda";
import "./App.css";

function App() {
  const [mudas, setMudas] = useState<DadosMuda[]>([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    listarMudas()
      .then(setMudas)
      .finally(() => setCarregando(false));
  }, []);

  function handleVerDetalhes(muda: DadosMuda) {
    console.log("Detalhes de:", muda.nomePopular);
  }

  return (
    <div>
      <header className="cabecalho">
        <h1 className="titulo">Catálogo de Mudas</h1>
      </header>

      <main className="container">
        {carregando && (
          <p className="centro cinza">Carregando mudas...</p>
        )}

        {!carregando && mudas.length === 0 && (
          <p className="centro cinza">
            Nenhuma muda encontrada por enquanto.
          </p>
        )}

        <div className="grid">
          {mudas.map((muda) => (
            <CardMuda
              key={muda.nomeCanonico}
              muda={muda}
              aoVerDetalhes={handleVerDetalhes}
            />
          ))}
        </div>
      </main>
    </div>
  );
}

export default App;