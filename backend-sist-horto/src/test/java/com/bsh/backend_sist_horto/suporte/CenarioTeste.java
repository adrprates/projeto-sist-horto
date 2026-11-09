package com.bsh.backend_sist_horto.suporte;

import com.bsh.backend_sist_horto.gestao_mudas.dto.AtualizarEtapaRequest;
import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.ParametroAnualRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.security.CustomBeneficiarioDetails;
import com.bsh.backend_sist_horto.gestao_mudas.security.JwtUtil;
import com.bsh.backend_sist_horto.gestao_mudas.service.EstoqueService;
import com.bsh.backend_sist_horto.gestao_mudas.service.EtapaService;
import com.bsh.backend_sist_horto.gestao_mudas.service.MudaService;
import com.bsh.backend_sist_horto.gestao_mudas.service.SolicitacaoService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

import static com.bsh.backend_sist_horto.suporte.DadosTeste.SENHA_PADRAO;

/**
 * Monta e persiste cenários reaproveitáveis entre as classes de teste:
 * usuários com cada permissão (já com token), mudas com estoque, parâmetros
 * anuais e solicitações em qualquer etapa do fluxo.
 *
 * Os fluxos são executados pelos próprios serviços da aplicação, então as regras
 * de negócio (limites, estoque, transições) também valem na preparação dos dados.
 */
@Component
public class CenarioTeste {

    private final BeneficiarioRepository beneficiarioRepository;
    private final ParametroAnualRepository parametroAnualRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final MudaService mudaService;
    private final EstoqueService estoqueService;
    private final SolicitacaoService solicitacaoService;
    private final EtapaService etapaService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TransactionTemplate transacao;

