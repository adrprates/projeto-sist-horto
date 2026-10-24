import { useEffect, useState } from "react";
import { FileDown } from "lucide-react";
import type { SaidaFilter } from "../../types/SaidaFilter";
import type { RelatorioSaida } from "../../types/RelatorioSaida";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import { buscarRelatorioSaidas } from "../../api/relatorioService";
import { listarMudas } from "../../api/mudaService";
import { useValorAtrasado } from "../../hooks/useValorAtrasado";
import { gerarPdfRelatorio } from "../../utils/gerarPdfRelatorio";
import FiltrosRelatorioSaidas from "../FiltrosRelatorios/FiltrosRelatorioSaidas";
import ResumoRelatorio from "../ResumoRelatorio/ResumoRelatorio";
import BarraPaginacao from "../Paginacao/BarraPaginacao";
import Paginacao from "../Paginacao/Paginacao";

function dataIso(data: Date): string {
  const ano = data.getFullYear();
  const mes = String(data.getMonth() + 1).padStart(2, "0");
  const dia = String(data.getDate()).padStart(2, "0");
  return `${ano}-${mes}-${dia}`;
}

function filtroPadrao(): SaidaFilter {
  const hoje = new Date();
  return {
    dataInicial: dataIso(new Date(hoje.getFullYear(), 0, 1)),
    dataFinal: dataIso(hoje),
  };
}

function formatarDataIso(data?: string): string {
  if (!data) return "";
  const [ano, mes, dia] = data.split("-");
  return `${dia}/${mes}/${ano}`;
}

function descreverPeriodo(filtro: SaidaFilter): string {
  if (filtro.dataInicial && filtro.dataFinal) {
    return `${formatarDataIso(filtro.dataInicial)} a ${formatarDataIso(filtro.dataFinal)}`;
  }
  if (filtro.dataInicial) return `A partir de ${formatarDataIso(filtro.dataInicial)}`;
  if (filtro.dataFinal) return `Até ${formatarDataIso(filtro.dataFinal)}`;
  return "Todo o período";
}

