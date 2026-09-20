import { useEffect, useState } from "react";
import { Gauge } from "lucide-react";
import type { ParametroAnualDisponivel } from "../../types/ParametroAnualDisponivel";
import type { ParametroAnual } from "../../types/ParametroAnual";
import { buscarParametroAnual } from "../../api/parametroAnualService";
import "./CardSaldoParametro.css";

interface CardSaldoParametroProps {
  saldo: ParametroAnualDisponivel | null;
  carregando: boolean;
}

interface LinhaSaldoProps {
  rotulo: string;
  usado: number;
  restante: number;
}

function LinhaSaldo({ rotulo, usado, restante }: LinhaSaldoProps) {
  const limite = usado + Math.max(restante, 0);
  const percentualUsado = limite > 0 ? Math.min(100, (usado / limite) * 100) : 0;

  let classeBarra = "barra-saldo-preenchida";
  if (restante <= 0) {
    classeBarra += " barra-saldo-esgotada";
  } else if (limite > 0 && restante / limite <= 0.2) {
    classeBarra += " barra-saldo-baixa";
  }

  return (
    <div className="linha-saldo">
      <div className="linha-saldo-topo">
        <span className="linha-saldo-rotulo">{rotulo}</span>
        <span className="linha-saldo-numeros">
          {usado} / {limite} — {restante <= 0 ? "esgotado" : `${restante} disponíveis`}
        </span>
      </div>
      <div className="barra-saldo-fundo">
        <div className={classeBarra} style={{ width: `${percentualUsado}%` }} />
      </div>
    </div>
  );
}

function CardSaldoParametro({ saldo, carregando }: CardSaldoParametroProps) {
  const [limitesAno, setLimitesAno] = useState<ParametroAnual | null>(null);
  const [carregandoLimites, setCarregandoLimites] = useState(true);

  useEffect(() => {
    const anoAtual = new Date().getFullYear();

    buscarParametroAnual(anoAtual)
      .then(setLimitesAno)
      .catch(() => setLimitesAno(null))
      .finally(() => setCarregandoLimites(false));
  }, []);

  return (
    <div className="card-saldo-parametro">
      <div className="card-saldo-titulo">
        <Gauge size={18} />
        <span>Seu saldo de solicitação este ano</span>
      </div>

      {carregando && <p className="card-saldo-mensagem">Carregando saldo...</p>}

      {!carregando && !saldo && (
        <p className="card-saldo-mensagem">Não foi possível carregar seu saldo.</p>
      )}

      {!carregando && saldo && (
        <div className="card-saldo-linhas">
          <LinhaSaldo
            rotulo="Frutíferas"
            usado={saldo.frutiferasUtilizadas}
            restante={saldo.frutiferasDisponiveis}
          />
          <LinhaSaldo
            rotulo="Outras categorias"
            usado={saldo.outrasUtilizadas}
            restante={saldo.outrasDisponiveis}
          />
          <LinhaSaldo rotulo="Total geral" usado={saldo.totalUtilizado} restante={saldo.totalDisponivel} />
        </div>
      )}

      {!carregandoLimites && limitesAno && (
        <div className="card-saldo-regras">
          <p className="card-saldo-regras-titulo">Limite por espécie (mesma muda)</p>
          <div className="card-saldo-regras-linhas">
            <span>
              Frutíferas: até <strong>{limitesAno.maxPorEspecieFrutifera}</strong> unidades da
              mesma espécie
            </span>
            <span>
              Outras categorias: até <strong>{limitesAno.maxPorEspecieOutras}</strong> unidades da
              mesma espécie
            </span>
          </div>
        </div>
      )}
    </div>
  );
}

export default CardSaldoParametro;