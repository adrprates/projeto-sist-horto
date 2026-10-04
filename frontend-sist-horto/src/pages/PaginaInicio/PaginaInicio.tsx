import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Clock, MapPin } from "lucide-react";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import { listarMudas } from "../../api/mudaService";
import { SITE } from "../../config/site";
import CardMudaVitrine from "../../components/CardMudaVitrine/CardMudaVitrine";
import IlustracaoMuda from "./IlustracaoMuda";
import "./PaginaInicio.css";

const ETAPAS_RESUMO = [
  ["Escolha", "Consulte o catálogo e veja quais espécies combinam com o seu espaço."],
  ["Solicite", "Crie sua conta, monte sua solicitação no sistema e envie para análise."],
  ["Retire", "Quando o pedido estiver pronto, busque as mudas no Horto dentro do prazo."],
];

function PaginaInicio() {
  const [destaques, setDestaques] = useState<DadosMudaResumo[]>([]);
  const [carregandoDestaques, setCarregandoDestaques] = useState(true);

  useEffect(() => {
    listarMudas({})
      .then((mudas) => setDestaques(mudas.filter((muda) => muda.disponivel && muda.estoqueDisponivel > 0).slice(0, 3)))
      .catch(() => setDestaques([]))
      .finally(() => setCarregandoDestaques(false));
  }, []);

  return (
    <div>
      <section className="inicio-hero">
        <div className="container-pagina inicio-hero-grade">
          <div>
            <p className="inicio-hero-cidade">{SITE.cidade}</p>
            <h1 className="inicio-hero-titulo">
              Mudas gratuitas para quem quer plantar em Patrocínio
            </h1>
            <p className="inicio-hero-texto">
              O Horto Florestal de Patrocínio doa mudas nativas do Cerrado, frutíferas e de
              arborização para moradores, escolas e produtores rurais. Veja o catálogo e faça
              sua solicitação pelo sistema.
            </p>
            <div className="inicio-hero-acoes">
              <Link to="/catalogo" className="botao-destaque">
                Pedir minha muda
              </Link>
              <Link to="/como-solicitar" className="botao-contorno-claro">
                Como funciona
              </Link>
            </div>
          </div>
          <IlustracaoMuda />
        </div>
        <svg
          viewBox="0 0 1440 40"
          preserveAspectRatio="none"
          className="onda-divisoria"
          aria-hidden="true"
        >
          <path fill="currentColor" d="M0 40V22C240 -2 480 -2 720 18s480 20 720 0V40Z" />
        </svg>
      </section>

      <section className="container-pagina secao">
        <h2 className="secao-titulo">Do pedido à retirada</h2>
        <ol className="inicio-etapas">
          {ETAPAS_RESUMO.map(([titulo, descricao]) => (
            <li key={titulo}>
              <h3>{titulo}</h3>
              <p>{descricao}</p>
            </li>
          ))}
        </ol>
        <Link to="/como-solicitar" className="link-sublinhado inicio-link-passos">
          Ver o passo a passo completo
        </Link>
      </section>

      <section className="secao secao-suave">
        <div className="container-pagina">
          <div className="inicio-destaques-topo">
            <div>
              <h2 className="secao-titulo">Algumas das mudas disponíveis</h2>
              <p className="secao-texto inicio-destaques-texto">
                A lista completa, com estoque atualizado, está no catálogo.
              </p>
            </div>
            <Link to="/catalogo" className="link-sublinhado">
              Ver todas
            </Link>
          </div>

          {carregandoDestaques && <p className="mensagem-central">Carregando mudas...</p>}

          {!carregandoDestaques && destaques.length === 0 && (
            <p className="mensagem-central">
              Nenhuma muda disponível no momento. Volte em breve!
            </p>
          )}

          {!carregandoDestaques && destaques.length > 0 && (
            <div className="inicio-destaques-grade">
              {destaques.map((muda) => (
                <CardMudaVitrine key={muda.id} muda={muda} />
              ))}
            </div>
          )}
        </div>
      </section>

      <section className="container-pagina secao inicio-horto">
        <div>
          <h2 className="secao-titulo">Venha conhecer o Horto</h2>
          <p className="secao-texto">
            É lá que as mudas são cultivadas e entregues, no bairro São Judas Tadeu. Escolas e
            grupos podem agendar visita.
          </p>
          <Link to="/horto-florestal" className="link-sublinhado inicio-link-passos">
            Sobre o Horto Florestal
          </Link>
        </div>

        <div className="inicio-horto-cartao">
          <p className="inicio-horto-nome">{SITE.horto.nome}</p>
          <ul>
            <li>
              <MapPin size={20} />
              {SITE.horto.endereco}, Patrocínio/MG
            </li>
            <li>
              <Clock size={20} />
              {SITE.horto.horario}
            </li>
          </ul>
          <Link to="/horto-florestal" className="botao-destaque">
            Ver localização e horários
          </Link>
        </div>
      </section>
    </div>
  );
}

export default PaginaInicio;
