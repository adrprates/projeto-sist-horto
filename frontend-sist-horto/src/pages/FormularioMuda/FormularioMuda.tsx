import { useEffect, useState, type FormEvent } from "react";
import { Trash2, ArrowLeft, X } from "lucide-react";
import type { Muda } from "../../types/Muda";
import { CategoriaMuda, rotuloCategoria } from "../../types/CategoriaMuda";
import { salvar, deletarMuda, buscarMuda } from "../../services/mudaService";
import { atualizarQuantidade } from "../../services/estoqueService";
import "./FormularioMuda.css";

interface MudaComEstoque extends Muda {
  estoqueDisponivel?: number;
}

interface FormularioMudaProps {
  idMudaEdicao?: number;
  aoVoltar: () => void;
  aoSalvarComSucesso: () => void;
}

const MUDA_VAZIA: Muda = {
  nomesPopulares: [],
};

function FormularioMuda({ idMudaEdicao, aoVoltar, aoSalvarComSucesso }: FormularioMudaProps) {
  const ehEdicao = idMudaEdicao !== undefined;

  const [campos, setCampos] = useState<Muda>(MUDA_VAZIA);
  const [estoqueAtual, setEstoqueAtual] = useState(0);
  const [novoValorEstoque, setNovoValorEstoque] = useState(0);
  const [novoNomePopular, setNovoNomePopular] = useState("");

  const [carregando, setCarregando] = useState(ehEdicao);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  useEffect(() => {
    if (!ehEdicao || idMudaEdicao === undefined) {
      return;
    }

    setCarregando(true);
    buscarMuda(idMudaEdicao)
      .then((mudaEncontrada) => {
        const mudaComEstoque = mudaEncontrada as MudaComEstoque;
        setCampos(mudaComEstoque);
        setEstoqueAtual(mudaComEstoque.estoqueDisponivel ?? 0);
        setNovoValorEstoque(mudaComEstoque.estoqueDisponivel ?? 0);
      })
      .catch(() => setErro("Não foi possível carregar os dados da muda."))
      .finally(() => setCarregando(false));
  }, [ehEdicao, idMudaEdicao]);

  function atualizarCampo<K extends keyof Muda>(campo: K, valor: Muda[K]) {
    setCampos((atual) => ({ ...atual, [campo]: valor }));
  }

  function adicionarNomePopular() {
    const nome = novoNomePopular.trim();
    if (!nome || campos.nomesPopulares.includes(nome)) {
      return;
    }
    atualizarCampo("nomesPopulares", [...campos.nomesPopulares, nome]);
    setNovoNomePopular("");
  }

  function removerNomePopular(nome: string) {
    atualizarCampo(
      "nomesPopulares",
      campos.nomesPopulares.filter((item) => item !== nome)
    );
  }

  async function handleSalvar(evento: FormEvent) {
    evento.preventDefault();
    setErro("");

    if (!campos.categoria) {
      setErro("Selecione uma categoria antes de salvar.");
      return;
    }

    if (campos.nomesPopulares.length === 0) {
      setErro("Adicione ao menos um nome popular.");
      return;
    }

    setSalvando(true);

    try {
      await salvar({ ...campos, id: idMudaEdicao });

      if (ehEdicao && idMudaEdicao !== undefined && novoValorEstoque !== estoqueAtual) {
        await atualizarQuantidade(idMudaEdicao, novoValorEstoque);
      }

      aoSalvarComSucesso();
    } catch {
      setErro("Não foi possível salvar a muda. Tente novamente.");
    } finally {
      setSalvando(false);
    }
  }

  async function handleDeletar() {
    if (idMudaEdicao === undefined) {
      return;
    }

    const confirmado = window.confirm(
      "Tem certeza que deseja excluir esta muda? Essa ação não pode ser desfeita."
    );

    if (!confirmado) {
      return;
    }

    try {
      await deletarMuda(idMudaEdicao);
      aoSalvarComSucesso();
    } catch {
      setErro("Não foi possível excluir a muda.");
    }
  }

  if (carregando) {
    return (
      <div className="formulario-pagina">
        <p className="formulario-carregando">Carregando dados da muda...</p>
      </div>
    );
  }

  return (
    <div className="formulario-pagina">
      <div className="formulario-cabecalho">
        <button type="button" className="botao-voltar" onClick={aoVoltar}>
          <ArrowLeft size={18} />
          Voltar ao catálogo
        </button>
        <h1 className="formulario-titulo">
          {ehEdicao ? "Editar Muda" : "Nova Muda"}
        </h1>
      </div>

      {erro && <p className="formulario-erro">{erro}</p>}

      <form className="formulario" onSubmit={handleSalvar}>
        <section className="formulario-secao">
          <h3>Identificação</h3>

          <div className="campo">
            <label>Nomes populares *</label>
            <div className="chips-input">
              <input
                type="text"
                value={novoNomePopular}
                placeholder="Digite um nome e clique em adicionar"
                onChange={(evento) => setNovoNomePopular(evento.target.value)}
                onKeyDown={(evento) => {
                  if (evento.key === "Enter") {
                    evento.preventDefault();
                    adicionarNomePopular();
                  }
                }}
              />
              <button type="button" onClick={adicionarNomePopular}>
                Adicionar
              </button>
            </div>
            <div className="chips-lista">
              {campos.nomesPopulares.map((nome) => (
                <span key={nome} className="chip">
                  {nome}
                  <button type="button" onClick={() => removerNomePopular(nome)}>
                    <X size={12} />
                  </button>
                </span>
              ))}
            </div>
          </div>

          <div className="campo">
            <label>Categoria *</label>
            <select
              value={campos.categoria ?? ""}
              onChange={(evento) =>
                atualizarCampo(
                  "categoria",
                  evento.target.value ? (evento.target.value as CategoriaMuda) : undefined
                )
              }
            >
              <option value="">Selecione uma categoria</option>
              {Object.values(CategoriaMuda).map((categoria) => (
                <option key={categoria} value={categoria}>
                  {rotuloCategoria[categoria]}
                </option>
              ))}
            </select>
          </div>

          <div className="campo-grade">
            <div className="campo">
              <label>Reino</label>
              <input
                type="text"
                maxLength={50}
                value={campos.reino ?? ""}
                onChange={(evento) => atualizarCampo("reino", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Filo</label>
              <input
                type="text"
                maxLength={50}
                value={campos.filo ?? ""}
                onChange={(evento) => atualizarCampo("filo", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Classe</label>
              <input
                type="text"
                maxLength={50}
                value={campos.classe ?? ""}
                onChange={(evento) => atualizarCampo("classe", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Ordem</label>
              <input
                type="text"
                maxLength={50}
                value={campos.ordem ?? ""}
                onChange={(evento) => atualizarCampo("ordem", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Família</label>
              <input
                type="text"
                maxLength={50}
                value={campos.familia ?? ""}
                onChange={(evento) => atualizarCampo("familia", evento.target.value)}
              />
            </div>
          </div>
        </section>

        <section className="formulario-secao">
          <h3>Características</h3>

          <div className="campo-checkboxes">
            <label>
              <input
                type="checkbox"
                checked={campos.possuiFlores ?? false}
                onChange={(evento) => atualizarCampo("possuiFlores", evento.target.checked)}
              />
              Possui flores
            </label>
            <label>
              <input
                type="checkbox"
                checked={campos.possuiFrutos ?? false}
                onChange={(evento) => atualizarCampo("possuiFrutos", evento.target.checked)}
              />
              Possui frutos
            </label>
            <label>
              <input
                type="checkbox"
                checked={campos.perdeMuitasFolhas ?? false}
                onChange={(evento) =>
                  atualizarCampo("perdeMuitasFolhas", evento.target.checked)
                }
              />
              Perde muitas folhas
            </label>
          </div>

          <div className="campo-grade">
            <div className="campo">
              <label>Formato</label>
              <input
                type="text"
                maxLength={100}
                value={campos.formato ?? ""}
                onChange={(evento) => atualizarCampo("formato", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Tamanho</label>
              <input
                type="text"
                maxLength={100}
                value={campos.tamanho ?? ""}
                onChange={(evento) => atualizarCampo("tamanho", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Raízes</label>
              <input
                type="text"
                maxLength={100}
                value={campos.raizes ?? ""}
                onChange={(evento) => atualizarCampo("raizes", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Cor da flor</label>
              <input
                type="text"
                maxLength={50}
                value={campos.corFlor ?? ""}
                onChange={(evento) => atualizarCampo("corFlor", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Tipos de flores</label>
              <input
                type="text"
                maxLength={150}
                value={campos.tiposFlores ?? ""}
                onChange={(evento) => atualizarCampo("tiposFlores", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Época das flores</label>
              <input
                type="text"
                maxLength={100}
                value={campos.epocaFlores ?? ""}
                onChange={(evento) => atualizarCampo("epocaFlores", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Tipos de frutos</label>
              <input
                type="text"
                maxLength={150}
                value={campos.tiposFrutos ?? ""}
                onChange={(evento) => atualizarCampo("tiposFrutos", evento.target.value)}
              />
            </div>
            <div className="campo">
              <label>Época dos frutos</label>
              <input
                type="text"
                maxLength={100}
                value={campos.epocaFrutos ?? ""}
                onChange={(evento) => atualizarCampo("epocaFrutos", evento.target.value)}
              />
            </div>
          </div>
        </section>

        <section className="formulario-secao">
          <h3>Imagens</h3>

          <div className="campo">
            <label>Link da imagem da árvore</label>
            <input
              type="text"
              maxLength={255}
              value={campos.linkImagemArvore ?? ""}
              onChange={(evento) => atualizarCampo("linkImagemArvore", evento.target.value)}
            />
          </div>
          <div className="campo">
            <label>Link da imagem das flores</label>
            <input
              type="text"
              maxLength={255}
              value={campos.linkImagemFlores ?? ""}
              onChange={(evento) => atualizarCampo("linkImagemFlores", evento.target.value)}
            />
          </div>
          <div className="campo">
            <label>Link da imagem dos frutos</label>
            <input
              type="text"
              maxLength={255}
              value={campos.linkImagemFrutos ?? ""}
              onChange={(evento) => atualizarCampo("linkImagemFrutos", evento.target.value)}
            />
          </div>
        </section>

        {ehEdicao && (
          <section className="formulario-secao">
            <h3>Estoque</h3>
            <p className="formulario-dica">
              Estoque atual: <strong>{estoqueAtual}</strong>
            </p>
            <div className="campo">
              <label>Novo valor do estoque</label>
              <input
                type="number"
                min={0}
                value={novoValorEstoque}
                onChange={(evento) => setNovoValorEstoque(Number(evento.target.value))}
              />
            </div>
          </section>
        )}

        <div className="formulario-acoes">
          <button type="submit" className="botao-salvar" disabled={salvando}>
            {salvando ? "Salvando..." : "Salvar"}
          </button>
        </div>
      </form>

      {ehEdicao && (
        <div className="formulario-zona-perigo">
          <p>Excluir esta muda remove ela permanentemente do catálogo.</p>
          <button type="button" className="botao-excluir" onClick={handleDeletar}>
            <Trash2 size={16} />
            Excluir muda
          </button>
        </div>
      )}
    </div>
  );
}

export default FormularioMuda;