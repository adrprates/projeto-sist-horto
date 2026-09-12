import { useEffect, useState, type FormEvent } from "react";
import { Pencil, KeyRound } from "lucide-react";
import type { Perfil } from "../../types/Perfil";
import type { AtualizarPerfil } from "../../types/AtualizarPerfil";
import { obterPerfil, atualizarPerfil } from "../../api/perfilService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import "./PaginaPerfil.css";

function PaginaPerfil() {
  const [perfil, setPerfil] = useState<Perfil>({});
  const [perfilOriginal, setPerfilOriginal] = useState<Perfil>({});

  const [carregando, setCarregando] = useState(true);
  const [editando, setEditando] = useState(false);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");

  useEffect(() => {
    obterPerfil()
      .then((dados) => {
        setPerfil(dados);
        setPerfilOriginal(dados);
      })
      .catch(() => setErro("Não foi possível carregar os dados do perfil."))
      .finally(() => setCarregando(false));
  }, []);

  function atualizarCampo<K extends keyof Perfil>(campo: K, valor: Perfil[K]) {
    setPerfil((atual) => ({ ...atual, [campo]: valor }));
  }

  function handleIniciarEdicao() {
    setErro("");
    setEditando(true);
  }

  function handleCancelar() {
    setPerfil(perfilOriginal);
    setEditando(false);
    setErro("");
  }

  async function handleSalvar(evento: FormEvent) {
    evento.preventDefault();
    setErro("");

    if (!perfil.nome || !perfil.email || !perfil.celular || !perfil.endereco) {
      setErro("Nome, e-mail, celular e endereço são obrigatórios.");
      return;
    }

    const dadosParaSalvar: AtualizarPerfil = {
      nome: perfil.nome,
      email: perfil.email,
      celular: perfil.celular,
      endereco: perfil.endereco,
      telefone: perfil.telefone || undefined,
    };

    setSalvando(true);

    try {
      const perfilAtualizado = await atualizarPerfil(dadosParaSalvar);
      setPerfil(perfilAtualizado);
      setPerfilOriginal(perfilAtualizado);
      setEditando(false);
    } catch {
      setErro("Não foi possível salvar as alterações. Tente novamente.");
    } finally {
      setSalvando(false);
    }
  }

  function handleAlterarSenha() {
    //ainda será implementado
    console.log("Abrir fluxo de alteração de senha");
  }

  if (carregando) {
    return (
      <div>
        <Cabecalho />
        <p className="perfil-carregando">Carregando perfil...</p>
        <Rodape />
      </div>
    );
  }

  return (
    <div>
      <Cabecalho />

      <main className="container-perfil">
        <div className="perfil-cabecalho">
          <h2 className="titulo-perfil">Meu Perfil</h2>

          {!editando && (
            <button type="button" className="botao-editar-perfil" onClick={handleIniciarEdicao}>
              <Pencil size={16} />
              Atualizar Perfil
            </button>
          )}
        </div>

        {erro && <p className="perfil-erro">{erro}</p>}

        <form className="perfil-card" onSubmit={handleSalvar}>
          <div className="campo-grade">
            <div className="campo">
              <label>Nome *</label>
              <input
                type="text"
                value={perfil.nome ?? ""}
                disabled={!editando}
                onChange={(evento) => atualizarCampo("nome", evento.target.value)}
              />
            </div>

            <div className="campo">
              <label>Login</label>
              <input type="text" value={perfil.login ?? ""} disabled />
            </div>

            <div className="campo">
              <label>CPF</label>
              <input type="text" value={perfil.cpf ?? ""} disabled />
            </div>

            <div className="campo">
              <label>E-mail *</label>
              <input
                type="email"
                value={perfil.email ?? ""}
                disabled={!editando}
                onChange={(evento) => atualizarCampo("email", evento.target.value)}
              />
            </div>

            <div className="campo">
              <label>Celular *</label>
              <input
                type="text"
                value={perfil.celular ?? ""}
                disabled={!editando}
                onChange={(evento) => atualizarCampo("celular", evento.target.value)}
              />
            </div>

            <div className="campo">
              <label>Telefone</label>
              <input
                type="text"
                value={perfil.telefone ?? ""}
                disabled={!editando}
                onChange={(evento) => atualizarCampo("telefone", evento.target.value)}
              />
            </div>

            <div className="campo campo-largura-total">
              <label>Endereço *</label>
              <input
                type="text"
                value={perfil.endereco ?? ""}
                disabled={!editando}
                onChange={(evento) => atualizarCampo("endereco", evento.target.value)}
              />
            </div>
          </div>

          {editando && (
            <div className="perfil-acoes">
              <button
                type="button"
                className="botao-cancelar-perfil"
                onClick={handleCancelar}
                disabled={salvando}
              >
                Cancelar
              </button>
              <button type="submit" className="botao-salvar-perfil" disabled={salvando}>
                {salvando ? "Salvando..." : "Salvar"}
              </button>
            </div>
          )}
        </form>

        <div className="perfil-senha">
          <div>
            <p className="perfil-senha-titulo">Senha</p>
            <p className="perfil-senha-descricao">
              Altere sua senha de acesso ao sistema.
            </p>
          </div>
          <button type="button" className="botao-alterar-senha" onClick={handleAlterarSenha}>
            <KeyRound size={16} />
            Alterar Senha
          </button>
        </div>
      </main>

      <Rodape />
    </div>
  );
}

export default PaginaPerfil;