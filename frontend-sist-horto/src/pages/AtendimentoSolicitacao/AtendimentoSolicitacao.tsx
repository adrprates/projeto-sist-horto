import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { CheckCircle2, Package, Search, Trash2 } from "lucide-react";
import type { DadosBeneficiario } from "../../types/DadosBeneficiario";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import type { Solicitacao } from "../../types/Solicitacao";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import type { ParametroAnualDisponivel } from "../../types/ParametroAnualDisponivel";
import { StatusSolicitacao } from "../../types/StatusSolicitacao";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import { buscarBeneficiario } from "../../api/beneficiarioService";
import { listarMudas } from "../../api/mudaService";
import {
  adicionarItemParaBeneficiario,
  atualizarItemDoBeneficiario,
  buscarRascunhoDoBeneficiario,
  buscarSaldoDoBeneficiario,
  buscarSolicitacaoAtualDoBeneficiario,
  enviarSolicitacaoDoBeneficiario,
  removerItemDoBeneficiario,
} from "../../api/atendimentoService";
import { extrairMensagemErro } from "../../utils/extrairMensagemErro";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import CardSaldoParametro from "../../components/CardSaldoParametro/CardSaldoParametro";
import SeletorQuantidade from "../../components/SeletorQuantidade/SeletorQuantidade";
import DetalhesSolicitacaoCartao from "../../components/DetalhesSolicitacaoCartao/DetalhesSolicitacaoCartao";
import "./AtendimentoSolicitacao.css";

