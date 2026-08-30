package com.bsh.backend_sist_horto.gestao_mudas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "itens_distribuicao")
public class ItemDistribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_distribuicao")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_distribuicao", nullable = false)
    private Distribuicao distribuicao;

    @ManyToOne
    @JoinColumn(name = "id_muda", nullable = false)
    private Muda muda;

    @NotNull
    @PositiveOrZero
    private Integer quantidade;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDistribuicao itemDistribuicao = (ItemDistribuicao) o;
        return id != null && id.equals(itemDistribuicao.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}