import { Sprout, User, BarChart3 } from "lucide-react";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import "./PaginaInicial.css";

interface PaginaInicialProps {
  nomeUsuario?: string;
}

function PaginaInicial({ nomeUsuario = "visitante" }: PaginaInicialProps) {
  return (
    <div>
      <Cabecalho />

      <main>
        <section className="banner">
          <div className="banner-conteudo">
            <h1 className="banner-titulo">
              <span className="banner-titulo-boas-vindas">
                Bem-vindo ao Sistema Horto!
              </span>
              <span className="banner-titulo-saudacao">
                Olá, <span className="banner-nome">{nomeUsuario}</span>
              </span>
            </h1>

            <div className="banner-card-texto">
              <h4 className="banner-descricao">
                Aqui você pode gerenciar suas requisições de mudas e
                consultar seu perfil
              </h4>
            </div>

            <button type="button" className="botao-destaque">
              Ver Catálogo de Mudas
            </button>
          </div>
        </section>

        <section className="cards">
          <div className="card-inicio">
            <Sprout className="card-inicio-icone" size={32} />
            <p className="card-inicio-titulo">Minhas Mudas</p>
            <button type="button" className="card-inicio-botao">
              Ver minhas mudas
            </button>
          </div>

          <div className="card-inicio">
            <User className="card-inicio-icone" size={32} />
            <p className="card-inicio-titulo">Meu Perfil</p>
            <button type="button" className="card-inicio-botao">
              Ver meu perfil
            </button>
          </div>

          <div className="card-inicio">
            <BarChart3 className="card-inicio-icone" size={32} />
            <p className="card-inicio-titulo">Central de Relatórios</p>
            <button type="button" className="card-inicio-botao">
              Ir para relatórios
            </button>
          </div>
        </section>
      </main>

      <Rodape />
    </div>
  );
}

export default PaginaInicial;