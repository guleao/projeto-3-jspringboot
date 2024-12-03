package com.example.projeto3.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Atividade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String atividade;
    private LocalDate dataExpiracao;

    @Enumerated(EnumType.STRING)
    private Situacao situacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id")
    @JsonBackReference
    private Pessoa pessoa;

    @Override
    public String toString() {
        return "Atividade{" +
                "id=" + id +
                ", atividade='" + atividade + '\'' +
                ", dataExpiracao=" + dataExpiracao +
                ", situacao=" + situacao +
                '}';
    }
}