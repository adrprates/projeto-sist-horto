package com.bsh.backend_sist_horto.gestao_mudas.model;

import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter@Setter
@Table(name = "beneficiarios")
public class Beneficiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beneficiario")
    private Long id;

    @NotBlank
    @Size(max = 14)
    @Column(nullable = false, length = 14, unique = true)
    private String cpf;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String celular;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String endereco;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String login;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String senha;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beneficiario beneficiario = (Beneficiario) o;
        return id != null && id.equals(beneficiario.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}