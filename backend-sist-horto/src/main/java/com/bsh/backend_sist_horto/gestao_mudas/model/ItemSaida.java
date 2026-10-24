package com.bsh.backend_sist_horto.gestao_mudas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "itens_saida")
public class ItemSaida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_saida")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_saida", nullable = false)
    private Saida saida;

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
        ItemSaida itemSaida = (ItemSaida) o;
        return id != null && id.equals(itemSaida.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
