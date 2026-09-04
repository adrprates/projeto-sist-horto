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
@Table(name = "distribuicoes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"id_beneficiario", "ano_retirada"}
                )
        })
public class Distribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_distribuicao")
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate dataRetirada;

    @ManyToOne
    @JoinColumn(name = "ano_retirada", nullable = false)
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

    @OneToMany(mappedBy = "distribuicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDistribuicao> itens = new ArrayList<>();

    public void adicionarItem(ItemDistribuicao item) {
        itens.add(item);
        item.setDistribuicao(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distribuicao distribuicao = (Distribuicao) o;
        return id != null && id.equals(distribuicao.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}