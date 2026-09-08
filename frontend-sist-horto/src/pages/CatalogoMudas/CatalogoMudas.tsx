import { useEffect, useState } from "react";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import type { MudaFilter } from "../../types/MudaFilter";
import { listarMudas } from "../../services/mudaService";
import { adicionarQuantidade, removerQuantidade } from "../../services/estoqueService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import FiltrosCatalogo from "../../components/FiltrosCatalogo/FiltrosCatalogo";
import CardMuda from "../../components/CardMuda/CardMuda";
import "./CatalogoMudas.css";

interface CatalogoMudasProps {
  aoAbrirNovaMuda: () => void;
  aoAbrirEdicaoMuda: (id: number) => void;
}

function CatalogoMudas({ aoAbrirNovaMuda, aoAbrirEdicaoMuda }: CatalogoMudasProps) {
  const [mudas, setMudas] = useState<DadosMudaResumo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState<MudaFilter>({});
  const [idsNaSolicitacao, setIdsNaSolicitacao] = useState<number[]>([]);

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

async function handleAdicionarEstoque(
  muda: DadosMudaResumo,
  quantidade: number
) {
  await adicionarQuantidade(muda.id, quantidade);

  const mudasAtualizadas = await listarMudas(filtro);
  setMudas(mudasAtualizadas);
}

async function handleRemoverEstoque(
  muda: DadosMudaResumo,
  quantidade: number
) {
  await removerQuantidade(muda.id, quantidade);

  const mudasAtualizadas = await listarMudas(filtro);
  setMudas(mudasAtualizadas);
}

  return (
    <div>
      <Cabecalho />

      <div className="boas-vindas-compacta">
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
          aoClicarNovaMuda={aoAbrirNovaMuda}
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
              aoGerenciar={(mudaSelecionada) => aoAbrirEdicaoMuda(mudaSelecionada.id)}
            />
          ))}
        </div>
      </main>

      <Rodape />
    </div>
  );
}

export default CatalogoMudas;