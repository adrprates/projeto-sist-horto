import { useState, type ReactNode } from "react";
import { createPortal } from "react-dom";
import { Check, Copy, KeyRound, Printer, Sprout, X } from "lucide-react";
import type { Credenciais } from "../../types/Credenciais";
import { SITE } from "../../config/site";
import "./ModalCredenciais.css";

interface ModalCredenciaisProps {
  credenciais: Credenciais;
  titulo: string;
  aoFechar: () => void;
  acoes?: ReactNode;
}

function ModalCredenciais({ credenciais, titulo, aoFechar, acoes }: ModalCredenciaisProps) {
  const [copiado, setCopiado] = useState(false);

  async function handleCopiar() {
    const texto = `Login: ${credenciais.login}\nSenha provisória: ${credenciais.senhaProvisoria}`;

    try {
      await navigator.clipboard.writeText(texto);
      setCopiado(true);
      setTimeout(() => setCopiado(false), 2000);
    } catch {
      setCopiado(false);
    }
  }

  return createPortal(
    <div className="credenciais-fundo">
      <div className="credenciais-modal" role="dialog" aria-modal="true" aria-label={titulo}>
        <button type="button" className="credenciais-fechar" onClick={aoFechar} aria-label="Fechar">
          <X size={20} />
        </button>

        <h2 className="credenciais-titulo">{titulo}</h2>
        <p className="credenciais-aviso">
          Anote ou imprima agora: por segurança, esta senha não será exibida novamente.
        </p>

        <div className="credenciais-cartao">
          <div className="credenciais-cartao-marca">
            <span className="credenciais-cartao-icone">
              <Sprout size={18} />
            </span>
            {SITE.nome}
          </div>

          <p className="credenciais-cartao-nome">{credenciais.nome}</p>

          <dl className="credenciais-dados">
            <div>
              <dt>Login</dt>
              <dd>{credenciais.login}</dd>
            </div>
            <div>
              <dt>Senha provisória</dt>
              <dd>{credenciais.senhaProvisoria}</dd>
            </div>
          </dl>

          <p className="credenciais-cartao-instrucao">
            <KeyRound size={14} />
            No primeiro acesso, o sistema pedirá para criar uma senha nova.
          </p>
        </div>

        <div className="credenciais-acoes">
          <button type="button" className="credenciais-botao-secundario" onClick={handleCopiar}>
            {copiado ? <Check size={16} /> : <Copy size={16} />}
            {copiado ? "Copiado" : "Copiar"}
          </button>
          <button type="button" className="credenciais-botao-secundario" onClick={() => window.print()}>
            <Printer size={16} />
            Imprimir
          </button>
          {acoes}
        </div>
      </div>
    </div>,
    document.body
  );
}

export default ModalCredenciais;
