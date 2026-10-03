import { SITE } from "../../config/site";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import BlocoLocalizacao from "../../components/BlocoLocalizacao/BlocoLocalizacao";
import "./PaginaHortoFlorestal.css";

const ESTRUTURA_DO_HORTO = [
  ["Viveiro de mudas", "Estufas de germinação, área sombreada de aclimatação e canteiros de crescimento."],
  ["Espécies do Cerrado", "Produção de nativas do Cerrado do Alto Paranaíba, como ipê, pequi e jatobá."],
  ["Frutíferas e arborização", "Mudas de frutíferas para quintais e escolas, e espécies próprias para ruas e praças."],
  ["Compensação ambiental", "Recebe mudas doadas como compensação por supressão de árvores autorizada pela SEMMA."],
];

const ETAPAS_DE_PRODUCAO = [
  "Coleta e germinação das sementes",
  "Repicagem para sacos de muda",
  "Crescimento e aclimatação",
  "Separação para doação",
];

function PaginaHortoFlorestal() {
  return (
    <div>
      <CabecalhoPagina
        titulo={SITE.horto.nome}
        texto="No bairro São Judas Tadeu, é onde nascem as mudas doadas à população e as usadas na arborização de Patrocínio."
      />

      <section className="container-pagina secao">
        <h2 className="secao-titulo">O que existe no Horto</h2>
        <dl className="lista-topicos lista-topicos-kraft">
          {ESTRUTURA_DO_HORTO.map(([titulo, descricao]) => (
            <div key={titulo}>
              <dt>{titulo}</dt>
              <dd>{descricao}</dd>
            </div>
          ))}
        </dl>
      </section>

      <section className="secao secao-suave">
        <div className="container-pagina">
          <h2 className="secao-titulo">Como uma muda é produzida</h2>
          <ol className="horto-etapas">
            {ETAPAS_DE_PRODUCAO.map((etapa, indice) => (
              <li key={etapa}>
                <span className="horto-etapa-numero">{indice + 1}</span>
                <p>{etapa}</p>
              </li>
            ))}
          </ol>
        </div>
      </section>

      <section className="container-pagina secao">
        <h2 className="secao-titulo">Onde fica o Horto</h2>
        <BlocoLocalizacao local={SITE.horto} />
      </section>
    </div>
  );
}

export default PaginaHortoFlorestal;
