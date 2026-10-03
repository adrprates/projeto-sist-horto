import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, Pencil, Trash2 } from "lucide-react";
import type { ParametroAnual } from "../../types/ParametroAnual";
import { listarParametrosAnuais, deletar } from "../../api/parametroAnualService";
import "./ListaParametrosAnuais.css";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";

function ListaParametrosAnuais() {
  const [parametros, setParametros] = useState<ParametroAnual[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const navigate = useNavigate();

  function carregarLista() {
    setCarregando(true);
    listarParametrosAnuais()
      .then(setParametros)
      .catch(() => setErro("Não foi possível carregar os parâmetros anuais."))
      .finally(() => setCarregando(false));
  }

  useEffect(() => {
    carregarLista();
  }, []);

  async function handleExcluir(ano: number) {
    const confirmado = window.confirm(
      `Tem certeza que deseja excluir os parâmetros do ano ${ano}? Essa ação não pode ser desfeita.`
    );

    if (!confirmado) {
      return;
    }

    try {
      await deletar(ano);
      carregarLista();
    } catch {
      setErro(`Não foi possível excluir os parâmetros do ano ${ano}.`);
    }
  }

  return (
    <div>
      <CabecalhoPagina
        compacto
        titulo="Parâmetros anuais"
        texto="Limites de mudas por ano, por categoria e por espécie."
        acoes={
          <button
            type="button"
            className="botao-destaque"
            onClick={() => navigate("/parametros/novo")}
          >
            <Plus size={18} />
            Novo parâmetro
          </button>
        }
      />

      <section className="container-pagina container-parametros">

        {erro && <p className="parametros-erro">{erro}</p>}

        {carregando && <p className="mensagem-central">Carregando parâmetros...</p>}

        {!carregando && parametros.length === 0 && (
          <p className="mensagem-central">Nenhum parâmetro anual cadastrado ainda.</p>
        )}

        {!carregando && parametros.length > 0 && (
          <div className="tabela-parametros-wrapper">
            <table className="tabela-parametros">
              <thead>
                <tr>
                  <th>Ano</th>
                  <th>Limite Frutíferas</th>
                  <th>Limite Outras</th>
                  <th>Limite Total</th>
                  <th>Máx./Espécie Frutífera</th>
                  <th>Máx./Espécie Outras</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {parametros.map((parametro) => (
                  <tr key={parametro.ano}>
                    <td className="celula-ano">{parametro.ano}</td>
                    <td>{parametro.limiteFrutiferas}</td>
                    <td>{parametro.limiteOutras}</td>
                    <td>{parametro.limiteTotalMudas}</td>
                    <td>{parametro.maxPorEspecieFrutifera}</td>
                    <td>{parametro.maxPorEspecieOutras}</td>
                    <td>
                      <div className="tabela-acoes">
                        <button
                          type="button"
                          className="botao-tabela-editar"
                          onClick={() => navigate(`/parametros/${parametro.ano}/editar`)}
                        >
                          <Pencil size={14} />
                          Editar
                        </button>
                        <button
                          type="button"
                          className="botao-tabela-excluir"
                          onClick={() => handleExcluir(parametro.ano)}
                        >
                          <Trash2 size={14} />
                          Excluir
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

    </div>
  );
}

export default ListaParametrosAnuais;