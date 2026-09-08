package com.bsh.backend_sist_horto.gestao_mudas.model;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@Table(name = "mudas")
public class Muda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_muda")
    private Long id;

    private Boolean perdeMuitasFolhas;
    private Boolean possuiFlores;
    private Boolean possuiFrutos;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaMuda categoria;

    @Size(max = 50)
    @Column(length = 50)
    private String classe;

    @Size(max = 50)
    @Column(length = 50)
    private String corFlor;

    @Size(max = 50)
    @Column(length = 50)
    private String familia;

    @Size(max = 50)
    @Column(length = 50)
    private String filo;

    @Size(max = 50)
    @Column(length = 50)
    private String ordem;

    @Size(max = 50)
    @Column(length = 50)
    private String reino;

    @Size(max = 100)
    @Column(length = 100)
    private String epocaFlores;

    @Size(max = 100)
    @Column(length = 100)
    private String epocaFrutos;

    @Size(max = 100)
    @Column(length = 100)
    private String formato;

    @Size(max = 100)
    @Column(length = 100)
    private String raizes;

    @Size(max = 100)
    @Column(length = 100)
    private String tamanho;

    @ElementCollection
    @CollectionTable(
            name = "muda_nomes_populares",
            joinColumns = @JoinColumn(name = "id_muda")
    )
    @Column(name = "nome_popular")
    private List<String> nomesPopulares;

    @Size(max = 150)
    @Column(length = 150)
    private String tiposFlores;

    @Size(max = 150)
    @Column(length = 150)
    private String tiposFrutos;

    @Size(max = 255)
    private String linkImagemArvore;

    @Size(max = 255)
    private String linkImagemFlores;

    @Size(max = 255)
    private String linkImagemFrutos;

    @OneToOne(
            mappedBy = "muda",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Estoque estoque;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Muda muda = (Muda) o;
        return id != null && id.equals(muda.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}