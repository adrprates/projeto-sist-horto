import { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import { X } from "lucide-react";
import type { DadosMudaDetalhes } from "../../types/DadosMudaDetalhes";
import { buscarDetalhesMuda } from "../../api/mudaService";
import { rotuloCategoria } from "../../types/CategoriaMuda";
import "./ModalDetalhesMuda.css";

interface ModalDetalhesMudaProps {
  idMuda: string;
  aoFechar: () => void;
}

function ModalDetalhesMuda({ idMuda, aoFechar }: ModalDetalhesMudaProps) {
  const [detalhes, setDetalhes] = useState<DadosMudaDetalhes | null>(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    buscarDetalhesMuda(idMuda)
      .then(setDetalhes)
      .finally(() => setCarregando(false));
  }, [idMuda]);

  const conteudoModal = (
    <div className="modal-fundo" onClick={aoFechar}>
      <div className="modal-conteudo" onClick={(evento) => evento.stopPropagation()}>
        <button type="button" className="modal-botao-fechar" onClick={aoFechar}>
          <X size={22} />
        </button>

        {carregando && <p className="modal-carregando">Carregando detalhes...</p>}

        {!carregando && !detalhes && (
          <p className="modal-carregando">Não foi possível carregar os detalhes.</p>
        )}

        {!carregando && detalhes && (
          <>
            <img
              src={detalhes.linkImagemArvore}
              alt={detalhes.nomesPopulares.join(", ")}
              className="modal-imagem-principal"
            />

            <div className="modal-corpo">
              <span className="tag">{rotuloCategoria[detalhes.categoria]}</span>
              <h2 className="modal-titulo">{detalhes.nomesPopulares.join(", ")}</h2>
              <p className="modal-nome-cientifico">{detalhes.nomeCientifico}</p>

              <div className="modal-secao">
                <h4>Taxonomia</h4>
                <div className="modal-grade">
                  <p><strong>Reino:</strong> {detalhes.reino}</p>
                  <p><strong>Filo:</strong> {detalhes.filo}</p>
                  <p><strong>Classe:</strong> {detalhes.classe}</p>
                  <p><strong>Ordem:</strong> {detalhes.ordem}</p>
                  <p><strong>Família:</strong> {detalhes.familia}</p>
                </div>
              </div>

              <div className="modal-secao">
                <h4>Características</h4>
                <div className="modal-grade">
                  {detalhes.formato && <p><strong>Formato:</strong> {detalhes.formato}</p>}
                  {detalhes.tamanho && <p><strong>Tamanho:</strong> {detalhes.tamanho}</p>}
                  {detalhes.raizes && <p><strong>Raízes:</strong> {detalhes.raizes}</p>}
                  {detalhes.corFlor && <p><strong>Cor da flor:</strong> {detalhes.corFlor}</p>}
                  {detalhes.tiposFlores && <p><strong>Tipo de flor:</strong> {detalhes.tiposFlores}</p>}
                  {detalhes.epocaFlores && <p><strong>Época das flores:</strong> {detalhes.epocaFlores}</p>}
                  {detalhes.tiposFrutos && <p><strong>Tipo de fruto:</strong> {detalhes.tiposFrutos}</p>}
                  {detalhes.epocaFrutos && <p><strong>Época dos frutos:</strong> {detalhes.epocaFrutos}</p>}
                </div>
              </div>

              {(detalhes.linkImagemFlores || detalhes.linkImagemFrutos || detalhes.imagensAdicionais) && (
                <div className="modal-secao">
                  <h4>Galeria</h4>
                  <div className="modal-galeria">
                    {detalhes.linkImagemFlores && <img src={detalhes.linkImagemFlores} alt="Flores" />}
                    {detalhes.linkImagemFrutos && <img src={detalhes.linkImagemFrutos} alt="Frutos" />}
                    {detalhes.imagensAdicionais?.map((url) => (
                      <img key={url} src={url} alt="Imagem adicional" />
                    ))}
                  </div>
                </div>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );

  return createPortal(conteudoModal, document.body);
}

export default ModalDetalhesMuda;