import { useEffect, useState } from "react";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import type { MudaFilter } from "../../types/MudaFilter";
import { listarMudas } from "../../services/mudaService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import FiltrosCatalogo from "../../components/FiltrosCatalogo/FiltrosCatalogo";
import CardMuda from "../../components/CardMuda/CardMuda";
import "./CatalogoMudas.css";

function CatalogoMudas() {
  const [mudas, setMudas] = useState<DadosMudaResumo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState<MudaFilter>({});
  const [idsNaSolicitacao, setIdsNaSolicitacao] = useState<string[]>([]);

  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    setCarregando(true);
    listarMudas(filtro)
      .then(setMudas)
      .finally(() => setCarregando(false));
  }, [filtro]);

  function alternarSolicitacao(muda: DadosMudaResumo) {
    setIdsNaSolicitacao((idsAtuais) =>
      idsAtuais.includes(muda.id)
        ? idsAtuais.filter((id) => id !== muda.id)
        : [...idsAtuais, muda.id]
    );
  }

  function handleAdicionarEstoque(muda: DadosMudaResumo) {
    console.log("Adicionar estoque:", muda.id);
  }

  function handleRemoverEstoque(muda: DadosMudaResumo) {
    console.log("Remover estoque:", muda.id);
  }

  function handleEditar(muda: DadosMudaResumo) {
    console.log("Editar:", muda.id);
  }

  return (
    <div>
      <Cabecalho />

      <div className="boas-vindas">
        <p>Bem-vindo ao Sistema Horto!</p>
        <label className="alternar-admin">
          <input
            type="checkbox"
            checked={isAdmin}
            onChange={(evento) => setIsAdmin(evento.target.checked)}
          />
          Modo administrador (provisório)
        </label>
      </div>

      <main className="container-catalogo">
        <h2 className="titulo-catalogo">Catálogo de Mudas</h2>

        <FiltrosCatalogo
          filtro={filtro}
          aoMudarFiltro={setFiltro}
          podeCadastrarMuda={isAdmin}
          aoClicarNovaMuda={() => console.log("Abrir cadastro de nova muda")}
        />

        {carregando && <p className="mensagem-central">Carregando mudas...</p>}

        {!carregando && mudas.length === 0 && (
          <p className="mensagem-central">Nenhuma muda encontrada com esses filtros.</p>
        )}

        <div className="grid-mudas">
          {mudas.map((muda) => (
            <CardMuda
              key={muda.id}
              muda={muda}
              estaNaSolicitacao={idsNaSolicitacao.includes(muda.id)}
              aoAlternarSolicitacao={alternarSolicitacao}
              isAdmin={isAdmin}
              aoAdicionarEstoque={handleAdicionarEstoque}
              aoRemoverEstoque={handleRemoverEstoque}
              aoEditar={handleEditar}
            />
          ))}
        </div>
      </main>

      <Rodape />
    </div>
  );
}

export default CatalogoMudas;