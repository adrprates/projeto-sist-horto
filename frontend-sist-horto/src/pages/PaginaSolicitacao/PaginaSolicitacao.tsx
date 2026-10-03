import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CheckCircle2, Trash2, XCircle } from "lucide-react";
import { isAxiosError } from "axios";
import type { Solicitacao } from "../../types/Solicitacao";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import type { SolicitacaoBeneficiarioResumo } from "../../types/SolicitacaoBeneficiarioResumo";
import type { ParametroAnualDisponivel } from "../../types/ParametroAnualDisponivel";
import type { ParametroAnual } from "../../types/ParametroAnual";
import { StatusSolicitacao } from "../../types/StatusSolicitacao";
import {
  buscarSolicitacaoAtual,
  buscarRascunho,
  buscarSaldoAtual,
  enviarSolicitacao,
  atualizarQuantidadeItemRascunho,
  removerItemRascunho,
  listarMinhasSolicitacoes,
  responderConfirmacao,
} from "../../api/solicitacaoService";
import { buscarParametroAnual } from "../../api/parametroAnualService";
import { CategoriaMuda, rotuloCategoria } from "../../types/CategoriaMuda";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import CardSaldoParametro from "../../components/CardSaldoParametro/CardSaldoParametro";
import SeletorQuantidade from "../../components/SeletorQuantidade/SeletorQuantidade";
import DetalhesSolicitacaoCartao from "../../components/DetalhesSolicitacaoCartao/DetalhesSolicitacaoCartao";
import ListaSolicitacoesAnteriores from "../../components/ListaSolicitacoesAnteriores/ListaSolicitacoesAnteriores";
import "./PaginaSolicitacao.css";

const MENSAGEM_PADRAO = "Ocorreu um erro. Tente novamente.";

function extrairMensagemErro(erro: unknown): string {
  if (!isAxiosError(erro)) {
    return MENSAGEM_PADRAO;
  }

  const dados = erro.response?.data;

  if (typeof dados === "string" && dados.trim().length > 0) {
    return dados;
  }

  if (dados && typeof dados === "object") {
    const possivelMensagem =
      (dados as Record<string, unknown>).message ??
      (dados as Record<string, unknown>).erro ??
      (dados as Record<string, unknown>).error;

    if (typeof possivelMensagem === "string" && possivelMensagem.trim().length > 0) {
      return possivelMensagem;
    }
  }

  return MENSAGEM_PADRAO;
}

