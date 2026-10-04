import { useEffect, useState, type FormEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ClipboardList, Info, LockKeyhole, LockKeyholeOpen } from "lucide-react";
import type { CadastroBeneficiario } from "../../types/CadastroBeneficiario";
import type { Credenciais } from "../../types/Credenciais";
import {
  atualizarBeneficiario,
  buscarBeneficiario,
  cadastrarBeneficiario,
  corrigirCpfBeneficiario,
} from "../../api/beneficiarioService";
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

const somenteDigitos = (valor: string) => valor.replace(/\D/g, "");

function FormularioBeneficiario() {
  const { id } = useParams();
  const beneficiarioId = id ? Number(id) : null;
  const ehEdicao = beneficiarioId !== null;

  const [campos, setCampos] = useState<CadastroBeneficiario>(CAMPOS_VAZIOS);
  const [cpfOriginal, setCpfOriginal] = useState("");
  const [loginOriginal, setLoginOriginal] = useState("");
  const [cpfDesbloqueado, setCpfDesbloqueado] = useState(false);
  const [carregando, setCarregando] = useState(ehEdicao);
  const [erro, setErro] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [credenciais, setCredenciais] = useState<Credenciais | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (beneficiarioId === null) {
      return;
    }

    buscarBeneficiario(beneficiarioId)
      .then((dados) => {
        setCampos({
          nome: dados.nome ?? "",
          cpf: dados.cpf ?? "",
          email: dados.email ?? "",
          celular: dados.celular ?? "",
          telefone: dados.telefone ?? "",
          endereco: dados.endereco ?? "",
        });
        setCpfOriginal(dados.cpf ?? "");
        setLoginOriginal(dados.login ?? "");
      })
      .catch(() => setErro("Não foi possível carregar os dados do beneficiário."))
      .finally(() => setCarregando(false));
  }, [beneficiarioId]);

  function atualizarCampo<K extends keyof CadastroBeneficiario>(campo: K, valor: string) {
    setCampos((atual) => ({ ...atual, [campo]: valor }));
  }

  function handleDesbloquearCpf() {
    const mensagem =
      "Corrigir o CPF altera um documento de identificação do beneficiário.\n\n" +
      (somenteDigitos(cpfOriginal) === loginOriginal
        ? "Como o login desta pessoa é o próprio CPF, o login também passará a ser o CPF corrigido.\n\n"
        : "") +
      "Deseja continuar?";

    if (window.confirm(mensagem)) {
      setCpfDesbloqueado(true);
    }
  }

  function handleCancelarCpf() {
    setCpfDesbloqueado(false);
    atualizarCampo("cpf", cpfOriginal);
  }

  async function handleSalvar(evento: FormEvent) {
    evento.preventDefault();
    setErro("");

    if (!campos.nome || !campos.cpf || !campos.email || !campos.celular || !campos.endereco) {
      setErro("Preencha nome, CPF, e-mail, celular e endereço.");
      return;
    }

    if (somenteDigitos(campos.cpf).length !== 11) {
      setErro("O CPF deve conter 11 dígitos.");
      return;
    }

    setSalvando(true);

    try {
      if (beneficiarioId === null) {
        const resposta = await cadastrarBeneficiario({
          ...campos,
          telefone: campos.telefone || undefined,
        });
        setCredenciais(resposta);
        return;
      }

      let atualizado = await atualizarBeneficiario(beneficiarioId, {
        nome: campos.nome,
        email: campos.email,
        celular: campos.celular,
        telefone: campos.telefone || undefined,
        endereco: campos.endereco,
      });

      const cpfAlterado =
        cpfDesbloqueado && somenteDigitos(campos.cpf) !== somenteDigitos(cpfOriginal);

      if (cpfAlterado) {
        atualizado = await corrigirCpfBeneficiario(beneficiarioId, campos.cpf);
      }

      const loginMudou = cpfAlterado && atualizado.login !== loginOriginal;

      navigate("/beneficiarios", {
        state: {
          mensagem: loginMudou
            ? `Dados de ${atualizado.nome} atualizados. O login agora é ${atualizado.login}.`
            : `Dados de ${atualizado.nome} atualizados.`,
        },
      });
    } catch (erroRequisicao) {
      setErro(
        extrairMensagemErro(
          erroRequisicao,
          ehEdicao ? "Não foi possível salvar as alterações." : "Não foi possível cadastrar o beneficiário."
        )
      );
    } finally {
      setSalvando(false);
    }
  }

  const cpfBloqueado = ehEdicao && !cpfDesbloqueado;

  return (
    <div>
      <CabecalhoPagina
        compacto
        voltar={{ rotulo: "Voltar para beneficiários", aoClicar: () => navigate("/beneficiarios") }}
        titulo={ehEdicao ? "Atualizar beneficiário" : "Cadastrar beneficiário"}
        texto={
          ehEdicao
            ? "Corrija os dados cadastrais de quem é atendido pela Secretaria."
            : "Para quem prefere ser atendido pela Secretaria: o sistema gera o acesso automaticamente."
        }
      />

      <div className="container-pagina formulario-pagina">
        {!ehEdicao && (
          <p className="formulario-beneficiario-dica">
            <Info size={18} />
            O login será o CPF (somente números) e uma senha provisória será gerada. A pessoa pode
            usar esse acesso para acompanhar o pedido ou nunca entrar no sistema: a Secretaria faz a
            solicitação por ela.
          </p>
        )}

        {erro && <p className="formulario-erro">{erro}</p>}

        {carregando ? (
          <p className="formulario-carregando">Carregando dados do beneficiário...</p>
        ) : (
          <form className="formulario surgir" onSubmit={handleSalvar}>
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
                  <div className="campo-cpf">
                    <input
                      id="cpf"
                      type="text"
                      inputMode="numeric"
                      placeholder="000.000.000-00"
                      value={campos.cpf}
                      disabled={cpfBloqueado}
                      onChange={(evento) => atualizarCampo("cpf", evento.target.value)}
                    />
                    {ehEdicao &&
                      (cpfDesbloqueado ? (
                        <button
                          type="button"
                          className="campo-cpf-botao"
                          onClick={handleCancelarCpf}
                          title="Cancelar correção do CPF"
                        >
                          <LockKeyhole size={16} />
                          Cancelar
                        </button>
                      ) : (
                        <button
                          type="button"
                          className="campo-cpf-botao"
                          onClick={handleDesbloquearCpf}
                          title="Corrigir CPF digitado errado"
                        >
                          <LockKeyholeOpen size={16} />
                          Corrigir CPF
                        </button>
                      ))}
                  </div>
                  {cpfDesbloqueado && (
                    <p className="campo-dica campo-dica-alerta">
                      Confira o número com um documento antes de salvar.
                    </p>
                  )}
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
                {salvando
                  ? "Salvando..."
                  : ehEdicao
                  ? "Salvar alterações"
                  : "Cadastrar e gerar acesso"}
              </button>
            </div>
          </form>
        )}
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
