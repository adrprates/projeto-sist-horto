import { useState } from "react";
import { useNavigate, useLocation, Link as RouterLink } from "react-router-dom";
import { useAuth } from "../../../hooks/useAuth";
import { loginUser } from "../../../api/authService";
import { Leaf } from "lucide-react";
import { Avatar, Button, TextField, Alert, Link } from "@mui/material";
import "./PaginaLogin.css";

export const PaginaLogin = () => {
  const [login, setLogin] = useState("");
  const [senha, setSenha] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const auth = useAuth();

  const mensagemSucesso = (location.state as { mensagemSucesso?: string } | null)
    ?.mensagemSucesso;

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError("");

    try {
      const response = await loginUser({ login, senha });
      auth.login(response.token);
      navigate("/");
    } catch (err) {
      setError("Falha no login. Verifique suas credenciais.");
      console.error(err);
    }
  };

  return (
    <div className="pagina-autenticacao">
      <div className="cartao-autenticacao">
        <Avatar className="avatar-logo">
          <Leaf size={28} />
        </Avatar>

        <h1 className="titulo-autenticacao">Sistema Horto</h1>
        <p className="subtitulo-autenticacao">Entrar na sua conta</p>

        {mensagemSucesso && (
          <Alert severity="success" className="alerta-autenticacao">
            {mensagemSucesso}
          </Alert>
        )}

        <form className="formulario-autenticacao" onSubmit={handleSubmit} noValidate>
          <TextField
            margin="normal"
            required
            fullWidth
            id="login"
            label="Login"
            name="login"
            autoComplete="username"
            autoFocus
            value={login}
            onChange={(e) => setLogin(e.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="senha"
            label="Senha"
            type="password"
            id="senha"
            autoComplete="current-password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
          />

          {error && (
            <Alert severity="error" className="alerta-autenticacao">
              {error}
            </Alert>
          )}

          <Button type="submit" fullWidth variant="contained" className="botao-autenticacao">
            Entrar
          </Button>

          <p className="rodape-autenticacao">
            Não tem uma conta?{" "}
            <Link component={RouterLink} to="/registro" className="link-autenticacao">
              Cadastre-se
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};