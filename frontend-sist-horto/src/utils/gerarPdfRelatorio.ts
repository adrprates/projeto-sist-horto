import type { jsPDF } from "jspdf";
import { SITE } from "../config/site";

type Celula = string | number;

export interface TabelaPdf {
  titulo: string;
  colunas: string[];
  linhas: Celula[][];
  rodape?: Celula[];
  colunasNumericas?: number[];
}

export interface DadosRelatorioPdf {
  titulo: string;
  nomeArquivo: string;
  filtros: string[];
  indicadores: { rotulo: string; valor: Celula }[];
  tabelas: TabelaPdf[];
}

const VERDE: [number, number, number] = [34, 96, 63];
const VERDE_CLARO: [number, number, number] = [240, 246, 241];
const TEXTO: [number, number, number] = [23, 33, 27];
const CINZA: [number, number, number] = [94, 106, 98];
const MARGEM = 14;

type DocumentoComTabela = jsPDF & { lastAutoTable?: { finalY: number } };

function posicaoAposTabela(documento: jsPDF, padrao: number): number {
  return (documento as DocumentoComTabela).lastAutoTable?.finalY ?? padrao;
}

export async function gerarPdfRelatorio(dados: DadosRelatorioPdf) {
  const [{ jsPDF }, { default: autoTable }] = await Promise.all([
    import("jspdf"),
    import("jspdf-autotable"),
  ]);

  const documento = new jsPDF({ unit: "mm", format: "a4" });
  const largura = documento.internal.pageSize.getWidth();
  const altura = documento.internal.pageSize.getHeight();
  const geradoEm = new Date().toLocaleString("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  });

  documento.setFillColor(...VERDE);
  documento.rect(0, 0, largura, 22, "F");
  documento.setTextColor(255, 255, 255);
  documento.setFont("helvetica", "bold");
  documento.setFontSize(13);
  documento.text(SITE.nome, MARGEM, 10);
  documento.setFont("helvetica", "normal");
  documento.setFontSize(9);
  documento.text(`${SITE.secretaria.nome} - ${SITE.prefeitura.nome}`, MARGEM, 16);

  let y = 34;
  documento.setTextColor(...TEXTO);
  documento.setFont("helvetica", "bold");
  documento.setFontSize(16);
  documento.text(dados.titulo, MARGEM, y);

  y += 6;
  documento.setFont("helvetica", "normal");
  documento.setFontSize(9);
  documento.setTextColor(...CINZA);
  documento.text(`Gerado em ${geradoEm}`, MARGEM, y);

  dados.filtros.forEach((filtro) => {
    y += 5;
    documento.text(filtro, MARGEM, y);
  });

  y += 6;
  const espaco = 4;
  const larguraIndicador =
    (largura - MARGEM * 2 - espaco * (dados.indicadores.length - 1)) / dados.indicadores.length;

  dados.indicadores.forEach((indicador, indice) => {
    const x = MARGEM + indice * (larguraIndicador + espaco);
    documento.setFillColor(...VERDE_CLARO);
    documento.roundedRect(x, y, larguraIndicador, 18, 2, 2, "F");
    documento.setTextColor(...CINZA);
    documento.setFontSize(8);
    documento.text(indicador.rotulo.toUpperCase(), x + 4, y + 6);
    documento.setTextColor(...VERDE);
    documento.setFont("helvetica", "bold");
    documento.setFontSize(14);
    documento.text(String(indicador.valor), x + 4, y + 14);
    documento.setFont("helvetica", "normal");
  });

  y += 28;

  dados.tabelas.forEach((tabela) => {
    if (y > altura - 40) {
      documento.addPage();
      y = 20;
    }

    documento.setTextColor(...TEXTO);
    documento.setFont("helvetica", "bold");
    documento.setFontSize(11);
    documento.text(tabela.titulo, MARGEM, y);

    const colunasNumericas = tabela.colunasNumericas ?? [];

    autoTable(documento, {
      startY: y + 3,
      head: [tabela.colunas],
      body: tabela.linhas,
      foot: tabela.rodape ? [tabela.rodape] : undefined,
      margin: { left: MARGEM, right: MARGEM, bottom: 18 },
      styles: { font: "helvetica", fontSize: 9, textColor: TEXTO, cellPadding: 2.2 },
      headStyles: { fillColor: VERDE, textColor: 255, fontStyle: "bold" },
      footStyles: { fillColor: VERDE_CLARO, textColor: TEXTO, fontStyle: "bold" },
      alternateRowStyles: { fillColor: [250, 252, 250] },
      didParseCell: (celula) => {
        if (colunasNumericas.includes(celula.column.index)) {
          celula.cell.styles.halign = "right";
        }
      },
      showFoot: "lastPage",
    });

    y = posicaoAposTabela(documento, y) + 12;
  });

  const totalPaginas = documento.getNumberOfPages();

  for (let pagina = 1; pagina <= totalPaginas; pagina++) {
    documento.setPage(pagina);
    documento.setDrawColor(197, 224, 207);
    documento.line(MARGEM, altura - 12, largura - MARGEM, altura - 12);
    documento.setFont("helvetica", "normal");
    documento.setFontSize(8);
    documento.setTextColor(...CINZA);
    documento.text(`${SITE.nome} - ${dados.titulo}`, MARGEM, altura - 7);
    documento.text(`Página ${pagina} de ${totalPaginas}`, largura - MARGEM, altura - 7, {
      align: "right",
    });
  }

  const hoje = new Date();
  const dataArquivo = [
    hoje.getFullYear(),
    String(hoje.getMonth() + 1).padStart(2, "0"),
    String(hoje.getDate()).padStart(2, "0"),
  ].join("-");

  documento.save(`${dados.nomeArquivo}-${dataArquivo}.pdf`);
}
