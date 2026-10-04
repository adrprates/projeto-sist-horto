import { useEffect, useState } from "react";

export function useValorAtrasado<T>(valor: T, atraso = 250): T {
  const [valorAtrasado, setValorAtrasado] = useState(valor);

  useEffect(() => {
    const temporizador = setTimeout(() => setValorAtrasado(valor), atraso);
    return () => clearTimeout(temporizador);
  }, [valor, atraso]);

  return valorAtrasado;
}
