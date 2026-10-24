import { useSearchParams } from "react-router-dom";
import { Sprout, Warehouse } from "lucide-react";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import RelatorioSaidas from "../../components/RelatorioSaidas/RelatorioSaidas";
import RelatorioEstoque from "../../components/RelatorioEstoque/RelatorioEstoque";
import "./PaginaRelatorios.css";

type AbaRelatorio = "saidas" | "estoque";

function PaginaRelatorios() {
  const [parametros, setParametros] = useSearchParams();
  const aba: AbaRelatorio = parametros.get("aba") === "estoque" ? "estoque" : "saidas";

  function selecionarAba(novaAba: AbaRelatorio) {
    setParametros(novaAba === "saidas" ? {} : { aba: novaAba }, { replace: true });
  }

  return (
    <div>
      <CabecalhoPagina
        compacto
        titulo="Relatórios"
        texto="Acompanhe as mudas entregues e a posição atual do estoque do Horto."
      />

      <section className="container-pagina container-relatorios">
        <div className="relatorio-abas" role="tablist">
          <button
            type="button"
            role="tab"
            aria-selected={aba === "saidas"}
            className={aba === "saidas" ? "relatorio-aba relatorio-aba-ativa" : "relatorio-aba"}
            onClick={() => selecionarAba("saidas")}
          >
            <Sprout size={16} />
            Saída de mudas
          </button>
          <button
            type="button"
            role="tab"
            aria-selected={aba === "estoque"}
            className={aba === "estoque" ? "relatorio-aba relatorio-aba-ativa" : "relatorio-aba"}
            onClick={() => selecionarAba("estoque")}
          >
            <Warehouse size={16} />
            Posição de estoque
          </button>
        </div>

        <div key={aba} className="surgir">
          {aba === "saidas" ? <RelatorioSaidas /> : <RelatorioEstoque />}
        </div>
      </section>
    </div>
  );
}

export default PaginaRelatorios;
