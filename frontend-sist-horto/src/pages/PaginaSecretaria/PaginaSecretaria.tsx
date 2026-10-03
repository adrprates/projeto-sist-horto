import { SITE } from "../../config/site";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import BlocoLocalizacao from "../../components/BlocoLocalizacao/BlocoLocalizacao";

const FRENTES_DE_TRABALHO = [
  ["Produção de mudas", "Manejo do Horto Florestal de Patrocínio, da semente até a muda pronta para doação e plantio."],
  ["Arborização urbana", "Planejamento e plantio em ruas, praças e áreas públicas da cidade."],
  ["Apoio ao produtor rural", "Orientação técnica a pequenos e médios produtores, inclusive cafeicultores do município."],
  ["Licenciamento e fiscalização ambiental", "Análise de intervenções ambientais e compensação com plantio de mudas nativas."],
];

function PaginaSecretaria() {
  const secretaria = SITE.secretaria;

  return (
    <div>
      <CabecalhoPagina
        titulo={`${secretaria.nome} (${secretaria.sigla})`}
        texto={`Responsável pelo meio ambiente, pelo apoio à agricultura e pela produção de mudas de ${SITE.cidade}, incluindo o Horto Florestal e o programa de doação à população.`}
      />

      <section className="container-pagina secao">
        <h2 className="secao-titulo">O que fazemos</h2>
        <dl className="lista-topicos">
          {FRENTES_DE_TRABALHO.map(([titulo, descricao]) => (
            <div key={titulo}>
              <dt>{titulo}</dt>
              <dd>{descricao}</dd>
            </div>
          ))}
        </dl>
      </section>

      <section className="secao secao-suave">
        <div className="container-pagina">
          <h2 className="secao-titulo">Patrocínio e a agricultura</h2>
          <p className="secao-texto">
            Conhecida como a Capital Nacional do Café, Patrocínio tem na agricultura uma das bases
            da sua economia. A {secretaria.sigla} apoia quem planta no campo e amplia o verde da
            cidade, unindo produção rural, arborização urbana e educação ambiental em uma só pasta.
          </p>
        </div>
      </section>

      <section className="container-pagina secao">
        <h2 className="secao-titulo">Onde fica a Secretaria</h2>
        <BlocoLocalizacao local={secretaria} />
      </section>
    </div>
  );
}

export default PaginaSecretaria;
