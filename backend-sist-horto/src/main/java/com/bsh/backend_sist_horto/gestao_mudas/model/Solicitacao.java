package com.bsh.backend_sist_horto.gestao_mudas.model;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "solicitacoes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"id_beneficiario", "ano_solicitacao"}
                )
        })
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao")
    private Long id;

    @NotNull
    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataSolicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao statusAtual;

    @ManyToOne
    @JoinColumn(name = "ano_solicitacao", nullable = false)
    private ParametroAnual parametroAnual;

    @ManyToOne
    @JoinColumn(name = "id_beneficiario", nullable = false)
    private Beneficiario beneficiario;

    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSolicitacao> itens = new ArrayList<>();

    @OneToMany(
            mappedBy = "solicitacao",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("dataHora ASC, id ASC")
    @JsonProperty("etapas")
    private List<EtapaSolicitacao> historicos = new ArrayList<>();

    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPropostaAlteracao> itensPropostos = new ArrayList<>();

    public void adicionarItem(ItemSolicitacao item) {
        itens.add(item);
        item.setSolicitacao(this);
    }

    public void adicionarEtapa(EtapaSolicitacao etapa) {
        historicos.add(etapa);
        etapa.setSolicitacao(this);
    }

    public void adicionarItemProposto(ItemPropostaAlteracao item) {
        itensPropostos.add(item);
        item.setSolicitacao(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solicitacao that = (Solicitacao) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}