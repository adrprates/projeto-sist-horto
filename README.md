# 🌱 Sistema Horto — Doação de Mudas de Patrocínio

Sistema web desenvolvido como Trabalho de Conclusão de Curso para resolver um problema real: **automatizar e facilitar o processo de doação de mudas do Horto Florestal de Patrocínio/MG**, serviço mantido pela **Secretaria Municipal de Agricultura e Pecuária de Patrocínio (MG)**.

Antes, todo o processo (pedido, análise, separação e entrega das mudas) dependia de atendimento presencial e de controles manuais. Com o sistema, o morador consegue conhecer o Horto, consultar as mudas disponíveis e fazer a sua solicitação pela internet. Já a Secretaria acompanha cada pedido, controla o estoque e gera relatórios em um só lugar.

O atendimento presencial continua existindo: quando necessário, a própria Secretaria pode gerar o pedido para o morador, seja porque ele tem dificuldade com o sistema, seja para agilizar quem foi até lá se informar e já quer aproveitar para retirar as mudas.

---

## 🌳 Sobre o Horto Florestal

O Horto Florestal de Patrocínio fica no bairro São Judas Tadeu. É lá que nascem as mudas doadas à população e as usadas na arborização da cidade. A estrutura conta com:

- **Viveiro de mudas**, com estufas de germinação, área sombreada de aclimatação e canteiros de crescimento;
- **Espécies do Cerrado**, nativas do Alto Paranaíba, como ipê, pequi e jatobá;
- **Frutíferas e arborização**, mudas para quintais e escolas e espécies próprias para ruas e praças;
- **Compensação ambiental**, que recebe mudas doadas como compensação pela supressão autorizada de árvores.

Cada muda passa pelas etapas de **coleta e germinação das sementes → repicagem para sacos de muda → crescimento e aclimatação → separação para doação**.

---

## 💻 O sistema

Qualquer pessoa pode acessar o sistema, seja por curiosidade, para conhecer o Horto ou para pedir mudas. Sem precisar de login, a pessoa encontra:

- informações sobre o **Horto Florestal** e a **Secretaria**;
- o **catálogo de mudas**, com fotos, características de cada espécie e a quantidade disponível;
- um passo a passo de **como solicitar**, com respostas para as dúvidas mais comuns.

A partir daí, ela pode **criar a própria conta e fazer a solicitação sozinha**, pelo computador ou pelo celular.

> **Tem dificuldade com tecnologia? Fique tranquilo.** O rodapé de todas as páginas traz o endereço, o horário de atendimento, o telefone e o e-mail do Horto e da Secretaria. Basta entrar em contato ou ir até lá: a equipe faz o cadastro e a solicitação pela pessoa.

### Como funciona uma doação

```
Rascunho
   │  pedido confirmado
   ▼
Pendente ────── Secretaria rejeita ────────────────▶ Rejeitada (fim)
   │  │                                                  ▲
   │  │                                                  │ beneficiário recusa
   │  └──────── Secretaria propõe alteração ───▶ Aguardando confirmação
   │                                                     │
   │  Secretaria aprova                                  │ beneficiário aceita
   ▼                                                     │
Aprovada ◀───────────────────────────────────────────────┘
   │  mudas separadas e prazo definido
   ▼
Pronta para retirada ── prazo encerrado ───────────▶ Expirada (fim)
   │  mudas retiradas no Horto
   ▼
Entregue (fim)
```

1. **Rascunho:** o pedido é montado a partir do catálogo, respeitando os limites do ano;
2. **Pendente:** ao ser confirmado, o rascunho vira um pedido válido, as mudas ficam **reservadas** no estoque e o pedido segue para análise da Secretaria;
3. **Análise:** a Secretaria pode **aprovar**, **rejeitar** (informando o motivo) ou **propor uma alteração** nos itens, já que às vezes falta alguma muda e é possível sugerir outra para compensar;
4. **Aguardando confirmação:** quando há proposta de alteração, o pedido volta para o beneficiário. Se ele aceitar, o pedido é aprovado com os novos itens; se recusar, é rejeitado. Essa proposta também pode ser feita depois da aprovação, caso falte alguma muda durante a separação;
5. **Pronta para retirada:** com o pedido aprovado e as mudas separadas, a Secretaria define uma data limite para a retirada no Horto;
6. **Entregue ou Expirada:** na retirada, a Secretaria marca o pedido como **Entregue** e o estoque é baixado automaticamente. Se o prazo terminar sem retirada, a Secretaria marca o pedido como **Expirada** e as mudas voltam a ficar disponíveis.

Um pedido **Rejeitado** ou **Expirado** é encerrado e não avança mais.

Cada mudança de etapa fica registrada em uma **linha do tempo**, com data e mensagem, que o beneficiário acompanha como se fosse o rastreio de uma compra.

---

## 🔐 Níveis de acesso

### 👀 Visitante (sem login)
Navega livremente pelas páginas informativas (Início, Horto, Secretaria, Como Solicitar) e pelo catálogo de mudas. Para fazer um pedido, basta criar uma conta.

### 🙋 Beneficiário
O morador cadastrado no sistema pode:

- montar a solicitação de mudas do ano, acompanhando o **saldo** disponível dentro dos limites;
- ajustar quantidades e remover itens antes de enviar;
- acompanhar o andamento pela **linha do tempo** da solicitação;
- **aceitar ou recusar** uma proposta de alteração feita pela Secretaria;
- consultar o histórico de solicitações dos anos anteriores;
- atualizar os dados do perfil e trocar a senha.

