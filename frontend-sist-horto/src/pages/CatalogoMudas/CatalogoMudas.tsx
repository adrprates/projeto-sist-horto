import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Plus } from "lucide-react";
import { useAuth } from "../../hooks/useAuth";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import type { MudaFilter } from "../../types/MudaFilter";
import type { ParametroAnualDisponivel } from "../../types/ParametroAnualDisponivel";
import type { SolicitacaoBeneficiarioResumo } from "../../types/SolicitacaoBeneficiarioResumo";
import { StatusSolicitacao } from "../../types/StatusSolicitacao";
import { alterarDisponibilidadeMuda, listarMudas } from "../../api/mudaService";
import { useValorAtrasado } from "../../hooks/useValorAtrasado";
import { adicionarQuantidade, removerQuantidade } from "../../api/estoqueService";
import {
  buscarRascunho,
  adicionarItemRascunho,
  buscarSaldoAtual,
  buscarSolicitacaoAtual,
} from "../../api/solicitacaoService";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
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
  const filtroAtrasado = useValorAtrasado(filtro);
  const [versaoLista, setVersaoLista] = useState(0);

  const [saldo, setSaldo] = useState<ParametroAnualDisponivel | null>(null);
  const [carregandoSaldo, setCarregandoSaldo] = useState(podeSolicitar);

  const [solicitacaoExistente, setSolicitacaoExistente] =
    useState<SolicitacaoBeneficiarioResumo | null>(null);
  const [quantidadesNaSolicitacao, setQuantidadesNaSolicitacao] = useState<Record<number, number>>(
    {}
  );

  useEffect(() => {
    setCarregando(true);
    listarMudas(filtroAtrasado)
      .then((resultado) => {
        setMudas(resultado);
        setVersaoLista((versao) => versao + 1);
      })
      .finally(() => setCarregando(false));
  }, [filtroAtrasado]);

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

  async function handleAlterarDisponibilidade(
    muda: DadosMudaResumo,
    disponivel: boolean,
    motivo?: string
  ) {
    await alterarDisponibilidadeMuda(muda.id, disponivel, motivo);
    const mudasAtualizadas = await listarMudas(filtro);
    setMudas(mudasAtualizadas);
  }

  return (
    <div>
      <CabecalhoPagina
        compacto={isAdmin}
        titulo={isAdmin ? "Gestão de mudas" : "Catálogo de mudas"}
        texto={
          isAdmin
            ? "Cadastre novas espécies, edite as existentes e controle o estoque do viveiro."
            : "Espécies produzidas no Horto Florestal de Patrocínio. A disponibilidade muda conforme o estoque do viveiro."
        }
        acoes={
          isAdmin && (
            <button type="button" className="botao-destaque" onClick={() => navigate("/mudas/nova")}>
              <Plus size={18} />
              Nova muda
            </button>
          )
        }
      />

      <section className="container-pagina container-catalogo">
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
        />

        {carregando && versaoLista === 0 && <p className="mensagem-central">Carregando mudas...</p>}

        {!carregando && mudas.length === 0 && (
          <p className="mensagem-central surgir">Nenhuma muda encontrada com esses filtros.</p>
        )}

        <div
          key={versaoLista}
          className={`grid-mudas lista-animada conteudo-atualizavel${carregando ? " atualizando" : ""}`}
        >
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
              aoAlterarDisponibilidade={handleAlterarDisponibilidade}
            />
          ))}
        </div>

        <div className="catalogo-chamada">
          <div>
            <h2 className="catalogo-chamada-titulo">Já escolheu?</h2>
            <p className="catalogo-chamada-texto">
              {podeSolicitar
                ? "Revise os itens e finalize sua solicitação em poucos minutos."
                : "Entre no sistema ou crie sua conta para montar a sua solicitação."}
            </p>
          </div>
          {podeSolicitar ? (
            <Link to="/solicitacao" className="botao-destaque">
              Ver minha solicitação
            </Link>
          ) : (
            <div className="catalogo-chamada-acoes">
              <Link to="/registro" className="botao-contorno-claro">
                Criar conta
              </Link>
              <Link to="/login" className="botao-destaque">
                Entrar
              </Link>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}