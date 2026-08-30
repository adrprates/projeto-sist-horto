package com.bsh.backend_sist_horto.gestao_mudas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "parametros_anuais")
public class ParametroAnual {

    @Id
    @Column(name = "ano")
    private Integer ano;

    @NotNull
    @PositiveOrZero
    private Integer limiteFrutiferas;

    @NotNull
    @PositiveOrZero
    private Integer limiteOutras;

    @NotNull
    @PositiveOrZero
    private Integer limiteTotalMudas;

    @NotNull
    @PositiveOrZero
    private Integer maxPorEspecieFrutifera;

    @NotNull
    @PositiveOrZero
    private Integer maxPorEspecieOutras;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParametroAnual parametroAnual = (ParametroAnual) o;
        return ano != null && ano.equals(parametroAnual.ano);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}