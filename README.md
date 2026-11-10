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
| **Gestão de Mudas** | Cadastra, edita e remove espécies do catálogo (com características e links das imagens da árvore, das flores e dos frutos), controla a quantidade em estoque e marca uma muda como indisponível, informando o motivo. |
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
- **API documentada e testada**: todos os endpoints estão descritos no Swagger, e 125 testes automatizados comprovam as regras de negócio, a validação dos dados e a segurança.

---

## 🧰 Tecnologias

**Backend**
- Java 17 + Spring Boot 4 (Web MVC, Data JPA, Validation)
- Spring Security + JWT (autenticação sem estado e controle de acesso por perfil)
- MySQL + Hibernate
- Lombok
- springdoc-openapi (documentação Swagger)
- JUnit 6, Spring Boot Test (MockMvc), AssertJ e H2 em memória (testes automatizados)

**Frontend**
- React 19 + TypeScript + Vite
- React Router (rotas protegidas por perfil)
- Axios (com interceptador para o token JWT) e jwt-decode (leitura do perfil do usuário logado)
- Material UI (telas de autenticação) e CSS próprio com variáveis de tema
- Lucide (ícones) e jsPDF + AutoTable (geração dos relatórios em PDF)

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
│   ├── src/main/java/.../gestao_mudas/
│   │   ├── controller/     endpoints
│   │   ├── service/        regras de negócio
│   │   ├── repository/     acesso a dados
│   │   ├── specification/  filtros dinâmicos
│   │   ├── model/          entidades JPA
│   │   ├── enums/          status, perfis e categorias
│   │   ├── dto/ record/    objetos de entrada e saída
│   │   ├── security/       JWT e permissões
│   │   ├── exceptions/     tratamento padronizado de erros
│   │   └── config/         Swagger e dados iniciais
│   └── src/test/java/.../
│       ├── suporte/        dados e cenários reaproveitados pelos testes
│       └── autenticacao/, catalogo/, solicitacao/, relatorio/...   testes por funcionalidade
└── frontend-sist-horto/    Aplicação web (React + Vite)
    └── src/
        ├── pages/          telas
        ├── components/     componentes reutilizáveis
        ├── api/            chamadas ao backend
        ├── types/          tipos TypeScript
        ├── context/ hooks/ autenticação do usuário logado
        ├── utils/          máscaras, mensagens de erro e geração de PDF
        ├── config/         dados do site e tema
        └── routes/         rotas e proteção por perfil
```

---

## ▶️ Executando localmente

**Pré-requisitos:** Java 17+, Node.js 20.19+ ou 22.12+ (versões exigidas pelo Vite) e MySQL 8. Não é preciso instalar o Maven: o projeto traz o Maven Wrapper (`mvnw`).

1. **Banco de dados**: com o MySQL em execução, ajuste usuário e senha em `backend-sist-horto/src/main/resources/application.properties`. O banco `bd_sist_horto` e as tabelas são criados automaticamente.

2. **Backend** (sobe em `http://localhost:8080`):
   ```bash
   cd backend-sist-horto
   ./mvnw spring-boot:run        # Linux, macOS ou Git Bash
   .\mvnw.cmd spring-boot:run    # Windows (PowerShell ou Prompt de Comando)
   ```

3. **Frontend** (sobe em `http://localhost:5173`):
   ```bash
   cd frontend-sist-horto
   npm install
   npm run dev
   ```
   Outros comandos: `npm run build` gera a versão de produção em `dist/` e `npm run lint` verifica o código.

Na primeira execução, o sistema cria dois usuários de teste:

| Perfil | Login | Senha |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Beneficiário | `joao` | `benef123` |

> ⚠️ Esses usuários servem apenas para desenvolvimento. Troque as senhas antes de usar o sistema em produção.

### ⚙️ Configuração

| O quê | Onde | Padrão |
|---|---|---|
| Conexão com o MySQL | `spring.datasource.*` no `application.properties` | `localhost:3306/bd_sist_horto` |
| Segredo do token JWT | `app.jwt.secret` (ou variável de ambiente `APP_JWT_SECRET`) | valor fixo no código, **somente para desenvolvimento** |
| Validade do token JWT | `app.jwt.expiration`, em milissegundos (ou `APP_JWT_EXPIRATION`) | `3600000` (1 hora) |
| Origem liberada no CORS | `corsConfigurationSource()` em `SecurityConfig.java` | `http://localhost:5173` |
| Endereço da API usado pelo frontend | `baseURL` em `frontend-sist-horto/src/api/axiosConfig.ts` | `http://localhost:8080` |

### 🚀 Antes de colocar em produção

