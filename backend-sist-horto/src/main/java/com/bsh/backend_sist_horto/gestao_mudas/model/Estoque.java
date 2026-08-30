package com.bsh.backend_sist_horto.gestao_mudas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "estoques")
public class Estoque {

    @Id
    @Column(name = "id_muda")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_muda",  nullable = false)
    private Muda muda;

    @NotNull
    @PositiveOrZero
    private Integer quantidade;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estoque estoque = (Estoque) o;
        return id != null && id.equals(estoque.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}