function PaginaSolicitacao() {
  const [verificando, setVerificando] = useState(true);
  const [solicitacaoEnviada, setSolicitacaoEnviada] = useState<SolicitacaoAdmin | null>(null);

  const [solicitacao, setSolicitacao] = useState<Solicitacao | null>(null);
  const [saldo, setSaldo] = useState<ParametroAnualDisponivel | null>(null);
  const [limitesAno, setLimitesAno] = useState<ParametroAnual | null>(null);

  const [historico, setHistorico] = useState<SolicitacaoBeneficiarioResumo[]>([]);
  const [carregandoHistorico, setCarregandoHistorico] = useState(true);

  const [carregando, setCarregando] = useState(true);
  const [carregandoSaldo, setCarregandoSaldo] = useState(true);
  const [enviando, setEnviando] = useState(false);
  const [itemProcessando, setItemProcessando] = useState<number | null>(null);
  const [erro, setErro] = useState("");
  const [enviadaComSucesso, setEnviadaComSucesso] = useState(false);

  const [respondendo, setRespondendo] = useState(false);
  const [erroResposta, setErroResposta] = useState("");

  const navigate = useNavigate();

  useEffect(() => {
    verificarESeguirFluxo();
    carregarHistorico();
  }, []);

  function verificarESeguirFluxo() {
    setVerificando(true);

    buscarSolicitacaoAtual()
      .then((atual) => {
        const jaEnviada = atual !== null && atual.statusAtual !== StatusSolicitacao.RASCUNHO;
        setSolicitacaoEnviada(jaEnviada ? atual : null);

        if (!jaEnviada) {
          carregarDados();
        }
      })
      .catch(() => {
        setSolicitacaoEnviada(null);
        carregarDados();
      })
      .finally(() => setVerificando(false));
  }

  function carregarHistorico() {
    setCarregandoHistorico(true);
    listarMinhasSolicitacoes()
      .then(setHistorico)
      .catch(() => setHistorico([]))
      .finally(() => setCarregandoHistorico(false));
  }

  function carregarDados() {
    setCarregando(true);
    setCarregandoSaldo(true);

    buscarRascunho()
      .then(setSolicitacao)
      .catch(() => setErro("Não foi possível carregar sua solicitação."))
      .finally(() => setCarregando(false));

    buscarSaldoAtual()
      .then(setSaldo)
      .catch(() => setSaldo(null))
      .finally(() => setCarregandoSaldo(false));

    const anoAtual = new Date().getFullYear();
    buscarParametroAnual(anoAtual)
      .then(setLimitesAno)
      .catch(() => setLimitesAno(null));
  }

  function recarregarSaldo() {
    buscarSaldoAtual()
      .then(setSaldo)
      .catch(() => setSaldo(null));
  }

  function maxPorEspecie(categoria: CategoriaMuda): number | null {
    if (!limitesAno) {
      return null;
    }
    return categoria === CategoriaMuda.FRUTIFERAS
      ? limitesAno.maxPorEspecieFrutifera
      : limitesAno.maxPorEspecieOutras;
  }

  async function handleAlterarQuantidade(itemId: number, novaQuantidade: number) {
    setErro("");
    setItemProcessando(itemId);

    try {
      const solicitacaoAtualizada = await atualizarQuantidadeItemRascunho(itemId, novaQuantidade);
      setSolicitacao(solicitacaoAtualizada);
      recarregarSaldo();
    } catch (erro) {
      setErro(extrairMensagemErro(erro));
    } finally {
      setItemProcessando(null);
    }
  }

  async function handleRemoverItem(itemId: number, nomeMuda: string) {
    const confirmado = window.confirm(`Remover "${nomeMuda}" da sua solicitação?`);

    if (!confirmado) {
      return;
    }

    setErro("");
    setItemProcessando(itemId);

    try {
      const solicitacaoAtualizada = await removerItemRascunho(itemId);
      setSolicitacao(solicitacaoAtualizada);
      recarregarSaldo();
    } catch (erro) {
      setErro(extrairMensagemErro(erro));
    } finally {
      setItemProcessando(null);
    }
  }

  async function handleFinalizarSolicitacao() {
    if (!solicitacao) {
      return;
    }

    setErro("");
    setEnviando(true);

    try {
      await enviarSolicitacao(solicitacao.id);
      setEnviadaComSucesso(true);
      verificarESeguirFluxo();
      carregarHistorico();
    } catch (erro) {
      setErro(extrairMensagemErro(erro));
    } finally {
      setEnviando(false);
    }
  }

  async function handleResponderConfirmacao(aceitar: boolean) {
    if (!solicitacaoEnviada) {
      return;
    }

    setErroResposta("");
    setRespondendo(true);

    try {
      const atualizada = await responderConfirmacao(solicitacaoEnviada.id, aceitar);
      setSolicitacaoEnviada(atualizada);
      carregarHistorico();
    } catch (erro) {
      setErroResposta(extrairMensagemErro(erro));
    } finally {
      setRespondendo(false);
    }
  }

  const quantidadeTotalItens =
    solicitacao?.itens.reduce((total, item) => total + item.quantidade, 0) ?? 0;

  const precisaConfirmar =
    solicitacaoEnviada?.statusAtual === StatusSolicitacao.AGUARDANDO_CONFIRMACAO;

  const solicitacoesAnteriores = historico.filter(
    (item) =>
      item.statusAtual !== StatusSolicitacao.RASCUNHO && item.id !== solicitacaoEnviada?.id
  );

  function renderizarRascunho() {
    return (
      <>
        <CardSaldoParametro saldo={saldo} carregando={carregandoSaldo} />

        {erro && <p className="solicitacao-erro">{erro}</p>}

        {carregando && <p className="mensagem-central">Carregando sua solicitação...</p>}

        {!carregando && solicitacao && solicitacao.itens.length === 0 && (
          <div className="solicitacao-vazia">
            <p>Sua solicitação ainda está vazia. Volte ao catálogo para escolher mudas.</p>
            <button type="button" className="botao-continuar" onClick={() => navigate("/catalogo")}>
              Ir para o catálogo
            </button>
          </div>
        )}

        {!carregando && solicitacao && solicitacao.itens.length > 0 && (
          <div className="lista-itens-solicitacao">
            {solicitacao.itens.map((item) => {
              const nomeMuda = item.muda.nomesPopulares.slice(0, 3).join(", ");
              const processandoEsteItem = itemProcessando === item.id;
              const maximoEspecie = maxPorEspecie(item.muda.categoria);

              return (
                <div key={item.id} className="item-solicitacao">
                  <div className="item-solicitacao-imagem">
                    {item.muda.linkImagemArvore ? (
                      <img src={item.muda.linkImagemArvore} alt={nomeMuda} />
                    ) : (
                      <span role="img" aria-label={nomeMuda}>
                        🌱
                      </span>
                    )}
                  </div>

                  <div className="item-solicitacao-info">
                    <p className="item-solicitacao-nome">{nomeMuda}</p>
                    <span className="tag">{rotuloCategoria[item.muda.categoria]}</span>
                    {maximoEspecie !== null && (
                      <span className="item-solicitacao-limite-especie">
                        {item.quantidade} / {maximoEspecie} desta espécie
                      </span>
                    )}
                  </div>

                  <SeletorQuantidade
                    valor={item.quantidade}
                    aoAlterar={(novaQuantidade) =>
                      handleAlterarQuantidade(item.id, novaQuantidade)
                    }
                    minimo={1}
                    desabilitado={processandoEsteItem}
                  />

                  <button
                    type="button"
                    className="botao-remover-item"
                    onClick={() => handleRemoverItem(item.id, nomeMuda)}
                    disabled={processandoEsteItem}
                    aria-label={`Remover ${nomeMuda}`}
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              );
            })}

            <div className="solicitacao-total">
              <span>Total de mudas na solicitação</span>
              <strong>{quantidadeTotalItens}</strong>
            </div>
          </div>
        )}

        {!carregando && solicitacao && solicitacao.itens.length > 0 && (
          <div className="solicitacao-acoes">
            <button type="button" className="botao-continuar" onClick={() => navigate("/catalogo")}>
              Continuar adicionando mudas
            </button>
            <button
              type="button"
              className="botao-finalizar"
              onClick={handleFinalizarSolicitacao}
              disabled={enviando}
            >
              {enviando ? "Enviando..." : "Finalizar Solicitação"}
            </button>
          </div>
        )}
      </>
    );
  }

  function renderizarSolicitacaoEnviada(enviada: SolicitacaoAdmin) {
    return (
      <>
        <DetalhesSolicitacaoCartao solicitacao={enviada} />

        {precisaConfirmar && (
          <div className="painel-confirmacao">
            <p className="painel-confirmacao-titulo">
              O administrador propôs uma alteração na sua solicitação. Veja a descrição da etapa
              mais recente acima e decida se aceita ou não.
            </p>

            {erroResposta && <p className="painel-confirmacao-erro">{erroResposta}</p>}

            <div className="painel-confirmacao-acoes">
              <button
                type="button"
                className="botao-recusar-confirmacao"
                onClick={() => handleResponderConfirmacao(false)}
                disabled={respondendo}
              >
                <XCircle size={16} />
                Recusar
              </button>
              <button
                type="button"
                className="botao-aceitar-confirmacao"
                onClick={() => handleResponderConfirmacao(true)}
                disabled={respondendo}
              >
                <CheckCircle2 size={16} />
                {respondendo ? "Enviando..." : "Aceitar alteração"}
              </button>
            </div>
          </div>
        )}
      </>
    );
  }

  return (
    <div>

      <CabecalhoPagina
        compacto
        titulo="Minhas solicitações"
        texto="Monte a solicitação deste ano, acompanhe o andamento e consulte os pedidos anteriores."
      />

      <section className="container-pagina container-solicitacao">

        {enviadaComSucesso && (
          <p className="solicitacao-sucesso">
            Solicitação enviada com sucesso! Ela agora está com status "Pendente" para análise.
          </p>
        )}

        <section className="solicitacao-secao">
          <h3 className="solicitacao-subtitulo">
            {solicitacaoEnviada ? "Solicitação deste ano" : "Solicitação em construção"}
          </h3>

          {verificando && <p className="mensagem-central">Verificando sua solicitação...</p>}

          {!verificando && solicitacaoEnviada && renderizarSolicitacaoEnviada(solicitacaoEnviada)}

          {!verificando && !solicitacaoEnviada && renderizarRascunho()}
        </section>

        <section className="solicitacao-secao">
          <h3 className="solicitacao-subtitulo">Solicitações anteriores</h3>
          <ListaSolicitacoesAnteriores
            solicitacoes={solicitacoesAnteriores}
            carregando={carregandoHistorico}
          />
        </section>
      </section>

    </div>
  );
}

export default PaginaSolicitacao;