- Defina um `APP_JWT_SECRET` próprio, longo e aleatório. Se o segredo padrão for usado, qualquer pessoa que leia o código consegue gerar tokens válidos.
- Remova ou troque as senhas dos usuários de teste (`admin` e `joao`), que são criados pelo `DataLoader`.
- Use um usuário do MySQL com senha forte, sem ser o `root`.
- Ajuste a origem do CORS e o `baseURL` do frontend para o endereço real do sistema.
- Se a documentação não deve ficar pública, desative o Swagger com `springdoc.api-docs.enabled=false` e `springdoc.swagger-ui.enabled=false`.

### 📖 Documentação da API (Swagger)

Com o backend em execução, a documentação interativa fica em **http://localhost:8080/swagger-ui.html** (especificação OpenAPI em `/v3/api-docs`). Para testar rotas protegidas, faça o login em `POST /auth/login`, copie o `token` retornado e informe-o no botão **Authorize**.

### 🧪 Testes automatizados

```bash
cd backend-sist-horto
./mvnw test          # ou .\mvnw.cmd test no Windows
```

São **125 casos de teste** que comprovam a confiabilidade das regras de negócio, a validação dos dados e a segurança do backend.

**Como os testes funcionam**
- **Testes de integração com MockMvc**: a aplicação sobe por completo (filtro JWT, Spring Security, controllers, serviços e JPA) e o **MockMvc** simula as requisições HTTP do frontend, sem precisar abrir um servidor de verdade. Assim, cada teste percorre o mesmo caminho de uma chamada real.
- **Banco H2 em memória** (perfil `test`): os testes **não precisam do MySQL**. O banco é limpo antes de cada teste, então nenhum teste depende do outro.
- **Testes unitários** (sem Spring e sem banco) para as regras isoladas: a geração e validação do JWT, usando `MockEnvironment` para simular a configuração do segredo e da validade, e a regra de limites do parâmetro anual.
- **Dados reaproveitáveis** em `src/test/java/.../suporte`: `CenarioTeste` cria usuários de cada perfil já com token, mudas com estoque, parâmetros do ano e solicitações em qualquer etapa; `DadosTeste` monta os corpos de requisição válidos, que cada teste altera para gerar o caso inválido.

**O que está sendo comprovado**

| Área | O que os testes garantem |
|---|---|
| **Registro e login** | Os dados são gravados como enviados, o CPF é formatado e a senha fica criptografada com BCrypt. CPF ou login repetido é recusado (409), assim como dados inválidos (400). O login devolve um token válido e não revela quais usuários existem. |
| **Segurança e JWT** | Cada perfil (visitante, beneficiário, administrador) acessa só as rotas permitidas. Tokens adulterados, forjados com outro segredo, sem assinatura ou expirados são rejeitados, e o perfil de acesso vem do banco, não do token. Um beneficiário não vê nem altera a solicitação de outra pessoa. |
| **Perfil** | Os dados de contato são alterados sem permitir mudar CPF, login, perfil ou senha. A troca de senha exige a senha atual e invalida a anterior. |
| **Beneficiários** | O cadastro assistido gera um login sem dados pessoais e uma senha provisória que funcionam no login, e recusa CPF já cadastrado. A listagem filtra por nome, é paginada e nunca expõe senhas. A correção de CPF formata o valor e recusa CPF de outra pessoa. Redefinir a senha invalida a anterior e não vale para administradores. |
| **Catálogo e estoque** | A pesquisa por nome e os filtros combinados (`MudaSpecification`) retornam o esperado. O disponível desconta as mudas reservadas. As ações do administrador no estoque nunca deixam a quantidade negativa. |
| **Mudas** | Cadastro, edição (preservando estoque e disponibilidade), indisponibilidade com motivo e exclusão (uma muda que já faz parte de uma solicitação não é excluída). Uma muda indisponível não entra em solicitações. |
| **Parâmetros anuais** | Cadastro e manutenção dos limites. O rascunho respeita o máximo por espécie, os totais de frutíferas e das demais espécies e o total geral, com o saldo correto. |
| **Solicitações** | Todo o fluxo: rascunho, envio com reserva do estoque, aprovação, rejeição com motivo, proposta de alteração com aceite ou recusa do beneficiário, data limite de retirada, entrega com baixa do estoque e expiração liberando as mudas. Transições fora do fluxo e solicitações finalizadas são bloqueadas, cada beneficiário tem uma solicitação por ano e a entrega é bloqueada sem baixar nada quando falta estoque físico. Também cobre a listagem da Secretaria (filtros, ordenação por prioridade e paginação) e o atendimento presencial. |
| **Relatórios** | Os totais, o resumo por espécie e por categoria e a situação de cada muda (disponível, estoque baixo, esgotada, indisponível) conferem com um cenário de valores conhecidos, inclusive com os filtros. |
| **Documentação** | A especificação OpenAPI e o Swagger UI estão acessíveis e descrevem as rotas e a autenticação JWT. |

---

## 👤 Autor

**Adriano Prates Souza**
Trabalho de Conclusão de Curso — Sistema de doação de mudas do Horto Florestal de Patrocínio/MG.
