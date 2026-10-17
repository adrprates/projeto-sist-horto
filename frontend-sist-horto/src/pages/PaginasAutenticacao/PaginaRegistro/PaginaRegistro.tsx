import { useState } from "react";
import { useNavigate, Link as RouterLink } from "react-router-dom";
import { registrarUsuario } from "../../../api/authService";
import { extrairMensagemErro } from "../../../utils/extrairMensagemErro";
import { mascararCpf, mascararTelefone } from "../../../utils/mascaras";
import type { RegisterRequest } from "../../../api/authService";
import { Button, TextField, Alert, Link } from "@mui/material";
import CabecalhoPagina from "../../../components/CabecalhoPagina/CabecalhoPagina";
import PainelAutenticacao from "../../../components/PainelAutenticacao/PainelAutenticacao";
import "./PaginaRegistro.css";

type CamposRegistro = RegisterRequest & { confirmarSenha: string };

const CAMPOS_VAZIOS: CamposRegistro = {
  cpf: "",
  celular: "",
  telefone: "",
  email: "",
  nome: "",
  endereco: "",
  login: "",
  senha: "",
  confirmarSenha: "",
};

export const PaginaRegistro = () => {
  const [campos, setCampos] = useState<CamposRegistro>(CAMPOS_VAZIOS);
  const [erro, setErro] = useState("");
  const [enviando, setEnviando] = useState(false);
  const navigate = useNavigate();

  function atualizarCampo<K extends keyof CamposRegistro>(campo: K, valor: string) {
    setCampos((atual) => ({ ...atual, [campo]: valor }));
  }

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErro("");

    const digitos = (valor: string) => valor.replace(/\D/g, "");

    if (digitos(campos.cpf).length !== 11) {
      setErro("O CPF deve conter 11 dígitos.");
      return;
    }

    if (digitos(campos.celular).length !== 11) {
      setErro("Informe o celular com DDD: (00) 00000-0000.");
      return;
    }

    if (campos.telefone && digitos(campos.telefone).length !== 10) {
      setErro("Informe o telefone com DDD: (00) 0000-0000.");
      return;
    }

    const dadosParaEnviar: RegisterRequest = {
      cpf: campos.cpf,
      celular: campos.celular,
      telefone: campos.telefone || undefined,
      email: campos.email,
      nome: campos.nome,
      endereco: campos.endereco,
      login: campos.login,
      senha: campos.senha,
      confirmarSenha: campos.confirmarSenha,
    };

    setEnviando(true);

    try {
      await registrarUsuario(dadosParaEnviar);

      navigate("/login", {
        state: {
          mensagemSucesso:
            "Cadastro realizado com sucesso! Faça login para continuar.",
        },
      });
    } catch (erro) {
      setErro(
        extrairMensagemErro(erro, "Não foi possível concluir o cadastro. Por favor, tente novamente.")
      );
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div>
      <CabecalhoPagina
        compacto
        titulo="Criar sua conta"
        texto="O cadastro é gratuito e necessário para solicitar mudas do Horto."
      />

      <div className="container-pagina pagina-autenticacao">
        <div className="cartao-autenticacao">
          <h2 className="cartao-autenticacao-titulo">Seus dados</h2>
          <p className="cartao-autenticacao-texto">Campos com * são obrigatórios.</p>

          <form className="formulario-autenticacao" onSubmit={handleSubmit} noValidate>
            <div className="grade-registro">
              <TextField
                required
                fullWidth
                label="Nome completo"
                value={campos.nome}
                onChange={(e) => atualizarCampo("nome", e.target.value)}
                className="campo-largura-total"
              />

              <TextField
                required
                fullWidth
                label="CPF"
                value={campos.cpf}
                placeholder="000.000.000-00"
                slotProps={{ htmlInput: { inputMode: "numeric" } }}
                onChange={(e) => atualizarCampo("cpf", mascararCpf(e.target.value))}
              />

              <TextField
                required
                fullWidth
                type="email"
                label="E-mail"
                value={campos.email}
                onChange={(e) => atualizarCampo("email", e.target.value)}
              />

              <TextField
                required
                fullWidth
                label="Celular"
                value={campos.celular}
                placeholder="(00) 00000-0000"
                slotProps={{ htmlInput: { inputMode: "tel" } }}
                onChange={(e) => atualizarCampo("celular", mascararTelefone(e.target.value))}
              />

              <TextField
                fullWidth
                label="Telefone (opcional)"
                value={campos.telefone}
                placeholder="(00) 0000-0000"
                slotProps={{ htmlInput: { inputMode: "tel" } }}
                onChange={(e) => atualizarCampo("telefone", mascararTelefone(e.target.value))}
              />

              <TextField
                required
                fullWidth
                label="Endereço"
                value={campos.endereco}
                onChange={(e) => atualizarCampo("endereco", e.target.value)}
                className="campo-largura-total"
              />

              <TextField
                required
                fullWidth
                label="Login"
                autoComplete="username"
                value={campos.login}
                onChange={(e) => atualizarCampo("login", e.target.value)}
                className="campo-largura-total"
              />

              <TextField
                required
                fullWidth
                type="password"
                label="Senha"
                autoComplete="new-password"
                value={campos.senha}
                onChange={(e) => atualizarCampo("senha", e.target.value)}
              />

              <TextField
                required
                fullWidth
                type="password"
                label="Confirmar senha"
                autoComplete="new-password"
                value={campos.confirmarSenha}
                onChange={(e) => atualizarCampo("confirmarSenha", e.target.value)}
              />
            </div>

            {erro && (
              <Alert severity="error" className="alerta-autenticacao">
                {erro}
              </Alert>
            )}

            <Button
              type="submit"
              fullWidth
              variant="contained"
              disabled={enviando}
              className="botao-autenticacao"
            >
              {enviando ? "Cadastrando..." : "Cadastrar"}
            </Button>

            <p className="rodape-autenticacao">
              Já tem uma conta?{" "}
              <Link component={RouterLink} to="/login" className="link-autenticacao">
                Entrar
              </Link>
            </p>
          </form>
        </div>

        <PainelAutenticacao />
      </div>
    </div>
  );
};
