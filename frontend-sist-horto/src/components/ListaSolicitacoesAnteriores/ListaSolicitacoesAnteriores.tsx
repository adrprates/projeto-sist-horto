import { useState } from "react";
import type { SolicitacaoAdmin } from "../../types/SolicitacaoAdmin";
import type { SolicitacaoBeneficiarioResumo } from "../../types/SolicitacaoBeneficiarioResumo";
import { buscarMinhaSolicitacaoDetalhada } from "../../api/solicitacaoService";
import BadgeStatusSolicitacao from "../BadgeStatusSolicitacao/BadgeStatusSolicitacao";
import DetalhesSolicitacaoCartao from "../DetalhesSolicitacaoCartao/DetalhesSolicitacaoCartao";
import "./ListaSolicitacoesAnteriores.css";

type DetalheCarregado = "carregando" | "erro" | SolicitacaoAdmin;

interface ListaSolicitacoesAnterioresProps {
  solicitacoes: SolicitacaoBeneficiarioResumo[];
  carregando: boolean;
}

function ListaSolicitacoesAnteriores({ solicitacoes, carregando }: ListaSolicitacoesAnterioresProps) {
  const [detalhesExpandidos, setDetalhesExpandidos] = useState<Record<number, DetalheCarregado>>(
    {}
  );

  function toggleDetalhes(id: number) {
    setDetalhesExpandidos((atual) => {
      if (atual[id] !== undefined) {
        const copia = { ...atual };
        delete copia[id];
        return copia;
      }
      return { ...atual, [id]: "carregando" };
    });

    if (detalhesExpandidos[id] === undefined) {
      buscarMinhaSolicitacaoDetalhada(id)
        .then((dados) => {
          setDetalhesExpandidos((atual) => ({ ...atual, [id]: dados }));
        })
        .catch(() => {
          setDetalhesExpandidos((atual) => ({ ...atual, [id]: "erro" }));
        });
    }
  }

  if (carregando) {
    return <p className="mensagem-central">Carregando solicitações anteriores...</p>;
  }

  if (solicitacoes.length === 0) {
    return <p className="mensagem-central">Nenhuma solicitação anterior encontrada.</p>;
  }

  return (
    <div className="lista-anteriores">
      {solicitacoes.map((item) => {
        const detalhe = detalhesExpandidos[item.id];

        return (
          <div key={item.id} className="item-anterior">
            <div className="item-anterior-resumo">
              <div>
                <p className="item-anterior-ano">Ano {item.ano}</p>
                <p className="item-anterior-data">{item.dataSolicitacao}</p>
              </div>
              <BadgeStatusSolicitacao status={item.statusAtual} />
              <button
                type="button"
                className="botao-ver-detalhes-anterior"
                onClick={() => toggleDetalhes(item.id)}
              >
                {detalhe !== undefined ? "Ocultar detalhes" : "Ver detalhes"}
              </button>
            </div>

            {detalhe === "carregando" && (
              <p className="mensagem-central">Carregando detalhes...</p>
            )}

            {detalhe === "erro" && (
              <p className="item-anterior-erro">
                Não foi possível carregar os detalhes dessa solicitação.
              </p>
            )}

            {detalhe && detalhe !== "carregando" && detalhe !== "erro" && (
              <div className="item-anterior-detalhe">
                <DetalhesSolicitacaoCartao solicitacao={detalhe} />
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
}

export default ListaSolicitacoesAnteriores;
