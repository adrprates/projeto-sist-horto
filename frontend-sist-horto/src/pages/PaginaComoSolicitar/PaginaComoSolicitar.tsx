import { Link } from "react-router-dom";
import { SITE } from "../../config/site";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import "./PaginaComoSolicitar.css";

const PASSOS = [
  ["Crie sua conta", "Na tela de cadastro, informe nome, CPF, e-mail, celular, endereço e escolha um login e uma senha. Se já tem conta, pule para o próximo passo."],
  ["Entre no sistema", "Faça login com o seu usuário e senha."],
  ["Escolha as mudas no catálogo", "Filtre por categoria, nome ou características e use \"Ver mais detalhes\" para conhecer cada espécie."],
  ["Monte sua solicitação", "Escolha a quantidade e clique em \"Adicionar à Solicitação\". O quadro de saldo mostra quanto ainda cabe no limite do ano."],
  ["Revise e finalize", "Em \"Minhas solicitações\", ajuste quantidades, remova itens e clique em \"Finalizar Solicitação\". Ela passa a ficar Pendente para análise."],
  ["Acompanhe o status", "A mesma página mostra o histórico do pedido. Se a equipe propuser uma alteração, você pode aceitar ou recusar por lá."],
  ["Retire no Horto", "Quando o status for \"Pronta para Retirada\", vá ao Horto Florestal até a data limite indicada, com um documento de identificação."],
];

const PERGUNTAS = [
  ["Preciso pagar pela muda?", "Não. A doação é gratuita para moradores do município."],
  ["Quantas mudas posso pedir?", "Os limites são definidos a cada ano pela Secretaria: um total de mudas, um limite para frutíferas, outro para as demais espécies e um máximo por espécie. O sistema mostra seu saldo enquanto você monta a solicitação."],
  ["Posso fazer mais de uma solicitação no ano?", "Não. Cada pessoa envia uma solicitação por ano. Enquanto ela não for enviada, você pode alterar os itens à vontade."],
  ["Quanto tempo leva?", "Depende da espécie e do estoque do viveiro. Acompanhe o status em \"Minhas solicitações\"."],
  ["E se eu não retirar no prazo?", "Após a data limite de retirada, a solicitação é marcada como expirada e as mudas voltam para o estoque."],
];

function Folha({ espelhada }: { espelhada: boolean }) {
  return (
    <svg
      width="34"
      height="24"
      viewBox="0 0 34 24"
      aria-hidden="true"
      className={espelhada ? "passo-folha-espelhada" : undefined}
    >
      <path d="M0 12C8-2 26-2 34 12 26 26 8 26 0 12Z" fill="#8DB51F" />
    </svg>
  );
}

function PaginaComoSolicitar() {
  return (
    <div>
      <CabecalhoPagina
        titulo="Como solicitar sua muda"
        texto="Sete passos, feitos pelo computador ou pelo celular, direto aqui no sistema."
      />

      <section className="container-pagina secao como-solicitar-passos">
        <ol className="linha-passos">
          {PASSOS.map(([titulo, descricao], indice) => (
            <li key={titulo} className="passo">
              <span className="passo-numero">{indice + 1}</span>
              <span className="passo-folha" aria-hidden="true">
                <Folha espelhada={indice % 2 === 1} />
              </span>
              <h2 className="passo-titulo">{titulo}</h2>
              <p className="passo-descricao">{descricao}</p>
            </li>
          ))}
        </ol>

        <div className="como-solicitar-acoes">
          <Link to="/catalogo" className="botao-primario">
            Ir para o catálogo
          </Link>
          <Link to="/registro" className="link-sublinhado">
            Ainda não tenho conta
          </Link>
        </div>
        <p className="como-solicitar-ajuda">
          Com dificuldade? A Secretaria atende pelo telefone {SITE.secretaria.telefone} e
          pessoalmente.
        </p>
      </section>

      <section className="secao secao-suave">
        <div className="container-pagina como-solicitar-faq">
          <h2 className="secao-titulo">Perguntas frequentes</h2>
          <div className="faq-lista">
            {PERGUNTAS.map(([pergunta, resposta]) => (
              <details key={pergunta} className="faq-item">
                <summary>
                  {pergunta}
                  <span className="faq-icone" aria-hidden="true">+</span>
                </summary>
                <p>{resposta}</p>
              </details>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}

export default PaginaComoSolicitar;