function AtendimentoSolicitacao() {
  const { id } = useParams();
  const beneficiarioId = Number(id);
  const navigate = useNavigate();

  const [beneficiario, setBeneficiario] = useState<DadosBeneficiario | null>(null);
  const [solicitacaoEnviada, setSolicitacaoEnviada] = useState<SolicitacaoAdmin | null>(null);
  const [rascunho, setRascunho] = useState<Solicitacao | null>(null);
  const [saldo, setSaldo] = useState<ParametroAnualDisponivel | null>(null);
  const [carregandoSaldo, setCarregandoSaldo] = useState(true);
  const [carregando, setCarregando] = useState(true);
  const [erroCarregamento, setErroCarregamento] = useState("");

  const [mudas, setMudas] = useState<DadosMudaResumo[]>([]);
  const [busca, setBusca] = useState("");
  const [carregandoMudas, setCarregandoMudas] = useState(true);
  const [quantidades, setQuantidades] = useState<Record<number, number>>({});

  const [processando, setProcessando] = useState(false);
  const [erro, setErro] = useState("");
  const [enviadaAgora, setEnviadaAgora] = useState(false);

  const recarregarSaldo = useCallback(() => {
    buscarSaldoDoBeneficiario(beneficiarioId)
      .then(setSaldo)
      .catch(() => setSaldo(null))
      .finally(() => setCarregandoSaldo(false));
  }, [beneficiarioId]);

  useEffect(() => {
    async function carregar() {
      try {
        const [dadosBeneficiario, atual] = await Promise.all([
          buscarBeneficiario(beneficiarioId),
          buscarSolicitacaoAtualDoBeneficiario(beneficiarioId),
        ]);
        setBeneficiario(dadosBeneficiario);

        if (atual && atual.statusAtual !== StatusSolicitacao.RASCUNHO) {
          setSolicitacaoEnviada(atual);
          return;
        }

        setRascunho(await buscarRascunhoDoBeneficiario(beneficiarioId));
        recarregarSaldo();
      } catch (erroRequisicao) {
        setErroCarregamento(
          extrairMensagemErro(erroRequisicao, "Não foi possível carregar o atendimento.")
        );
      } finally {
        setCarregando(false);
      }
    }

    carregar();
  }, [beneficiarioId, recarregarSaldo]);

  useEffect(() => {
    const temporizador = setTimeout(() => {
      setCarregandoMudas(true);
      listarMudas(busca ? { nomePopular: busca } : {})
        .then(setMudas)
        .catch(() => setMudas([]))
        .finally(() => setCarregandoMudas(false));
    }, 300);

    return () => clearTimeout(temporizador);
  }, [busca]);

  async function executar(acao: () => Promise<Solicitacao>) {
    setErro("");
    setProcessando(true);

    try {
      setRascunho(await acao());
      recarregarSaldo();
    } catch (erroRequisicao) {
      setErro(extrairMensagemErro(erroRequisicao));
    } finally {
      setProcessando(false);
    }
  }

  function handleAdicionar(muda: DadosMudaResumo) {
    const quantidade = quantidades[muda.id] ?? 1;
    executar(() => adicionarItemParaBeneficiario(beneficiarioId, { mudaId: muda.id, quantidade }));
  }

  function handleRemover(itemId: number, nomeMuda: string) {
    if (window.confirm(`Remover "${nomeMuda}" da solicitação?`)) {
      executar(() => removerItemDoBeneficiario(beneficiarioId, itemId));
    }
  }

  async function handleEnviar() {
    const confirmado = window.confirm(
      `Enviar a solicitação de ${beneficiario?.nome ?? "beneficiário"} para análise?`
    );

    if (!confirmado) {
      return;
    }

    setErro("");
    setProcessando(true);

    try {
      setSolicitacaoEnviada(await enviarSolicitacaoDoBeneficiario(beneficiarioId));
      setEnviadaAgora(true);
    } catch (erroRequisicao) {
      setErro(extrairMensagemErro(erroRequisicao, "Não foi possível enviar a solicitação."));
    } finally {
      setProcessando(false);
    }
  }

  const itens = rascunho?.itens ?? [];
  const totalItens = itens.reduce((total, item) => total + item.quantidade, 0);

  return (
    <div>
      <CabecalhoPagina
        compacto
        voltar={{ rotulo: "Voltar para beneficiários", aoClicar: () => navigate("/beneficiarios") }}
        titulo={beneficiario?.nome ? `Solicitação de ${beneficiario.nome}` : "Atendimento"}
        texto={
          beneficiario
            ? `CPF ${beneficiario.cpf ?? "—"} · Pedido registrado pela Secretaria em nome do beneficiário.`
            : undefined
        }
      />

      <section className="container-pagina atendimento">
        {carregando && <p className="mensagem-central">Carregando atendimento...</p>}

        {!carregando && erroCarregamento && <p className="atendimento-erro">{erroCarregamento}</p>}

        {!carregando && !erroCarregamento && solicitacaoEnviada && (
          <div className="atendimento-enviada">
            {enviadaAgora ? (
              <p className="atendimento-sucesso">
                <CheckCircle2 size={18} />
                Solicitação enviada! Ela agora aparece em Gerenciar Solicitações como pendente.
              </p>
            ) : (
              <p className="atendimento-info">
                Este beneficiário já possui a solicitação deste ano. Acompanhe o andamento abaixo.
              </p>
            )}

            <DetalhesSolicitacaoCartao solicitacao={solicitacaoEnviada} />

            <div className="atendimento-enviada-acoes">
              <button
                type="button"
                className="botao-primario"
                onClick={() => navigate(`/admin/solicitacoes/${solicitacaoEnviada.id}`)}
              >
                Gerenciar esta solicitação
              </button>
            </div>
          </div>
        )}

        {!carregando && !erroCarregamento && !solicitacaoEnviada && (
          <div className="atendimento-grade">
            <div className="atendimento-mudas">
              <h2 className="atendimento-subtitulo">Escolha as mudas</h2>

              <div className="atendimento-busca">
                <Search size={18} />
                <input
                  type="text"
                  placeholder="Buscar por nome popular..."
                  value={busca}
                  onChange={(evento) => setBusca(evento.target.value)}
                />
              </div>

              {carregandoMudas && <p className="mensagem-central">Carregando mudas...</p>}

              {!carregandoMudas && mudas.length === 0 && (
                <p className="mensagem-central">Nenhuma muda encontrada.</p>
              )}

              {!carregandoMudas && mudas.length > 0 && (
                <ul className="atendimento-lista-mudas">
                  {mudas.map((muda) => {
                    const nome = muda.nomesPopulares.slice(0, 2).join(", ");
                    const semEstoque = muda.estoqueDisponivel <= 0;

                    return (
                      <li key={muda.id} className="atendimento-muda">
                        <div className="atendimento-muda-imagem">
                          {muda.linkImagemArvore ? (
                            <img src={muda.linkImagemArvore} alt={nome} />
                          ) : (
                            <span role="img" aria-label={nome}>🌱</span>
                          )}
                        </div>

                        <div className="atendimento-muda-info">
                          <p className="atendimento-muda-nome">{nome}</p>
                          <span className="tag">{rotuloCategoria[muda.categoria]}</span>
                          <span className="atendimento-muda-estoque">
                            <Package size={14} />
                            {semEstoque ? "Sem estoque" : `${muda.estoqueDisponivel} em estoque`}
                          </span>
                        </div>

                        <div className="atendimento-muda-acoes">
                          <SeletorQuantidade
                            valor={quantidades[muda.id] ?? 1}
                            aoAlterar={(valor) =>
                              setQuantidades((atual) => ({ ...atual, [muda.id]: valor }))
                            }
                            minimo={1}
                            maximo={Math.max(muda.estoqueDisponivel, 1)}
                            desabilitado={semEstoque || processando}
                          />
                          <button
                            type="button"
                            className="botao-tabela atendimento-botao-adicionar"
                            onClick={() => handleAdicionar(muda)}
                            disabled={semEstoque || processando}
                          >
                            Adicionar
                          </button>
                        </div>
                      </li>
                    );
                  })}
                </ul>
              )}
            </div>

            <aside className="atendimento-resumo">
              <h2 className="atendimento-subtitulo">Itens da solicitação</h2>

              <CardSaldoParametro saldo={saldo} carregando={carregandoSaldo} />

              {erro && <p className="atendimento-erro">{erro}</p>}

              {itens.length === 0 ? (
                <p className="atendimento-vazio">
                  Nenhuma muda adicionada ainda. Escolha na lista ao lado.
                </p>
              ) : (
                <ul className="atendimento-itens">
                  {itens.map((item) => {
                    const nome = item.muda.nomesPopulares.slice(0, 2).join(", ");

                    return (
                      <li key={item.id} className="atendimento-item">
                        <div className="atendimento-item-info">
                          <p className="atendimento-muda-nome">{nome}</p>
                          <span className="tag">{rotuloCategoria[item.muda.categoria]}</span>
                        </div>
                        <SeletorQuantidade
                          valor={item.quantidade}
                          aoAlterar={(valor) =>
                            executar(() =>
                              atualizarItemDoBeneficiario(beneficiarioId, item.id, valor)
                            )
                          }
                          minimo={1}
                          desabilitado={processando}
                        />
                        <button
                          type="button"
                          className="atendimento-remover"
                          onClick={() => handleRemover(item.id, nome)}
                          disabled={processando}
                          aria-label={`Remover ${nome}`}
                        >
                          <Trash2 size={16} />
                        </button>
                      </li>
                    );
                  })}
                </ul>
              )}

              <div className="atendimento-total">
                <span>Total de mudas</span>
                <strong>{totalItens}</strong>
              </div>

              <button
                type="button"
                className="botao-primario atendimento-enviar"
                onClick={handleEnviar}
                disabled={itens.length === 0 || processando}
              >
                {processando ? "Processando..." : "Enviar solicitação"}
              </button>
            </aside>
          </div>
        )}
      </section>
    </div>
  );
}

export default AtendimentoSolicitacao;
