const somenteDigitos = (valor: string) => valor.replace(/\D/g, "");

export function mascararCpf(valor: string): string {
  const digitos = somenteDigitos(valor).slice(0, 11);

  return digitos
    .replace(/^(\d{3})(\d)/, "$1.$2")
    .replace(/^(\d{3})\.(\d{3})(\d)/, "$1.$2.$3")
    .replace(/\.(\d{3})(\d{1,2})$/, ".$1-$2");
}

export function mascararTelefone(valor: string): string {
  const digitos = somenteDigitos(valor).slice(0, 11);

  if (digitos.length <= 2) {
    return digitos.length ? `(${digitos}` : "";
  }

  const ddd = digitos.slice(0, 2);
  const numero = digitos.slice(2);

  if (numero.length <= 4) {
    return `(${ddd}) ${numero}`;
  }

  const tamanhoInicio = numero.length === 9 ? 5 : 4;
  return `(${ddd}) ${numero.slice(0, tamanhoInicio)}-${numero.slice(tamanhoInicio)}`;
}

export function telefoneValido(valor: string): boolean {
  const tamanho = somenteDigitos(valor).length;
  return tamanho === 10 || tamanho === 11;
}

export function formatarCpfExibicao(valor?: string | null): string {
  if (!valor) return "—";
  return somenteDigitos(valor).length === 11 ? mascararCpf(valor) : valor;
}

export function formatarTelefoneExibicao(valor?: string | null): string {
  if (!valor) return "—";
  const tamanho = somenteDigitos(valor).length;
  return tamanho === 10 || tamanho === 11 ? mascararTelefone(valor) : valor;
}

export function cpfParaEdicao(valor?: string | null): string {
  if (!valor) return "";
  return somenteDigitos(valor).length === 11 ? mascararCpf(valor) : valor;
}

export function telefoneParaEdicao(valor?: string | null): string {
  if (!valor) return "";
  const tamanho = somenteDigitos(valor).length;
  return tamanho === 10 || tamanho === 11 ? mascararTelefone(valor) : valor;
}
