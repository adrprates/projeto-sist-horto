package com.bsh.backend_sist_horto.gestao_mudas.model;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "etapa_solicitacao")
public class EtapaSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_solicitacao")
    @JsonIgnore
    private Solicitacao solicitacao;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataHora;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDate dataLimiteRetirada;

    @Column(length = 1000)
    private String descricao;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EtapaSolicitacao etapaSolicitacao = (EtapaSolicitacao) o;
        return id != null && id.equals(etapaSolicitacao.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}