package com.bsh.backend_sist_horto.gestao_mudas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "itens_proposta_alteracao")
public class ItemPropostaAlteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_proposta")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_solicitacao", nullable = false)
    @JsonIgnore
    private Solicitacao solicitacao;

    @ManyToOne
    @JoinColumn(name = "id_muda", nullable = false)
    private Muda muda;

    @NotNull
    @Positive
    private Integer quantidade;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPropostaAlteracao that = (ItemPropostaAlteracao) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
