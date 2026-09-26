import { useEffect, useState } from "react";
import { CheckCircle2, XCircle } from "lucide-react";
import { isAxiosError } from "axios";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import type { SolicitacaoBeneficiarioResumo } from "../../types/SolicitacaoBeneficiarioResumo";
import { StatusSolicitacao } from "../../types/StatusSolicitacao";
import {
  buscarSolicitacaoAtual,
  buscarMinhaSolicitacaoDetalhada,
  listarMinhasSolicitacoes,
  responderConfirmacao,
} from "../../api/solicitacaoService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import BadgeStatusSolicitacao from "../../components/BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import DetalhesSolicitacaoCartao from "../../components/DetalhesSolicitacaoCartao/DetalhesSolicitacaoCartao";
import "./HistoricoSolicitacoes.css";

const MENSAGEM_PADRAO = "Não foi possível responder à confirmação da solicitação.";

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

type DetalheCarregado = "carregando" | "erro" | SolicitacaoAdmin;

function HistoricoSolicitacoes() {
  const [solicitacaoAtual, setSolicitacaoAtual] = useState<SolicitacaoAdmin | null>(null);
  const [carregandoAtual, setCarregandoAtual] = useState(true);

  const [historico, setHistorico] = useState<SolicitacaoBeneficiarioResumo[]>([]);
  const [carregandoHistorico, setCarregandoHistorico] = useState(true);

  const [detalhesExpandidos, setDetalhesExpandidos] = useState<Record<number, DetalheCarregado>>(
    {}
  );

  const [respondendo, setRespondendo] = useState(false);
  const [erroResposta, setErroResposta] = useState("");

  useEffect(() => {
    carregarSolicitacaoAtual();
    carregarHistorico();
  }, []);

  function carregarSolicitacaoAtual() {
    setCarregandoAtual(true);
    buscarSolicitacaoAtual()
      .then(setSolicitacaoAtual)
      .catch(() => setSolicitacaoAtual(null))
      .finally(() => setCarregandoAtual(false));
  }

  function carregarHistorico() {
    setCarregandoHistorico(true);
    listarMinhasSolicitacoes()
      .then(setHistorico)
      .finally(() => setCarregandoHistorico(false));
  }

  function toggleDetalhes(id: number) {
    setDetalhesExpandidos((atual) => {
      if (atual[id] !== undefined) {
        const copia = { ...atual };
        delete copia[id];
        return copia;
      }
      return { ...atual, [id]: "carregando" };
    });

    if (detalhesExpandidos[id] === undefined) {
      buscarMinhaSolicitacaoDetalhada(id)
        .then((dados) => {
          setDetalhesExpandidos((atual) => ({ ...atual, [id]: dados }));
        })
        .catch(() => {
          setDetalhesExpandidos((atual) => ({ ...atual, [id]: "erro" }));
        });
    }
  }

  async function handleResponderConfirmacao(aceitar: boolean) {
    if (!solicitacaoAtual) {
      return;
    }

    setErroResposta("");
    setRespondendo(true);

    try {
      const atualizada = await responderConfirmacao(solicitacaoAtual.id, aceitar);
      setSolicitacaoAtual(atualizada);
      carregarHistorico();
    } catch (erro) {
      setErroResposta(extrairMensagemErro(erro));
    } finally {
      setRespondendo(false);
    }
  }

  const precisaConfirmar =
    solicitacaoAtual?.statusAtual === StatusSolicitacao.AGUARDANDO_CONFIRMACAO;

  return (
    <div>
      <Cabecalho />

      <main className="container-historico">
        <h2 className="titulo-historico">Minhas Solicitações</h2>

        <section className="historico-secao">
          <h3 className="historico-subtitulo">Solicitação deste ano</h3>

          {carregandoAtual && <p className="mensagem-central">Carregando...</p>}

          {!carregandoAtual && !solicitacaoAtual && (
            <p className="mensagem-central">
              Você ainda não fez nenhuma solicitação de mudas este ano.
            </p>
          )}

          {!carregandoAtual && solicitacaoAtual && (
            <>
              <DetalhesSolicitacaoCartao solicitacao={solicitacaoAtual} />

              {precisaConfirmar && (
                <div className="painel-confirmacao">
                  <p className="painel-confirmacao-titulo">
                    O administrador propôs uma alteração na sua solicitação. Veja a descrição da
                    etapa mais recente acima e decida se aceita ou não.
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
          )}
        </section>

        <section className="historico-secao">
          <h3 className="historico-subtitulo">Solicitações anteriores</h3>

          {carregandoHistorico && <p className="mensagem-central">Carregando histórico...</p>}

          {!carregandoHistorico && historico.length === 0 && (
            <p className="mensagem-central">Nenhuma solicitação anterior encontrada.</p>
          )}

          {!carregandoHistorico && historico.length > 0 && (
            <div className="lista-historico">
              {historico.map((item) => {
                const detalhe = detalhesExpandidos[item.id];

                return (
                  <div key={item.id} className="item-historico">
                    <div className="item-historico-resumo">
                      <div>
                        <p className="item-historico-ano">Ano {item.ano}</p>
                        <p className="item-historico-data">
                          {item.dataSolicitacao}
                        </p>
                      </div>
                      <BadgeStatusSolicitacao status={item.statusAtual} />
                      <button
                        type="button"
                        className="botao-ver-detalhes-historico"
                        onClick={() => toggleDetalhes(item.id)}
                      >
                        {detalhe !== undefined ? "Ocultar detalhes" : "Ver detalhes"}
                      </button>
                    </div>

                    {detalhe === "carregando" && (
                      <p className="mensagem-central">Carregando detalhes...</p>
                    )}

                    {detalhe === "erro" && (
                      <p className="item-historico-erro">
                        Não foi possível carregar os detalhes dessa solicitação.
                      </p>
                    )}

                    {detalhe && detalhe !== "carregando" && detalhe !== "erro" && (
                      <div className="item-historico-detalhe">
                        <DetalhesSolicitacaoCartao solicitacao={detalhe} />
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </section>
      </main>

      <Rodape />
    </div>
  );
}

export default HistoricoSolicitacoes;