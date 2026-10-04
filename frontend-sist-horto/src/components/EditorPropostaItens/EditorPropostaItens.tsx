import { useEffect, useState } from "react";
import { Plus, Trash2, Undo2 } from "lucide-react";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import type { CategoriaMuda } from "../../types/CategoriaMuda";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import { listarMudas } from "../../api/mudaService";
import SeletorQuantidade from "../SeletorQuantidade/SeletorQuantidade";
import "./EditorPropostaItens.css";

export interface LinhaProposta {
  mudaId: number;
  nome: string;
  categoria: CategoriaMuda;
  quantidadeAtual: number;
  quantidade: number;
}

interface EditorPropostaItensProps {
  linhas: LinhaProposta[];
  aoAlterar: (linhas: LinhaProposta[]) => void;
  desabilitado?: boolean;
}

function EditorPropostaItens({ linhas, aoAlterar, desabilitado = false }: EditorPropostaItensProps) {
  const [mudas, setMudas] = useState<DadosMudaResumo[]>([]);
  const [mudaParaIncluir, setMudaParaIncluir] = useState("");

  useEffect(() => {
    listarMudas({})
      .then(setMudas)
      .catch(() => setMudas([]));
  }, []);

  function alterarQuantidade(mudaId: number, quantidade: number) {
    aoAlterar(linhas.map((linha) => (linha.mudaId === mudaId ? { ...linha, quantidade } : linha)));
  }

  function remover(mudaId: number) {
    const linha = linhas.find((item) => item.mudaId === mudaId);

    if (linha && linha.quantidadeAtual > 0) {
      alterarQuantidade(mudaId, 0);
      return;
    }

    aoAlterar(linhas.filter((item) => item.mudaId !== mudaId));
  }

  function restaurar(mudaId: number) {
    const linha = linhas.find((item) => item.mudaId === mudaId);
    if (linha) {
      alterarQuantidade(mudaId, linha.quantidadeAtual);
    }
  }

  function incluir() {
    const muda = mudas.find((item) => item.id === Number(mudaParaIncluir));

    if (!muda) {
      return;
    }

    aoAlterar([
      ...linhas,
      {
        mudaId: muda.id,
        nome: muda.nomesPopulares.slice(0, 2).join(", "),
        categoria: muda.categoria,
        quantidadeAtual: 0,
        quantidade: 1,
      },
    ]);
    setMudaParaIncluir("");
  }

  const idsIncluidos = new Set(linhas.map((linha) => linha.mudaId));
  const opcoesInclusao = mudas.filter(
    (muda) => !idsIncluidos.has(muda.id) && muda.disponivel && muda.estoqueDisponivel > 0
  );

  return (
    <div className="editor-proposta">
      <ul className="editor-proposta-lista">
        {linhas.map((linha) => {
          const removida = linha.quantidade === 0;
          const alterada = linha.quantidade !== linha.quantidadeAtual;
          const nova = linha.quantidadeAtual === 0;

          let classe = "editor-proposta-linha";
          if (removida) classe += " editor-proposta-linha-removida";
          else if (nova) classe += " editor-proposta-linha-nova";
          else if (alterada) classe += " editor-proposta-linha-alterada";

          return (
            <li key={linha.mudaId} className={classe}>
              <div className="editor-proposta-info">
                <p className="editor-proposta-nome">{linha.nome}</p>
                <span className="tag">{rotuloCategoria[linha.categoria]}</span>
                <span className="editor-proposta-original">
                  {nova
                    ? "Nova muda na proposta"
                    : removida
                    ? `Será removida (atual: ${linha.quantidadeAtual})`
                    : `Atual: ${linha.quantidadeAtual} un.`}
                </span>
              </div>

              {removida ? (
                <button
                  type="button"
                  className="editor-proposta-botao"
                  onClick={() => restaurar(linha.mudaId)}
                  disabled={desabilitado}
                >
                  <Undo2 size={14} />
                  Manter
                </button>
              ) : (
                <>
                  <SeletorQuantidade
                    valor={linha.quantidade}
                    aoAlterar={(valor) => alterarQuantidade(linha.mudaId, valor)}
                    minimo={1}
                    desabilitado={desabilitado}
                  />
                  <button
                    type="button"
                    className="editor-proposta-remover"
                    onClick={() => remover(linha.mudaId)}
                    disabled={desabilitado}
                    aria-label={`Remover ${linha.nome} da proposta`}
                  >
                    <Trash2 size={15} />
                  </button>
                </>
              )}
            </li>
          );
        })}
      </ul>

      <div className="editor-proposta-incluir">
        <select
          value={mudaParaIncluir}
          onChange={(evento) => setMudaParaIncluir(evento.target.value)}
          disabled={desabilitado}
        >
          <option value="">Incluir outra muda na proposta...</option>
          {opcoesInclusao.map((muda) => (
            <option key={muda.id} value={muda.id}>
              {muda.nomesPopulares.slice(0, 2).join(", ")} ({muda.estoqueDisponivel} disponíveis)
            </option>
          ))}
        </select>
        <button
          type="button"
          className="editor-proposta-botao"
          onClick={incluir}
          disabled={desabilitado || !mudaParaIncluir}
        >
          <Plus size={14} />
          Incluir
        </button>
      </div>
    </div>
  );
}

export default EditorPropostaItens;
