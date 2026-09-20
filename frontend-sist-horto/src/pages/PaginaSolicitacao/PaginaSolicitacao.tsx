import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { History, ShoppingBag, Trash2 } from "lucide-react";
import { isAxiosError } from "axios";
import type { Solicitacao } from "../../types/Solicitacao";
import type { ParametroAnualDisponivel } from "../../types/ParametroAnualDisponivel";
import type { ParametroAnual } from "../../types/ParametroAnual";
import {
  buscarRascunho,
  buscarSaldoAtual,
  enviarSolicitacao,
  atualizarQuantidadeItemRascunho,
  removerItemRascunho,
} from "../../api/solicitacaoService";
import { buscarParametroAnual } from "../../api/parametroAnualService";
import { CategoriaMuda, rotuloCategoria } from "../../types/CategoriaMuda";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import CardSaldoParametro from "../../components/CardSaldoParametro/CardSaldoParametro";
import SeletorQuantidade from "../../components/SeletorQuantidade/SeletorQuantidade";
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
  const [solicitacao, setSolicitacao] = useState<Solicitacao | null>(null);
  const [saldo, setSaldo] = useState<ParametroAnualDisponivel | null>(null);
  const [limitesAno, setLimitesAno] = useState<ParametroAnual | null>(null);

  const [carregando, setCarregando] = useState(true);
  const [carregandoSaldo, setCarregandoSaldo] = useState(true);
  const [enviando, setEnviando] = useState(false);
  const [itemProcessando, setItemProcessando] = useState<number | null>(null);
  const [erro, setErro] = useState("");
  const [enviadaComSucesso, setEnviadaComSucesso] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    carregarDados();
  }, []);

  useEffect(() => {
    if (!solicitacao) {
      return;
    }

    const ids = solicitacao.itens.map((item) => item.id);
    const idsUnicos = new Set(ids);

    if (idsUnicos.size !== ids.length) {
      console.warn(
        "Atenção: a lista de itens da solicitação veio com ids duplicados ou repetidos.",
        ids
      );
    }
  }, [solicitacao]);

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
      const solicitacaoAtualizada = await enviarSolicitacao(solicitacao.id);
      setSolicitacao(solicitacaoAtualizada);
      setEnviadaComSucesso(true);
    } catch (erro) {
      setErro(extrairMensagemErro(erro));
    } finally {
      setEnviando(false);
    }
  }

  const quantidadeTotalItens =
    solicitacao?.itens.reduce((total, item) => total + item.quantidade, 0) ?? 0;

  return (
    <div>
      <Cabecalho />

      <main className="container-solicitacao">
        <div className="solicitacao-cabecalho">
          <h2 className="titulo-solicitacao">
            <ShoppingBag size={22} />
            Minha Solicitação
          </h2>

          <button
            type="button"
            className="botao-historico"
            onClick={() => navigate("/solicitacoes/historico")}
          >
            <History size={16} />
            Histórico de solicitações
          </button>
        </div>

        <CardSaldoParametro saldo={saldo} carregando={carregandoSaldo} />

        {erro && <p className="solicitacao-erro">{erro}</p>}

        {enviadaComSucesso && (
          <p className="solicitacao-sucesso">
            Solicitação enviada com sucesso! Ela agora está com status "Pendente" para análise.
          </p>
        )}

        {carregando && <p className="mensagem-central">Carregando sua solicitação...</p>}

        {!carregando && solicitacao && solicitacao.itens.length === 0 && (
          <p className="mensagem-central">
            Sua solicitação ainda está vazia. Volte ao catálogo para escolher mudas.
          </p>
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

                  {!enviadaComSucesso && (
                    <SeletorQuantidade
                      valor={item.quantidade}
                      aoAlterar={(novaQuantidade) =>
                        handleAlterarQuantidade(item.id, novaQuantidade)
                      }
                      minimo={1}
                      desabilitado={processandoEsteItem}
                    />
                  )}

                  {enviadaComSucesso && (
                    <div className="item-solicitacao-quantidade">
                      <span>Quantidade</span>
                      <strong>{item.quantidade}</strong>
                    </div>
                  )}

                  {!enviadaComSucesso && (
                    <button
                      type="button"
                      className="botao-remover-item"
                      onClick={() => handleRemoverItem(item.id, nomeMuda)}
                      disabled={processandoEsteItem}
                      aria-label={`Remover ${nomeMuda}`}
                    >
                      <Trash2 size={16} />
                    </button>
                  )}
                </div>
              );
            })}

            <div className="solicitacao-total">
              <span>Total de mudas na solicitação</span>
              <strong>{quantidadeTotalItens}</strong>
            </div>
          </div>
        )}

        {!carregando && solicitacao && solicitacao.itens.length > 0 && !enviadaComSucesso && (
          <div className="solicitacao-acoes">
            <button
              type="button"
              className="botao-continuar"
              onClick={() => navigate("/")}
            >
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
      </main>

      <Rodape />
    </div>
  );
}

export default PaginaSolicitacao;