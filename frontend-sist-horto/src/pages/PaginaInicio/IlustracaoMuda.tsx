const FOLHAS = [
  { x: 150, y: 300, rotacao: -160, atraso: 0.9 },
  { x: 165, y: 250, rotacao: -20, atraso: 1.1 },
  { x: 152, y: 195, rotacao: -155, atraso: 1.3 },
  { x: 168, y: 150, rotacao: -25, atraso: 1.5 },
  { x: 160, y: 100, rotacao: -150, atraso: 1.7 },
  { x: 160, y: 62, rotacao: -60, atraso: 1.9 },
];

function IlustracaoMuda() {
  return (
    <svg
      viewBox="0 0 320 420"
      className="ilustracao-muda"
      role="img"
      aria-label="Ilustração de uma muda crescendo"
    >
      <path d="M100 350h120l-14 60h-92z" fill="#C89B62" />
      <rect x="94" y="340" width="132" height="16" rx="6" fill="#9A6B3A" />
      <path
        className="ilustracao-muda-caule"
        d="M160 342C160 300 145 270 160 225S172 120 160 55"
        fill="none"
        stroke="#B5D33D"
        strokeWidth="6"
        strokeLinecap="round"
      />
      {FOLHAS.map((folha, indice) => (
        <g key={indice} transform={`translate(${folha.x} ${folha.y}) rotate(${folha.rotacao})`}>
          <path
            className="ilustracao-muda-folha"
            style={{ animationDelay: `${folha.atraso}s` }}
            d="M0 0C25-28 65-22 78 0C60 24 22 24 0 0Z"
            fill={indice % 2 ? "#3F9564" : "#8DB51F"}
          />
        </g>
      ))}
    </svg>
  );
}

export default IlustracaoMuda;
