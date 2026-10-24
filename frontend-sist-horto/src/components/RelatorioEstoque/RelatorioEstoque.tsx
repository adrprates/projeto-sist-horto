import { useEffect, useState } from "react";
import { FileDown } from "lucide-react";
import type { RelatorioEstoqueFilter } from "../../types/RelatorioEstoqueFilter";
import type { RelatorioEstoque as DadosRelatorioEstoque } from "../../types/RelatorioEstoque";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import { SituacaoEstoque, rotuloSituacaoEstoque } from "../../types/SituacaoEstoque";
import { buscarRelatorioEstoque } from "../../api/relatorioService";
import { gerarPdfRelatorio } from "../../utils/gerarPdfRelatorio";
import FiltrosRelatorioEstoque from "../FiltrosRelatorios/FiltrosRelatorioEstoque";
import ResumoRelatorio from "../ResumoRelatorio/ResumoRelatorio";
import "./RelatorioEstoque.css";

const CLASSE_SITUACAO: Record<SituacaoEstoque, string> = {
  [SituacaoEstoque.DISPONIVEL]: "situacao-estoque-disponivel",
  [SituacaoEstoque.ESTOQUE_BAIXO]: "situacao-estoque-baixo",
  [SituacaoEstoque.ESGOTADA]: "situacao-estoque-esgotada",
  [SituacaoEstoque.INDISPONIVEL]: "situacao-estoque-indisponivel",
};

function formatarNumero(valor: number): string {
  return valor.toLocaleString("pt-BR");
}

