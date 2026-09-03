package com.bsh.backend_sist_horto.gestao_mudas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "itens_solicitacao")
public class ItemSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_solicitacao")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_solicitacao", nullable = false)
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
        ItemSolicitacao that = (ItemSolicitacao) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}