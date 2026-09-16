import { useEffect, useState, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, Trash2 } from "lucide-react";
import type { ParametroAnual } from "../../types/ParametroAnual";
import {
  buscarParametroAnual,
  salvar,
  deletar,
} from "../../api/parametroAnualService";
import "./FormularioParametroAnual.css";

const CAMPOS_VAZIOS: ParametroAnual = {
  ano: new Date().getFullYear(),
  limiteFrutiferas: 0,
  limiteOutras: 0,
  limiteTotalMudas: 0,
  maxPorEspecieFrutifera: 0,
  maxPorEspecieOutras: 0,
};

function FormularioParametroAnual() {
  const { ano: anoParam } = useParams<{ ano: string }>();
  const ehEdicao = anoParam !== undefined;
  const navigate = useNavigate();

  const [campos, setCampos] = useState<ParametroAnual>(CAMPOS_VAZIOS);
  const [carregando, setCarregando] = useState(ehEdicao);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  useEffect(() => {
    if (!ehEdicao || anoParam === undefined) {
      return;
    }

    setCarregando(true);
    buscarParametroAnual(Number(anoParam))
      .then(setCampos)
      .catch(() => setErro("Não foi possível carregar os dados desse ano."))
      .finally(() => setCarregando(false));
  }, [ehEdicao, anoParam]);

  function atualizarCampo<K extends keyof ParametroAnual>(campo: K, valor: number) {
    setCampos((atual) => ({ ...atual, [campo]: valor }));
  }

  function valoresValidos(): boolean {
    const numeros = [
      campos.ano,
      campos.limiteFrutiferas,
      campos.limiteOutras,
      campos.limiteTotalMudas,
      campos.maxPorEspecieFrutifera,
      campos.maxPorEspecieOutras,
    ];

    return numeros.every((valor) => !Number.isNaN(valor) && valor >= 0);
  }

  async function handleSalvar(evento: FormEvent) {
    evento.preventDefault();
    setErro("");

    if (!campos.ano) {
      setErro("Informe o ano.");
      return;
    }

    if (!valoresValidos()) {
      setErro("Todos os valores devem ser números iguais ou maiores que zero.");
      return;
    }

    setSalvando(true);

    try {
      await salvar(campos, ehEdicao);
      navigate("/parametros");
    } catch {
      setErro(
        ehEdicao
          ? "Não foi possível salvar as alterações."
          : "Não foi possível criar o parâmetro. Verifique se o ano já não existe."
      );
    } finally {
      setSalvando(false);
    }
  }

  async function handleExcluir() {
    const confirmado = window.confirm(
      `Tem certeza que deseja excluir os parâmetros do ano ${campos.ano}? Essa ação não pode ser desfeita.`
    );

    if (!confirmado) {
      return;
    }

    try {
      await deletar(campos.ano);
      navigate("/parametros");
    } catch {
      setErro("Não foi possível excluir esse parâmetro.");
    }
  }

  if (carregando) {
    return (
      <div className="formulario-pagina">
        <p className="formulario-carregando">Carregando dados do parâmetro...</p>
      </div>
    );
  }

  return (
    <div className="formulario-pagina">
      <div className="formulario-cabecalho">
        <button type="button" className="botao-voltar" onClick={() => navigate("/parametros")}>
          <ArrowLeft size={18} />
          Voltar para Parâmetros Anuais
        </button>
        <h1 className="formulario-titulo">
          {ehEdicao ? `Editar Parâmetros — ${campos.ano}` : "Novo Parâmetro Anual"}
        </h1>
      </div>

      {erro && <p className="formulario-erro">{erro}</p>}

      <form className="formulario" onSubmit={handleSalvar}>
        <section className="formulario-secao">
          <h3>Identificação</h3>

          <div className="campo">
            <label>Ano *</label>
            <input
              type="number"
              value={campos.ano}
              disabled={ehEdicao}
              onChange={(evento) => atualizarCampo("ano", Number(evento.target.value))}
            />
            {ehEdicao && (
              <p className="campo-dica">O ano não pode ser alterado após a criação.</p>
            )}
          </div>
        </section>

        <section className="formulario-secao">
          <h3>Limites gerais</h3>

          <div className="campo-grade">
            <div className="campo">
              <label>Limite de Frutíferas *</label>
              <input
                type="number"
                min={0}
                value={campos.limiteFrutiferas}
                onChange={(evento) =>
                  atualizarCampo("limiteFrutiferas", Number(evento.target.value))
                }
              />
            </div>

            <div className="campo">
              <label>Limite de Outras Categorias *</label>
              <input
                type="number"
                min={0}
                value={campos.limiteOutras}
                onChange={(evento) => atualizarCampo("limiteOutras", Number(evento.target.value))}
              />
            </div>

            <div className="campo campo-largura-total">
              <label>Limite Total de Mudas *</label>
              <input
                type="number"
                min={0}
                value={campos.limiteTotalMudas}
                onChange={(evento) =>
                  atualizarCampo("limiteTotalMudas", Number(evento.target.value))
                }
              />
            </div>
          </div>
        </section>

        <section className="formulario-secao">
          <h3>Limites por espécie</h3>

          <div className="campo-grade">
            <div className="campo">
              <label>Máximo por Espécie Frutífera *</label>
              <input
                type="number"
                min={0}
                value={campos.maxPorEspecieFrutifera}
                onChange={(evento) =>
                  atualizarCampo("maxPorEspecieFrutifera", Number(evento.target.value))
                }
              />
            </div>

            <div className="campo">
              <label>Máximo por Espécie de Outras Categorias *</label>
              <input
                type="number"
                min={0}
                value={campos.maxPorEspecieOutras}
                onChange={(evento) =>
                  atualizarCampo("maxPorEspecieOutras", Number(evento.target.value))
                }
              />
            </div>
          </div>
        </section>

        <div className="formulario-acoes">
          <button type="submit" className="botao-salvar" disabled={salvando}>
            {salvando ? "Salvando..." : "Salvar"}
          </button>
        </div>
      </form>

      {ehEdicao && (
        <div className="formulario-zona-perigo">
          <p>Excluir remove permanentemente os parâmetros deste ano.</p>
          <button type="button" className="botao-excluir" onClick={handleExcluir}>
            <Trash2 size={16} />
            Excluir parâmetro
          </button>
        </div>
      )}
    </div>
  );
}

export default FormularioParametroAnual;