function RelatorioSaidas() {
  const [filtro, setFiltro] = useState<SaidaFilter>(filtroPadrao);
  const filtroAtrasado = useValorAtrasado(filtro);
  const [relatorio, setRelatorio] = useState<RelatorioSaida | null>(null);
  const [mudas, setMudas] = useState<DadosMudaResumo[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [pagina, setPagina] = useState(0);
  const [tamanho, setTamanho] = useState(10);

  useEffect(() => {
    listarMudas({})
      .then(setMudas)
      .catch(() => setMudas([]));
  }, []);

  useEffect(() => {
    buscarRelatorioSaidas(filtroAtrasado)
      .then((resultado) => {
        setRelatorio(resultado);
        setErro("");
        setPagina(0);
      })
      .catch(() => setErro("Não foi possível gerar o relatório de saídas."))
      .finally(() => setCarregando(false));
  }, [filtroAtrasado]);

  function handleMudarFiltro(novoFiltro: SaidaFilter) {
    setCarregando(true);
    setFiltro(novoFiltro);
  }

  function descreverFiltros(): string[] {
    const filtros = [`Período: ${descreverPeriodo(filtro)}`];
    if (filtro.categoria) filtros.push(`Categoria: ${rotuloCategoria[filtro.categoria]}`);
    if (filtro.mudaId) {
      const muda = mudas.find((item) => item.id === filtro.mudaId);
      filtros.push(`Muda: ${muda?.nomesPopulares[0] ?? filtro.mudaId}`);
    }
    return filtros;
  }

  function handleBaixarPdf() {
    if (!relatorio) return;

    gerarPdfRelatorio({
      titulo: "Relatório de saída de mudas",
      nomeArquivo: "relatorio-saidas",
      filtros: descreverFiltros(),
      indicadores: [
        { rotulo: "Mudas entregues", valor: relatorio.totalMudas },
        { rotulo: "Retiradas", valor: relatorio.totalRetiradas },
        { rotulo: "Beneficiários", valor: relatorio.totalBeneficiarios },
      ],
      tabelas: [
        {
          titulo: "Resumo por espécie",
          colunas: ["Muda", "Categoria", "Quantidade", "Beneficiários"],
          linhas: relatorio.resumo.map((item) => [
            item.nomeMuda,
            rotuloCategoria[item.categoria],
            item.quantidade,
            item.totalBeneficiarios,
          ]),
          rodape: ["Total", "", relatorio.totalMudas, relatorio.totalBeneficiarios],
          colunasNumericas: [2, 3],
        },
        {
          titulo: "Detalhamento das saídas",
          colunas: ["Data", "Solicitação", "Muda", "Quantidade", "Beneficiário"],
          linhas: relatorio.itens.map((item) => [
            item.dataSaida,
            `#${item.solicitacaoId}`,
            item.nomeMuda,
            item.quantidade,
            item.nomeBeneficiario,
          ]),
          colunasNumericas: [3],
        },
      ],
    }).catch(() => setErro("Não foi possível gerar o arquivo PDF."));
  }

  const itensDaPagina = relatorio?.itens.slice(pagina * tamanho, (pagina + 1) * tamanho) ?? [];
  const totalPaginas = relatorio ? Math.ceil(relatorio.itens.length / tamanho) : 0;
  const vazio = relatorio !== null && relatorio.itens.length === 0;

  return (
    <div>
      <FiltrosRelatorioSaidas
        filtro={filtro}
        mudas={mudas}
        aoMudarFiltro={handleMudarFiltro}
        aoLimpar={() => handleMudarFiltro(filtroPadrao())}
      />

      <div className="relatorio-barra">
        <div>
          <h2 className="relatorio-titulo">Saída de mudas</h2>
          <p className="relatorio-subtitulo">{descreverPeriodo(filtro)}</p>
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
              { rotulo: "Mudas entregues", valor: relatorio.totalMudas },
              { rotulo: "Retiradas", valor: relatorio.totalRetiradas },
              { rotulo: "Beneficiários", valor: relatorio.totalBeneficiarios },
            ]}
          />

          {vazio ? (
            <p className="mensagem-central surgir">Nenhuma saída registrada com esses filtros.</p>
          ) : (
            <>
              <h3 className="relatorio-secao-titulo">Resumo por espécie</h3>
              <div className="tabela-wrapper relatorio-tabela-espaco">
                <table className="tabela relatorio-tabela">
                  <thead>
                    <tr>
                      <th>Muda</th>
                      <th>Categoria</th>
                      <th className="relatorio-numero">Quantidade</th>
                      <th className="relatorio-numero">Beneficiários</th>
                    </tr>
                  </thead>
                  <tbody className="lista-animada">
                    {relatorio.resumo.map((item) => (
                      <tr key={item.mudaId}>
                        <td>{item.nomeMuda}</td>
                        <td>{rotuloCategoria[item.categoria]}</td>
                        <td className="relatorio-numero">{item.quantidade.toLocaleString("pt-BR")}</td>
                        <td className="relatorio-numero">{item.totalBeneficiarios}</td>
                      </tr>
                    ))}
                  </tbody>
                  <tfoot>
                    <tr>
                      <td>Total</td>
                      <td></td>
                      <td className="relatorio-numero">{relatorio.totalMudas.toLocaleString("pt-BR")}</td>
                      <td className="relatorio-numero">{relatorio.totalBeneficiarios}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>

              <h3 className="relatorio-secao-titulo">Detalhamento das saídas</h3>
              <BarraPaginacao
                pagina={pagina}
                tamanho={tamanho}
                totalElementos={relatorio.itens.length}
                rotuloItens="registros"
                aoMudarTamanho={(novoTamanho) => {
                  setTamanho(novoTamanho);
                  setPagina(0);
                }}
              />
              <div className="tabela-wrapper">
                <table className="tabela relatorio-tabela">
                  <thead>
                    <tr>
                      <th>Data</th>
                      <th>Solicitação</th>
                      <th>Muda</th>
                      <th className="relatorio-numero">Quantidade</th>
                      <th>Beneficiário</th>
                    </tr>
                  </thead>
                  <tbody key={pagina} className="lista-animada">
                    {itensDaPagina.map((item, indice) => (
                      <tr key={`${item.solicitacaoId}-${item.nomeMuda}-${indice}`}>
                        <td>{item.dataSaida}</td>
                        <td>#{item.solicitacaoId}</td>
                        <td>{item.nomeMuda}</td>
                        <td className="relatorio-numero">{item.quantidade}</td>
                        <td>{item.nomeBeneficiario}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <Paginacao pagina={pagina} totalPaginas={totalPaginas} aoMudarPagina={setPagina} />
            </>
          )}
        </div>
      )}
    </div>
  );
}

export default RelatorioSaidas;
