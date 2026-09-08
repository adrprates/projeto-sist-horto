import { useAuth } from "../../hooks/useAuth";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import type { MudaFilter } from "../../types/MudaFilter";
import { listarMudas } from "../../api/mudaService";
import { adicionarQuantidade, removerQuantidade } from "../../api/estoqueService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import FiltrosCatalogo from "../../components/FiltrosCatalogo/FiltrosCatalogo";
import CardMuda from "../../components/CardMuda/CardMuda";
import "./CatalogoMudas.css";

export default function CatalogoMudas() {
  const navigate = useNavigate();
  const [mudas, setMudas] = useState<DadosMudaResumo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState<MudaFilter>({});
  const [idsNaSolicitacao, setIdsNaSolicitacao] = useState<number[]>([]);
  const { isAuthenticated, hasRole } = useAuth();
  const isAdmin = hasRole(["ADMINISTRADOR"]);
  const isBeneficiario = hasRole(["BENEFICIARIO"]);
  const podeSolicitar =
  isAuthenticated &&
  (isBeneficiario || isAdmin);

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
      </div>

      <main className="container-catalogo">
        <h2 className="titulo-catalogo">Catálogo de Mudas</h2>

        <FiltrosCatalogo
          filtro={filtro}
          aoMudarFiltro={setFiltro}
          podeCadastrarMuda={isAdmin}
          aoClicarNovaMuda={() => navigate("/mudas/nova")}
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
              podeSolicitar={podeSolicitar}
              aoAdicionarEstoque={handleAdicionarEstoque}
              aoRemoverEstoque={handleRemoverEstoque}
              aoGerenciar={(mudaSelecionada) =>
                navigate(`/mudas/editar/${mudaSelecionada.id}`)
  }
            />
          ))}
        </div>
      </main>

      <Rodape />
    </div>
  );
}