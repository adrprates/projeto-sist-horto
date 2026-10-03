import { useState } from "react";
import { useNavigate, useLocation, Link as RouterLink } from "react-router-dom";
import { useAuth } from "../../../hooks/useAuth";
import { loginUser } from "../../../api/authService";
import { Button, TextField, Alert, Link } from "@mui/material";
import CabecalhoPagina from "../../../components/CabecalhoPagina/CabecalhoPagina";
import PainelAutenticacao from "../../../components/PainelAutenticacao/PainelAutenticacao";
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
      navigate("/catalogo");
    } catch (err) {
      setError("Falha no login. Verifique suas credenciais.");
      console.error(err);
    }
  };

  return (
    <div>
      <CabecalhoPagina
        compacto
        titulo="Entrar no sistema"
        texto="Acesse sua conta para montar e acompanhar suas solicitações de mudas."
      />

      <div className="container-pagina pagina-autenticacao pagina-autenticacao-estreita">
        <div className="cartao-autenticacao">
          <h2 className="cartao-autenticacao-titulo">Acesse sua conta</h2>
          <p className="cartao-autenticacao-texto">Informe o login e a senha cadastrados.</p>

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

        <PainelAutenticacao />
      </div>
    </div>
  );
};
