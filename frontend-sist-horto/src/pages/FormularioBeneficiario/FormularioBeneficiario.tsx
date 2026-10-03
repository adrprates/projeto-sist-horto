import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { ClipboardList, Info } from "lucide-react";
import type { CadastroBeneficiario } from "../../types/CadastroBeneficiario";
import type { Credenciais } from "../../types/Credenciais";
import { cadastrarBeneficiario } from "../../api/beneficiarioService";
import { extrairMensagemErro } from "../../utils/extrairMensagemErro";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import ModalCredenciais from "../../components/ModalCredenciais/ModalCredenciais";
import "./FormularioBeneficiario.css";

const CAMPOS_VAZIOS: CadastroBeneficiario = {
  nome: "",
  cpf: "",
  email: "",
  celular: "",
  telefone: "",
  endereco: "",
};

function FormularioBeneficiario() {
  const [campos, setCampos] = useState<CadastroBeneficiario>(CAMPOS_VAZIOS);
  const [erro, setErro] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [credenciais, setCredenciais] = useState<Credenciais | null>(null);
  const navigate = useNavigate();

  function atualizarCampo<K extends keyof CadastroBeneficiario>(campo: K, valor: string) {
    setCampos((atual) => ({ ...atual, [campo]: valor }));
  }

  async function handleSalvar(evento: FormEvent) {
    evento.preventDefault();
    setErro("");

    if (!campos.nome || !campos.cpf || !campos.email || !campos.celular || !campos.endereco) {
      setErro("Preencha nome, CPF, e-mail, celular e endereço.");
      return;
    }

    if (campos.cpf.replace(/\D/g, "").length !== 11) {
      setErro("O CPF deve conter 11 dígitos.");
      return;
    }

    setSalvando(true);

    try {
      const resposta = await cadastrarBeneficiario({
        ...campos,
        telefone: campos.telefone || undefined,
      });
      setCredenciais(resposta);
    } catch (erroRequisicao) {
      setErro(extrairMensagemErro(erroRequisicao, "Não foi possível cadastrar o beneficiário."));
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div>
      <CabecalhoPagina
        compacto
        voltar={{ rotulo: "Voltar para beneficiários", aoClicar: () => navigate("/beneficiarios") }}
        titulo="Cadastrar beneficiário"
        texto="Para quem prefere ser atendido pela Secretaria: o sistema gera o acesso automaticamente."
      />

      <div className="container-pagina formulario-pagina">
        <p className="formulario-beneficiario-dica">
          <Info size={18} />
          O login será o CPF (somente números) e uma senha provisória será gerada. A pessoa pode
          usar esse acesso para acompanhar o pedido ou nunca entrar no sistema: a Secretaria faz a
          solicitação por ela.
        </p>

        {erro && <p className="formulario-erro">{erro}</p>}

        <form className="formulario" onSubmit={handleSalvar}>
          <section className="formulario-secao">
            <h3>Dados pessoais</h3>

            <div className="campo-grade">
              <div className="campo campo-largura-total">
                <label htmlFor="nome">Nome completo *</label>
                <input
                  id="nome"
                  type="text"
                  value={campos.nome}
                  onChange={(evento) => atualizarCampo("nome", evento.target.value)}
                />
              </div>

              <div className="campo">
                <label htmlFor="cpf">CPF *</label>
                <input
                  id="cpf"
                  type="text"
                  inputMode="numeric"
                  placeholder="000.000.000-00"
                  value={campos.cpf}
                  onChange={(evento) => atualizarCampo("cpf", evento.target.value)}
                />
              </div>

              <div className="campo">
                <label htmlFor="email">E-mail *</label>
                <input
                  id="email"
                  type="email"
                  value={campos.email}
                  onChange={(evento) => atualizarCampo("email", evento.target.value)}
                />
              </div>
            </div>
          </section>

          <section className="formulario-secao">
            <h3>Contato e endereço</h3>

            <div className="campo-grade">
              <div className="campo">
                <label htmlFor="celular">Celular *</label>
                <input
                  id="celular"
                  type="tel"
                  value={campos.celular}
                  onChange={(evento) => atualizarCampo("celular", evento.target.value)}
                />
              </div>

              <div className="campo">
                <label htmlFor="telefone">Telefone</label>
                <input
                  id="telefone"
                  type="tel"
                  value={campos.telefone}
                  onChange={(evento) => atualizarCampo("telefone", evento.target.value)}
                />
              </div>

              <div className="campo campo-largura-total">
                <label htmlFor="endereco">Endereço *</label>
                <input
                  id="endereco"
                  type="text"
                  value={campos.endereco}
                  onChange={(evento) => atualizarCampo("endereco", evento.target.value)}
                />
              </div>
            </div>
          </section>

          <div className="formulario-acoes">
            <button type="submit" className="botao-salvar" disabled={salvando}>
              {salvando ? "Cadastrando..." : "Cadastrar e gerar acesso"}
            </button>
          </div>
        </form>
      </div>

      {credenciais && (
        <ModalCredenciais
          titulo="Beneficiário cadastrado"
          credenciais={credenciais}
          aoFechar={() => navigate("/beneficiarios")}
          acoes={
            <button
              type="button"
              className="botao-primario"
              onClick={() => navigate(`/beneficiarios/${credenciais.beneficiarioId}/solicitacao`)}
            >
              <ClipboardList size={16} />
              Fazer solicitação agora
            </button>
          }
        />
      )}
    </div>
  );
}

export default FormularioBeneficiario;
