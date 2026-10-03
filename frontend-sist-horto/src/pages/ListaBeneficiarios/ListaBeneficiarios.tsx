import { useEffect, useState } from "react";
import { Pencil, UserPlus } from "lucide-react";
import type { DadosBeneficiario } from "../../types/DadosBeneficiario";
import type { BeneficiarioFilter } from "../../types/BeneficiarioFilter";
import { Role, rotuloRole } from "../../types/Role";
import { listarBeneficiarios } from "../../api/beneficiarioService";
import CabecalhoPagina from "../../components/CabecalhoPagina/CabecalhoPagina";
import FiltrosBeneficiarios from "../../components/FiltrosBeneficiarios/FiltrosBeneficiarios";
import "./ListaBeneficiarios.css";

const AVISO_EM_BREVE = "Funcionalidade disponível em breve";

function ListaBeneficiarios() {
  const [beneficiarios, setBeneficiarios] = useState<DadosBeneficiario[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState<BeneficiarioFilter>({});

  useEffect(() => {
    setCarregando(true);
    listarBeneficiarios(filtro)
      .then(setBeneficiarios)
      .finally(() => setCarregando(false));
  }, [filtro]);

  return (
    <div>
      <CabecalhoPagina
        compacto
        titulo="Beneficiários"
        texto="Pessoas cadastradas no sistema para solicitar mudas."
        acoes={
          <button type="button" className="botao-destaque" disabled title={AVISO_EM_BREVE}>
            <UserPlus size={18} />
            Cadastrar beneficiário
          </button>
        }
      />

      <section className="container-pagina container-beneficiarios">
        <FiltrosBeneficiarios filtro={filtro} aoMudarFiltro={setFiltro} />

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
                {beneficiarios.map((beneficiario) => (
                  <tr key={beneficiario.id}>
                    <td className="beneficiario-nome">{beneficiario.nome ?? "Nome não informado"}</td>
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
                            beneficiario.role === Role.ADMINISTRADOR
                              ? "badge-perfil badge-perfil-admin"
                              : "badge-perfil"
                          }
                        >
                          {rotuloRole[beneficiario.role]}
                        </span>
                      )}
                    </td>
                    <td>
                      <button
                        type="button"
                        className="botao-tabela"
                        disabled
                        title={AVISO_EM_BREVE}
                      >
                        <Pencil size={14} />
                        Atualizar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}

export default ListaBeneficiarios;
