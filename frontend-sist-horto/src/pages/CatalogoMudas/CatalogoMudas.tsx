import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import type { MudaFilter } from "../../types/MudaFilter";
import type { ParametroAnualDisponivel } from "../../types/ParametroAnualDisponivel";
import type { SolicitacaoBeneficiarioResumo } from "../../types/SolicitacaoBeneficiarioResumo";
import { StatusSolicitacao } from "../../types/StatusSolicitacao";
import { listarMudas } from "../../api/mudaService";
import { adicionarQuantidade, removerQuantidade } from "../../api/estoqueService";
import {
  buscarRascunho,
  adicionarItemRascunho,
  buscarSaldoAtual,
  buscarSolicitacaoAtual,
} from "../../api/solicitacaoService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import FiltrosCatalogo from "../../components/FiltrosCatalogo/FiltrosCatalogo";
import CardMuda from "../../components/CardMuda/CardMuda";
import CardSaldoParametro from "../../components/CardSaldoParametro/CardSaldoParametro";
import AvisoAutenticacaoNecessaria from "../../components/AvisoAutenticacaoNecessaria/AvisoAutenticacaoNecessaria";
import AvisoSolicitacaoExistente from "../../components/AvisoSolicitacaoExistente/AvisoSolicitacaoExistente";

import "./CatalogoMudas.css";

export default function CatalogoMudas() {
  const navigate = useNavigate();
  const { isAuthenticated, hasRole } = useAuth();

  const isAdmin = hasRole(["ADMINISTRADOR"]);
  const isBeneficiario = hasRole(["BENEFICIARIO"]);
  const podeSolicitar = isAuthenticated && (isBeneficiario || isAdmin);

  const [mudas, setMudas] = useState<DadosMudaResumo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState<MudaFilter>({});

  const [saldo, setSaldo] = useState<ParametroAnualDisponivel | null>(null);
  const [carregandoSaldo, setCarregandoSaldo] = useState(podeSolicitar);

  const [solicitacaoExistente, setSolicitacaoExistente] =
    useState<SolicitacaoBeneficiarioResumo | null>(null);
  const [quantidadesNaSolicitacao, setQuantidadesNaSolicitacao] = useState<Record<number, number>>(
    {}
  );

  useEffect(() => {
    setCarregando(true);
    listarMudas(filtro)
      .then(setMudas)
      .finally(() => setCarregando(false));
  }, [filtro]);

  useEffect(() => {
    if (!podeSolicitar) return;

    verificarSolicitacaoExistente();
  }, [podeSolicitar]);

  function verificarSolicitacaoExistente() {
    buscarSolicitacaoAtual()
      .then((solicitacao) => {
        if (!solicitacao) {
          setSolicitacaoExistente(null);
          carregarSaldoEQuantidades();
          return;
        }

        const jaEnviada = solicitacao.statusAtual !== StatusSolicitacao.RASCUNHO;

        if (jaEnviada) {
          setSolicitacaoExistente({
            id: solicitacao.id,
            ano: solicitacao.parametroAnual.ano,
            statusAtual: solicitacao.statusAtual,
            dataSolicitacao: solicitacao.dataSolicitacao,
          });
          setCarregandoSaldo(false);
        } else {
          setSolicitacaoExistente(null);
          carregarSaldoEQuantidades();
        }
      })
      .catch(() => {
        setSolicitacaoExistente(null);
        carregarSaldoEQuantidades();
      });
  }

  function carregarSaldoEQuantidades() {
    setCarregandoSaldo(true);
    buscarSaldoAtual()
      .then(setSaldo)
      .catch(() => setSaldo(null))
      .finally(() => setCarregandoSaldo(false));

    buscarRascunho()
      .then((solicitacao) => {
        const mapa: Record<number, number> = {};
        solicitacao.itens.forEach((item) => {
          mapa[item.muda.id] = item.quantidade;
        });
        setQuantidadesNaSolicitacao(mapa);
      })
      .catch(() => setQuantidadesNaSolicitacao({}));
  }

  async function handleAdicionarSolicitacao(muda: DadosMudaResumo, quantidade: number) {
    await adicionarItemRascunho({ mudaId: muda.id, quantidade });
    carregarSaldoEQuantidades();
  }

  async function handleAdicionarEstoque(muda: DadosMudaResumo, quantidade: number) {
    await adicionarQuantidade(muda.id, quantidade);
    const mudasAtualizadas = await listarMudas(filtro);
    setMudas(mudasAtualizadas);
  }

  async function handleRemoverEstoque(muda: DadosMudaResumo, quantidade: number) {
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

        {!podeSolicitar && (
          <AvisoAutenticacaoNecessaria mensagem="Para fazer o pedido de mudas, é necessário fazer login no sistema." />
        )}

        {podeSolicitar && solicitacaoExistente && (
          <AvisoSolicitacaoExistente solicitacao={solicitacaoExistente} />
        )}

        {podeSolicitar && !solicitacaoExistente && (
          <CardSaldoParametro saldo={saldo} carregando={carregandoSaldo} />
        )}

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
              isAdmin={isAdmin}
              podeSolicitar={podeSolicitar}
              solicitacaoBloqueada={solicitacaoExistente !== null}
              quantidadeJaSolicitada={quantidadesNaSolicitacao[muda.id] ?? 0}
              aoAdicionarSolicitacao={handleAdicionarSolicitacao}
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