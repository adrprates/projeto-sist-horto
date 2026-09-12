import { useEffect, useState } from "react";
import type { DadosBeneficiario } from "../../types/DadosBeneficiario";
import type { BeneficiarioFilter } from "../../types/BeneficiarioFilter";
import { listarBeneficiarios } from "../../api/beneficiarioService";
import Cabecalho from "../../components/Cabecalho/Cabecalho";
import Rodape from "../../components/Rodape/Rodape";
import FiltrosBeneficiarios from "../../components/FiltrosBeneficiarios/FiltrosBeneficiarios";
import CardBeneficiario from "../../components/CardBeneficiario/CardBeneficiario";
import "./ListaBeneficiarios.css";

interface ListaBeneficiariosProps {
  aoEditarBeneficiario?: (beneficiario: DadosBeneficiario) => void;
}

function ListaBeneficiarios({ aoEditarBeneficiario }: ListaBeneficiariosProps) {
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
      <Cabecalho />

      <main className="container-beneficiarios">
        <h2 className="titulo-beneficiarios">Beneficiários</h2>

        <FiltrosBeneficiarios filtro={filtro} aoMudarFiltro={setFiltro} />

        {carregando && <p className="mensagem-central">Carregando beneficiários...</p>}

        {!carregando && beneficiarios.length === 0 && (
          <p className="mensagem-central">Nenhum beneficiário encontrado com esses filtros.</p>
        )}

        <div className="grid-beneficiarios">
          {beneficiarios.map((beneficiario) => (
            <CardBeneficiario
              key={beneficiario.id}
              beneficiario={beneficiario}
              aoEditar={aoEditarBeneficiario}
            />
          ))}
        </div>
      </main>

      <Rodape />
    </div>
  );
}

export default ListaBeneficiarios;