function RelatorioEstoque() {
  const [filtro, setFiltro] = useState<RelatorioEstoqueFilter>({});
  const [relatorio, setRelatorio] = useState<DadosRelatorioEstoque | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    buscarRelatorioEstoque(filtro)
      .then((resultado) => {
        setRelatorio(resultado);
        setErro("");
      })
      .catch(() => setErro("Não foi possível gerar o relatório de estoque."))
      .finally(() => setCarregando(false));
  }, [filtro]);

  function handleMudarFiltro(novoFiltro: RelatorioEstoqueFilter) {
    setCarregando(true);
    setFiltro(novoFiltro);
  }

  function descreverFiltros(): string[] {
    return [
      `Categoria: ${filtro.categoria ? rotuloCategoria[filtro.categoria] : "Todas"}`,
      `Situação: ${filtro.situacao ? rotuloSituacaoEstoque[filtro.situacao] : "Todas"}`,
    ];
  }

  function handleBaixarPdf() {
    if (!relatorio) return;

    gerarPdfRelatorio({
      titulo: "Relatório de posição de estoque",
      nomeArquivo: "relatorio-estoque",
      filtros: descreverFiltros(),
      indicadores: [
        { rotulo: "Espécies", valor: relatorio.totalEspecies },
        { rotulo: "Estoque físico", valor: formatarNumero(relatorio.estoqueFisico) },
        { rotulo: "Reservadas", valor: formatarNumero(relatorio.quantidadeReservada) },
        { rotulo: "Disponíveis", valor: formatarNumero(relatorio.estoqueDisponivel) },
      ],
      tabelas: [
        {
          titulo: "Totais por categoria",
          colunas: ["Categoria", "Espécies", "Físico", "Reservado", "Disponível"],
          linhas: relatorio.categorias.map((item) => [
            rotuloCategoria[item.categoria],
            item.totalEspecies,
            item.estoqueFisico,
            item.quantidadeReservada,
            item.estoqueDisponivel,
          ]),
          rodape: [
            "Total",
            relatorio.totalEspecies,
            relatorio.estoqueFisico,
            relatorio.quantidadeReservada,
            relatorio.estoqueDisponivel,
          ],
          colunasNumericas: [1, 2, 3, 4],
        },
        {
          titulo: "Posição por muda",
          colunas: ["Muda", "Categoria", "Físico", "Reservado", "Disponível", "Situação"],
          linhas: relatorio.itens.map((item) => [
            item.nomeMuda,
            rotuloCategoria[item.categoria],
            item.estoqueFisico,
            item.quantidadeReservada,
            item.estoqueDisponivel,
            item.motivoIndisponibilidade
              ? `${rotuloSituacaoEstoque[item.situacao]} (${item.motivoIndisponibilidade})`
              : rotuloSituacaoEstoque[item.situacao],
          ]),
          colunasNumericas: [2, 3, 4],
        },
      ],
    }).catch(() => setErro("Não foi possível gerar o arquivo PDF."));
  }

  const vazio = relatorio !== null && relatorio.itens.length === 0;

  return (
    <div>
      <FiltrosRelatorioEstoque
        filtro={filtro}
        aoMudarFiltro={handleMudarFiltro}
        aoLimpar={() => handleMudarFiltro({})}
      />

      <div className="relatorio-barra">
        <div>
          <h2 className="relatorio-titulo">Posição de estoque</h2>
          <p className="relatorio-subtitulo">
            Disponível = estoque físico menos as mudas reservadas em solicitações em andamento.
          </p>
        </div>
        <button
          type="button"
          className="botao-primario"
          onClick={handleBaixarPdf}
          disabled={!relatorio || vazio || carregando}
        >
          <FileDown size={16} />
          Baixar PDF
        </button>
      </div>

      {erro && <p className="relatorio-erro">{erro}</p>}

      {carregando && relatorio === null && (
        <p className="mensagem-central">Gerando relatório...</p>
      )}

      {relatorio && (
        <div className={`conteudo-atualizavel${carregando ? " atualizando" : ""}`}>
          <ResumoRelatorio
            indicadores={[
              { rotulo: "Espécies", valor: relatorio.totalEspecies },
              { rotulo: "Estoque físico", valor: relatorio.estoqueFisico },
              { rotulo: "Reservadas", valor: relatorio.quantidadeReservada },
              { rotulo: "Disponíveis", valor: relatorio.estoqueDisponivel },
            ]}
          />

          {vazio ? (
            <p className="mensagem-central surgir">Nenhuma muda encontrada com esses filtros.</p>
          ) : (
            <>
              <h3 className="relatorio-secao-titulo">Totais por categoria</h3>
              <div className="tabela-wrapper relatorio-tabela-espaco">
                <table className="tabela relatorio-tabela">
                  <thead>
                    <tr>
                      <th>Categoria</th>
                      <th className="relatorio-numero">Espécies</th>
                      <th className="relatorio-numero">Físico</th>
                      <th className="relatorio-numero">Reservado</th>
                      <th className="relatorio-numero">Disponível</th>
                    </tr>
                  </thead>
                  <tbody className="lista-animada">
                    {relatorio.categorias.map((item) => (
                      <tr key={item.categoria}>
                        <td>{rotuloCategoria[item.categoria]}</td>
                        <td className="relatorio-numero">{item.totalEspecies}</td>
                        <td className="relatorio-numero">{formatarNumero(item.estoqueFisico)}</td>
                        <td className="relatorio-numero">{formatarNumero(item.quantidadeReservada)}</td>
                        <td className="relatorio-numero">{formatarNumero(item.estoqueDisponivel)}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr>
                      <td>Total</td>
                      <td className="relatorio-numero">{relatorio.totalEspecies}</td>
                      <td className="relatorio-numero">{formatarNumero(relatorio.estoqueFisico)}</td>
                      <td className="relatorio-numero">{formatarNumero(relatorio.quantidadeReservada)}</td>
                      <td className="relatorio-numero">{formatarNumero(relatorio.estoqueDisponivel)}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>

              <h3 className="relatorio-secao-titulo">Posição por muda</h3>
              <div className="tabela-wrapper">
                <table className="tabela relatorio-tabela">
                  <thead>
                    <tr>
                      <th>Muda</th>
                      <th>Categoria</th>
                      <th className="relatorio-numero">Físico</th>
                      <th className="relatorio-numero">Reservado</th>
                      <th className="relatorio-numero">Disponível</th>
                      <th>Situação</th>
                    </tr>
                  </thead>
                  <tbody className="lista-animada">
                    {relatorio.itens.map((item) => (
                      <tr key={item.mudaId}>
                        <td>{item.nomeMuda}</td>
                        <td>{rotuloCategoria[item.categoria]}</td>
                        <td className="relatorio-numero">{formatarNumero(item.estoqueFisico)}</td>
                        <td className="relatorio-numero">{formatarNumero(item.quantidadeReservada)}</td>
                        <td className="relatorio-numero">{formatarNumero(item.estoqueDisponivel)}</td>
                        <td>
                          <span
                            className={`situacao-estoque ${CLASSE_SITUACAO[item.situacao]}`}
                            title={item.motivoIndisponibilidade ?? undefined}
                          >
                            {rotuloSituacaoEstoque[item.situacao]}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}

export default RelatorioEstoque;