### 🛠️ Administrador
É a equipe da Secretaria. **Um administrador também é uma pessoa e, portanto, também é um beneficiário**: ele pode fazer a própria solicitação de mudas normalmente, além de ter acesso ao painel administrativo:

| Área | O que faz |
|---|---|
| **Gestão de Mudas** | Cadastra, edita e remove espécies do catálogo, controla a quantidade em estoque e marca uma muda como indisponível, informando o motivo. |
| **Gerenciar Solicitações** | Lista os pedidos com filtros (nome, CPF, status, ano e período) e ordenação por prioridade de atendimento ou por data. Em cada pedido é possível aprovar, rejeitar, propor alteração, marcar como pronta para retirada (com data limite), entregue ou expirada. |
| **Beneficiários** | Consulta e edita os cadastros e faz o **cadastro assistido** de quem não consegue se registrar sozinho. O sistema gera um login (sem dados pessoais) e uma senha provisória, e o administrador pode fazer a solicitação em nome da pessoa e redefinir a senha quando ela esquecer. |
| **Parâmetros Anuais** | Define as regras de cada ano: limite total de mudas por pessoa, limite de frutíferas, limite das demais espécies e máximo por espécie. |
| **Relatórios** | **Saída de mudas** (o que foi entregue, por período, categoria e espécie) e **Posição de estoque** (estoque físico, reservado e disponível, com alertas de estoque baixo ou esgotado). Os dois podem ser exportados em **PDF**. |

---

## ✨ Destaques

- **Reserva de estoque**, no estilo de um e-commerce: as mudas de um pedido em andamento ficam reservadas, e o catálogo mostra apenas o que realmente está disponível (estoque físico − reservado). Pedidos simultâneos das últimas unidades são tratados com bloqueio no banco de dados.
- **Regras anuais configuráveis**: os limites de mudas mudam conforme o planejamento de cada ano, sem alterar o código.
- **Proposta de alteração**: quando não é possível atender o pedido como foi feito, a Secretaria sugere outros itens e o beneficiário decide.
- **Atenção à LGPD**: o login gerado pelo sistema não usa o CPF, os relatórios não expõem documentos pessoais e as senhas são armazenadas criptografadas.
- **Máscaras e validações** de CPF, celular e telefone, compatíveis com cadastros antigos.
- **Interface responsiva**, que funciona no computador e no celular.

---

## 🧰 Tecnologias

**Backend**
- Java 17 + Spring Boot 4 (Web MVC, Data JPA, Validation)
- Spring Security + JWT (autenticação sem estado e controle de acesso por perfil)
- MySQL + Hibernate
- Lombok

**Frontend**
- React 19 + TypeScript + Vite
- React Router (rotas protegidas por perfil)
- Axios (com interceptador para o token JWT)
- Material UI (telas de autenticação) e CSS próprio com variáveis de tema
- Lucide (ícones) e jsPDF (geração dos relatórios em PDF)

### Estratégias e padrões adotados

- **Arquitetura em camadas**: `controller → service → repository → model`, com DTOs e records separando o que entra e o que sai da API das entidades do banco.
- **Specification + Filter**: cada listagem com filtros (mudas, beneficiários, solicitações e saídas) recebe um objeto `*Filter` e monta a consulta dinamicamente com uma `*Specification` do Spring Data JPA, combinando só os critérios informados.
- **Paginação no servidor**, com uma resposta padronizada (`PaginaResponse`) para as listagens maiores.
- **Histórico por etapas**: cada mudança de status gera um registro (`EtapaSolicitacao`), e as transições permitidas são validadas no backend.
- **Componentização no frontend**: componentes reutilizáveis (cabeçalho de página, filtros, paginação, badges de status) e um tipo TypeScript para cada contrato da API.

---

## 📁 Estrutura do repositório

```
├── backend-sist-horto/     API REST (Spring Boot)
│   └── src/main/java/.../gestao_mudas/
│       ├── controller/     endpoints
│       ├── service/        regras de negócio
│       ├── repository/     acesso a dados
│       ├── specification/  filtros dinâmicos
│       ├── model/          entidades JPA
│       ├── dto/ record/    objetos de entrada e saída
│       └── security/       JWT e permissões
└── frontend-sist-horto/    Aplicação web (React + Vite)
    └── src/
        ├── pages/          telas
        ├── components/     componentes reutilizáveis
        ├── api/            chamadas ao backend
        ├── types/          tipos TypeScript
        └── routes/         rotas e proteção por perfil
```

---

## ▶️ Executando localmente

**Pré-requisitos:** Java 17+, Node.js 20+ e MySQL 8.

1. **Banco de dados**: com o MySQL em execução, ajuste usuário e senha em `backend-sist-horto/src/main/resources/application.properties`. O banco `bd_sist_horto` e as tabelas são criados automaticamente.

2. **Backend** (sobe em `http://localhost:8080`):
   ```bash
   cd backend-sist-horto
   ./mvnw spring-boot:run
   ```

3. **Frontend** (sobe em `http://localhost:5173`):
   ```bash
   cd frontend-sist-horto
   npm install
   npm run dev
   ```

Na primeira execução, o sistema cria dois usuários de teste:

| Perfil | Login | Senha |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Beneficiário | `joao` | `benef123` |

> ⚠️ Esses usuários servem apenas para desenvolvimento. Troque as senhas antes de usar o sistema em produção.

---

## 👤 Autor

**Adriano Prates Souza**
Trabalho de Conclusão de Curso — Sistema de doação de mudas do Horto Florestal de Patrocínio/MG.
