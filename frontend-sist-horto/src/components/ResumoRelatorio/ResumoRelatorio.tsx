import "./ResumoRelatorio.css";

interface IndicadorRelatorio {
  rotulo: string;
  valor: number;
}

interface ResumoRelatorioProps {
  indicadores: IndicadorRelatorio[];
}

function ResumoRelatorio({ indicadores }: ResumoRelatorioProps) {
  return (
    <div className="resumo-relatorio">
      {indicadores.map((indicador) => (
        <div key={indicador.rotulo} className="resumo-relatorio-card">
          <span className="resumo-relatorio-rotulo">{indicador.rotulo}</span>
          <strong className="resumo-relatorio-valor">
            {indicador.valor.toLocaleString("pt-BR")}
          </strong>
        </div>
      ))}
    </div>
  );
}

export default ResumoRelatorio;
