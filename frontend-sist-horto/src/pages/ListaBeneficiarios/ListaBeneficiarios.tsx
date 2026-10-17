import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ClipboardList, KeyRound, Pencil, UserPlus } from "lucide-react";
import type { DadosBeneficiario } from "../../types/DadosBeneficiario";
import type { BeneficiarioFilter } from "../../types/BeneficiarioFilter";
import type { Credenciais } from "../../types/Credenciais";
import { Role, rotuloRole } from "../../types/Role";
import { listarBeneficiarios, redefinirSenhaBeneficiario } from "../../api/beneficiarioService";
import { extrairMensagemErro } from "../../utils/extrairMensagemErro";
import { useValorAtrasado } from "../../hooks/useValorAtrasado";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import FiltrosBeneficiarios from "../../components/FiltrosBeneficiarios/FiltrosBeneficiarios";
import ModalCredenciais from "../../components/ModalCredenciais/ModalCredenciais";
import BarraPaginacao from "../../components/Paginacao/BarraPaginacao";
import Paginacao from "../../components/Paginacao/Paginacao";
import { formatarCpfExibicao, formatarTelefoneExibicao } from "../../utils/mascaras";
import "./ListaBeneficiarios.css";

function ListaBeneficiarios() {
  const [beneficiarios, setBeneficiarios] = useState<DadosBeneficiario[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState<BeneficiarioFilter>({});
  const filtroAtrasado = useValorAtrasado(filtro);
  const [versaoLista, setVersaoLista] = useState(0);
  const [pagina, setPagina] = useState(0);
  const [tamanho, setTamanho] = useState(10);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);
  const [credenciais, setCredenciais] = useState<Credenciais | null>(null);
  const [erro, setErro] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const mensagemSucesso = (location.state as { mensagem?: string } | null)?.mensagem;

  useEffect(() => {
    setCarregando(true);
    listarBeneficiarios(filtroAtrasado, pagina, tamanho)
      .then((resultado) => {
        setBeneficiarios(resultado.conteudo);
        setTotalPaginas(resultado.totalPaginas);
        setTotalElementos(resultado.totalElementos);
        setVersaoLista((versao) => versao + 1);
      })
      .finally(() => setCarregando(false));
  }, [filtroAtrasado, pagina, tamanho]);

  function handleMudarFiltro(novoFiltro: BeneficiarioFilter) {
    setFiltro(novoFiltro);
    setPagina(0);
  }

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
        <FiltrosBeneficiarios filtro={filtro} aoMudarFiltro={handleMudarFiltro} />

        <BarraPaginacao
          pagina={pagina}
          tamanho={tamanho}
          totalElementos={totalElementos}
          rotuloItens="beneficiários"
          aoMudarTamanho={(novoTamanho) => {
            setTamanho(novoTamanho);
            setPagina(0);
          }}
        />

        {mensagemSucesso && <p className="beneficiarios-sucesso surgir">{mensagemSucesso}</p>}

        {erro && <p className="beneficiarios-erro">{erro}</p>}

        {carregando && versaoLista === 0 && (
          <p className="mensagem-central">Carregando beneficiários...</p>
        )}

        {!carregando && beneficiarios.length === 0 && (
          <p className="mensagem-central surgir">Nenhum beneficiário encontrado com esses filtros.</p>
        )}

        {beneficiarios.length > 0 && (
          <div className={`tabela-wrapper conteudo-atualizavel${carregando ? " atualizando" : ""}`}>
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
              <tbody key={versaoLista} className="lista-animada">
                {beneficiarios.map((beneficiario) => {
                  const ehAdministrador = beneficiario.role === Role.ADMINISTRADOR;

                  return (
                    <tr key={beneficiario.id}>
                      <td className="beneficiario-nome">
                        {beneficiario.nome ?? "Nome não informado"}
                      </td>
                      <td>{formatarCpfExibicao(beneficiario.cpf)}</td>
                      <td>{beneficiario.email ?? "—"}</td>
                      <td>
                        <span className="beneficiario-contato">
                          {formatarTelefoneExibicao(beneficiario.celular)}
                        </span>
                        {beneficiario.telefone && (
                          <span className="beneficiario-contato beneficiario-contato-secundario">
                            {formatarTelefoneExibicao(beneficiario.telefone)}
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
                            onClick={() => navigate(`/beneficiarios/${beneficiario.id}/editar`)}
                            title="Atualizar dados cadastrais"
                            aria-label={`Atualizar dados de ${beneficiario.nome}`}
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

        <Paginacao pagina={pagina} totalPaginas={totalPaginas} aoMudarPagina={setPagina} />
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
