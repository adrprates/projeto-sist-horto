package com.bsh.backend_sist_horto.gestao_mudas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "saidas",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"id_beneficiario", "ano_saida"}
                )
        })
public class Saida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_saida")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataSaida;

    @ManyToOne
    @JoinColumn(name = "ano_saida", nullable = false)
    private ParametroAnual parametroAnual;

    @ManyToOne
    @JoinColumn(name = "id_beneficiario", nullable = false)
    private Beneficiario beneficiario;

    @OneToOne
    @JoinColumn(
            name = "id_solicitacao",
            nullable = false,
            unique = true
    )
    private Solicitacao solicitacao;

    @OneToMany(mappedBy = "saida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSaida> itens = new ArrayList<>();

    public void adicionarItem(ItemSaida item) {
        itens.add(item);
        item.setSaida(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Saida saida = (Saida) o;
        return id != null && id.equals(saida.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
