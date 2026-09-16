import { useState, type FormEvent } from "react";
import { createPortal } from "react-dom";
import { X } from "lucide-react";
import { atualizarSenha } from "../../api/authService";
import "./ModalAlterarSenha.css";

interface ModalAlterarSenhaProps {
  aoFechar: () => void;
}

function ModalAlterarSenha({ aoFechar }: ModalAlterarSenhaProps) {
  const [senhaAtual, setSenhaAtual] = useState("");
  const [novaSenha, setNovaSenha] = useState("");
  const [confirmarNovaSenha, setConfirmarNovaSenha] = useState("");

  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState(false);
  const [enviando, setEnviando] = useState(false);

  async function handleSubmit(evento: FormEvent) {
    evento.preventDefault();
    setErro("");

    if (novaSenha !== confirmarNovaSenha) {
      setErro("A nova senha e a confirmação não coincidem.");
      return;
    }

    if (novaSenha.length < 6) {
      setErro("A nova senha deve ter pelo menos 6 caracteres.");
      return;
    }

    if (novaSenha === senhaAtual) {
      setErro("A nova senha deve ser diferente da senha atual.");
      return;
    }

    setEnviando(true);

    try {
      await atualizarSenha({ senhaAtual, novaSenha, confirmarNovaSenha });
      setSucesso(true);
    } catch (err) {
      setErro("Erro ao atualizar a senha. Por favor, tente novamente.");
    } finally {
      setEnviando(false);
    }
  }

  const conteudoModal = (
    <div className="modal-fundo" onClick={aoFechar}>
      <div className="modal-senha" onClick={(evento) => evento.stopPropagation()}>
        <button type="button" className="modal-botao-fechar" onClick={aoFechar}>
          <X size={20} />
        </button>

        <h3 className="modal-senha-titulo">Alterar Senha</h3>

        {sucesso ? (
          <div className="modal-senha-sucesso">
            <p>Senha alterada com sucesso!</p>
            <button type="button" className="botao-fechar-sucesso" onClick={aoFechar}>
              Fechar
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="campo">
              <label>Senha atual</label>
              <input
                type="password"
                autoComplete="current-password"
                value={senhaAtual}
                onChange={(evento) => setSenhaAtual(evento.target.value)}
                disabled={enviando}
              />
            </div>

            <div className="campo">
              <label>Nova senha</label>
              <input
                type="password"
                autoComplete="new-password"
                value={novaSenha}
                onChange={(evento) => setNovaSenha(evento.target.value)}
                disabled={enviando}
              />
            </div>

            <div className="campo">
              <label>Confirmar nova senha</label>
              <input
                type="password"
                autoComplete="new-password"
                value={confirmarNovaSenha}
                onChange={(evento) => setConfirmarNovaSenha(evento.target.value)}
                disabled={enviando}
              />
            </div>

            {erro && <p className="modal-senha-erro">{erro}</p>}

            <div className="modal-senha-acoes">
              <button
                type="button"
                className="botao-cancelar-senha"
                onClick={aoFechar}
                disabled={enviando}
              >
                Cancelar
              </button>
              <button type="submit" className="botao-salvar-senha" disabled={enviando}>
                {enviando ? "Salvando..." : "Salvar nova senha"}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );

  return createPortal(conteudoModal, document.body);
}

export default ModalAlterarSenha;