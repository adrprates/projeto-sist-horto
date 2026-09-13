import { useState } from "react";
import axios from "axios";
import { useNavigate, Link as RouterLink } from "react-router-dom";
import { registrarUsuario } from "../../../api/authService";
import type { RegisterRequest } from "../../../api/authService";
import { Leaf } from "lucide-react";
import { Avatar, Button, TextField, Alert, Link } from "@mui/material";
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

    if (campos.senha !== campos.confirmarSenha) {
      setErro("As senhas não coincidem.");
      return;
    }

    if (campos.senha.length < 6) {
      setErro("A senha deve ter pelo menos 6 caracteres.");
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

       if (axios.isAxiosError(erro)) {
          setErro(
            erro.response?.data?.message ??
            "Não foi possível concluir o cadastro."
          );
      } else {
          setErro("Erro inesperado.");
      }

      console.error(erro);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="pagina-autenticacao">
      <div className="cartao-autenticacao cartao-autenticacao-largo">
        <Avatar className="avatar-logo">
          <Leaf size={28} />
        </Avatar>

        <h1 className="titulo-autenticacao">Sistema Horto</h1>
        <p className="subtitulo-autenticacao">Criar sua conta</p>

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
              onChange={(e) => atualizarCampo("cpf", e.target.value)}
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
              onChange={(e) => atualizarCampo("celular", e.target.value)}
            />

            <TextField
              fullWidth
              label="Telefone (opcional)"
              value={campos.telefone}
              onChange={(e) => atualizarCampo("telefone", e.target.value)}
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
    </div>
  );
};