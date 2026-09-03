package com.bsh.backend_sist_horto.gestao_mudas.model;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    private LocalDate dataSolicitacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;

    @ManyToOne
    @JoinColumn(name = "ano_solicitacao", nullable = false)
    private ParametroAnual parametroAnual;

    @ManyToOne
    @JoinColumn(name = "id_beneficiario", nullable = false)
    private Beneficiario beneficiario;

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