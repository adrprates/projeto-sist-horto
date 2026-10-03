import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ClipboardList, KeyRound, Pencil, UserPlus } from "lucide-react";
import type { DadosBeneficiario } from "../../types/DadosBeneficiario";
import type { BeneficiarioFilter } from "../../types/BeneficiarioFilter";
import type { Credenciais } from "../../types/Credenciais";
import { Role, rotuloRole } from "../../types/Role";
import { listarBeneficiarios, redefinirSenhaBeneficiario } from "../../api/beneficiarioService";
import { extrairMensagemErro } from "../../utils/extrairMensagemErro";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import FiltrosBeneficiarios from "../../components/FiltrosBeneficiarios/FiltrosBeneficiarios";
import ModalCredenciais from "../../components/ModalCredenciais/ModalCredenciais";
import "./ListaBeneficiarios.css";

function ListaBeneficiarios() {
  const [beneficiarios, setBeneficiarios] = useState<DadosBeneficiario[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState<BeneficiarioFilter>({});
  const [credenciais, setCredenciais] = useState<Credenciais | null>(null);
  const [erro, setErro] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    setCarregando(true);
    listarBeneficiarios(filtro)
      .then(setBeneficiarios)
      .finally(() => setCarregando(false));
  }, [filtro]);

  async function handleNovaSenha(beneficiario: DadosBeneficiario) {
    if (!beneficiario.id) {
      return;
    }

    const confirmado = window.confirm(
      `Gerar uma nova senha provisória para ${beneficiario.nome}? A senha atual deixará de funcionar.`
    );

    if (!confirmado) {
      return;
    }

    setErro("");

    try {
      setCredenciais(await redefinirSenhaBeneficiario(beneficiario.id));
    } catch (erroRequisicao) {
      setErro(extrairMensagemErro(erroRequisicao, "Não foi possível gerar uma nova senha."));
    }
  }

  return (
    <div>
      <CabecalhoPagina
        compacto
        titulo="Beneficiários"
        texto="Pessoas cadastradas no sistema para solicitar mudas."
        acoes={
          <button
            type="button"
            className="botao-destaque"
            onClick={() => navigate("/beneficiarios/novo")}
          >
            <UserPlus size={18} />
            Cadastrar beneficiário
          </button>
        }
      />

      <section className="container-pagina container-beneficiarios">
        <FiltrosBeneficiarios filtro={filtro} aoMudarFiltro={setFiltro} />

        {erro && <p className="beneficiarios-erro">{erro}</p>}

        {carregando && <p className="mensagem-central">Carregando beneficiários...</p>}

        {!carregando && beneficiarios.length === 0 && (
          <p className="mensagem-central">Nenhum beneficiário encontrado com esses filtros.</p>
        )}

        {!carregando && beneficiarios.length > 0 && (
          <div className="tabela-wrapper">
            <table className="tabela">
              <thead>
                <tr>
                  <th>Nome</th>
                  <th>CPF</th>
                  <th>E-mail</th>
                  <th>Contato</th>
                  <th>Perfil</th>
                  <th className="tabela-coluna-acoes">Ações</th>
                </tr>
              </thead>
              <tbody>
                {beneficiarios.map((beneficiario) => {
                  const ehAdministrador = beneficiario.role === Role.ADMINISTRADOR;

                  return (
                    <tr key={beneficiario.id}>
                      <td className="beneficiario-nome">
                        {beneficiario.nome ?? "Nome não informado"}
                      </td>
                      <td>{beneficiario.cpf ?? "—"}</td>
                      <td>{beneficiario.email ?? "—"}</td>
                      <td>
                        <span className="beneficiario-contato">{beneficiario.celular ?? "—"}</span>
                        {beneficiario.telefone && (
                          <span className="beneficiario-contato beneficiario-contato-secundario">
                            {beneficiario.telefone}
                          </span>
                        )}
                      </td>
                      <td>
                        {beneficiario.role && (
                          <span
                            className={
                              ehAdministrador ? "badge-perfil badge-perfil-admin" : "badge-perfil"
                            }
                          >
                            {rotuloRole[beneficiario.role]}
                          </span>
                        )}
                      </td>
                      <td>
                        <div className="beneficiario-acoes">
                          {!ehAdministrador && (
                            <>
                              <button
                                type="button"
                                className="botao-tabela beneficiario-botao-solicitar"
                                onClick={() =>
                                  navigate(`/beneficiarios/${beneficiario.id}/solicitacao`)
                                }
                              >
                                <ClipboardList size={14} />
                                Solicitar
                              </button>
                              <button
                                type="button"
                                className="beneficiario-botao-icone"
                                onClick={() => handleNovaSenha(beneficiario)}
                                title="Gerar nova senha provisória"
                                aria-label={`Gerar nova senha para ${beneficiario.nome}`}
                              >
                                <KeyRound size={16} />
                              </button>
                            </>
                          )}
                          <button
                            type="button"
                            className="beneficiario-botao-icone"
                            disabled
                            title="Atualização de dados disponível em breve"
                            aria-label="Atualizar dados"
                          >
                            <Pencil size={16} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {credenciais && (
        <ModalCredenciais
          titulo="Nova senha provisória"
          credenciais={credenciais}
          aoFechar={() => setCredenciais(null)}
        />
      )}
    </div>
  );
}

export default ListaBeneficiarios;