    public CenarioTeste(BeneficiarioRepository beneficiarioRepository,
                        ParametroAnualRepository parametroAnualRepository,
                        SolicitacaoRepository solicitacaoRepository,
                        MudaService mudaService,
                        EstoqueService estoqueService,
                        SolicitacaoService solicitacaoService,
                        EtapaService etapaService,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil,
                        TransactionTemplate transacao) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.parametroAnualRepository = parametroAnualRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.mudaService = mudaService;
        this.estoqueService = estoqueService;
        this.solicitacaoService = solicitacaoService;
        this.etapaService = etapaService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.transacao = transacao;
    }

    // ==================================================================
    // Usuários
    // ==================================================================

    public UsuarioTeste criarAdministrador() {
        int n = DadosTeste.proximoNumero();
        return criarUsuario("admin" + n, "Servidor da Secretaria " + n, Role.ADMINISTRADOR);
    }

    public UsuarioTeste criarBeneficiario() {
        int n = DadosTeste.proximoNumero();
        return criarUsuario("morador" + n, "Morador " + n, Role.BENEFICIARIO);
    }

    public UsuarioTeste criarBeneficiario(String login, String nome) {
        return criarUsuario(login, nome, Role.BENEFICIARIO);
    }

    public UsuarioTeste criarUsuario(String login, String nome, Role role) {
        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setCpf(DadosTeste.cpfUnico());
        beneficiario.setCelular("(34)99999-1234");
        beneficiario.setTelefone("(34)3831-1234");
        beneficiario.setEmail(login + "@email.com");
        beneficiario.setNome(nome);
        beneficiario.setEndereco("Rua do Horto, 100");
        beneficiario.setLogin(login);
        beneficiario.setSenha(passwordEncoder.encode(SENHA_PADRAO));
        beneficiario.setRole(role);

        Beneficiario salvo = beneficiarioRepository.save(beneficiario);
        return new UsuarioTeste(salvo, SENHA_PADRAO, gerarToken(salvo));
    }

    public String gerarToken(Beneficiario beneficiario) {
        return jwtUtil.generateToken(new CustomBeneficiarioDetails(beneficiario));
    }

    // ==================================================================
    // Mudas, estoque e parâmetros
    // ==================================================================

    public Muda criarMuda(String nome, CategoriaMuda categoria, int estoque) {
        return salvarMuda(DadosTeste.muda(nome, categoria), estoque);
    }

    public Muda criarFrutifera(String nome, int estoque) {
        return criarMuda(nome, CategoriaMuda.FRUTIFERAS, estoque);
    }

    public Muda criarNaoFrutifera(String nome, int estoque) {
        return criarMuda(nome, CategoriaMuda.ARBORIZACAO, estoque);
    }

    public Muda salvarMuda(Muda muda, int estoque) {
        Muda salva = mudaService.salvar(muda);
        estoqueService.atualizarEstoque(salva.getId(), estoque);
        return salva;
    }

    public Muda criarMudaIndisponivel(String nome, CategoriaMuda categoria, int estoque, String motivo) {
        Muda muda = criarMuda(nome, categoria, estoque);
        return mudaService.alterarDisponibilidade(muda.getId(), false, motivo);
    }

    public void tornarIndisponivel(Muda muda, String motivo) {
        mudaService.alterarDisponibilidade(muda.getId(), false, motivo);
    }

    public void definirEstoque(Muda muda, int quantidade) {
        estoqueService.atualizarEstoque(muda.getId(), quantidade);
    }

    public ParametroAnual criarParametroAnoAtual() {
        return parametroAnualRepository.save(DadosTeste.parametroPadrao(DadosTeste.anoAtual()));
    }

    public ParametroAnual criarParametroAnoAtual(int limiteFrutiferas, int limiteOutras, int limiteTotal,
                                                 int maxPorEspecieFrutifera, int maxPorEspecieOutras) {
        return parametroAnualRepository.save(DadosTeste.parametro(DadosTeste.anoAtual(),
                limiteFrutiferas, limiteOutras, limiteTotal, maxPorEspecieFrutifera, maxPorEspecieOutras));
    }

    public ParametroAnual criarParametro(int ano) {
        return parametroAnualRepository.save(DadosTeste.parametroPadrao(ano));
    }

    // ==================================================================
    // Estoque (consultas para as asserções)
    // ==================================================================

    public int estoqueFisico(Muda muda) {
        return estoqueService.getEstoquePorId(muda.getId()).getQuantidade();
    }

    public int estoqueReservado(Muda muda) {
        return estoqueService.quantidadeReservada(muda.getId());
    }

    public int estoqueDisponivel(Muda muda) {
        return estoqueService.quantidadeDisponivel(muda.getId());
    }

    // ==================================================================
    // Solicitações
    // ==================================================================

    /** Item de uma solicitação montada no cenário. */
    public record Item(Muda muda, int quantidade) {}

    public static Item item(Muda muda, int quantidade) {
        return new Item(muda, quantidade);
    }

    /** Adiciona os itens ao rascunho do usuário e devolve o id do rascunho. */
    public Long montarRascunho(UsuarioTeste usuario, Item... itens) {
        return transacao.execute(status -> {
            Beneficiario beneficiario = beneficiarioRepository.findById(usuario.id()).orElseThrow();
            Solicitacao rascunho = solicitacaoService.buscarOuCriarRascunho(beneficiario);
            for (Item item : itens) {
                rascunho = solicitacaoService.montarSolicitacao(item.muda().getId(), item.quantidade(), beneficiario);
            }
            return rascunho.getId();
        });
    }

    /** Monta o rascunho e envia: a solicitação fica PENDENTE e reserva o estoque. */
    public Long criarSolicitacaoPendente(UsuarioTeste usuario, Item... itens) {
        Long id = montarRascunho(usuario, itens);
        transacao.executeWithoutResult(status -> solicitacaoService.enviarSolicitacao(id));
        return id;
    }

    /**
     * Cria uma solicitação e a conduz até o status desejado pelo caminho
     * "feliz" do fluxo. Para AGUARDANDO_CONFIRMACAO use {@link #proporAlteracao}.
     */
    public Long criarSolicitacaoNoStatus(UsuarioTeste usuario, StatusSolicitacao alvo, Item... itens) {
        if (alvo == StatusSolicitacao.RASCUNHO) {
            return montarRascunho(usuario, itens);
        }

        Long id = criarSolicitacaoPendente(usuario, itens);

        switch (alvo) {
            case PENDENTE -> { }
            case REJEITADA -> avancar(id, StatusSolicitacao.REJEITADA);
            case APROVADA -> avancar(id, StatusSolicitacao.APROVADA);
            case PRONTA_PARA_RETIRADA -> {
                avancar(id, StatusSolicitacao.APROVADA);
                avancar(id, StatusSolicitacao.PRONTA_PARA_RETIRADA);
            }
            case ENTREGUE, EXPIRADA -> {
                avancar(id, StatusSolicitacao.APROVADA);
                avancar(id, StatusSolicitacao.PRONTA_PARA_RETIRADA);
                avancar(id, alvo);
            }
            default -> throw new IllegalArgumentException(
                    "Use proporAlteracao(...) para chegar em " + alvo);
        }
        return id;
    }

    /** Registra a etapa com dados padrão válidos (motivo da rejeição, data limite futura). */
    public void avancar(Long idSolicitacao, StatusSolicitacao status) {
        AtualizarEtapaRequest request = new AtualizarEtapaRequest();
        request.setStatus(status);
        if (status == StatusSolicitacao.REJEITADA) {
            request.setMotivo("Motivo informado no teste");
        }
        if (status == StatusSolicitacao.PRONTA_PARA_RETIRADA) {
            request.setDataLimiteRetirada(LocalDate.now().plusDays(7));
        }
        transacao.executeWithoutResult(s -> etapaService.atualizarEtapa(idSolicitacao, request));
    }

    public void proporAlteracao(Long idSolicitacao, Item... itensPropostos) {
        AtualizarEtapaRequest request = new AtualizarEtapaRequest();
        request.setStatus(StatusSolicitacao.AGUARDANDO_CONFIRMACAO);
        for (Item item : itensPropostos) {
            var proposto = new com.bsh.backend_sist_horto.gestao_mudas.dto.ItemPropostoRequest();
            proposto.setMudaId(item.muda().getId());
            proposto.setQuantidade(item.quantidade());
            request.getItensPropostos().add(proposto);
        }
        transacao.executeWithoutResult(s -> etapaService.atualizarEtapa(idSolicitacao, request));
    }

    public StatusSolicitacao statusDe(Long idSolicitacao) {
        return solicitacaoRepository.findById(idSolicitacao).orElseThrow().getStatusAtual();
    }
}
