package com.example.projeto3.DTO;

import lombok.Data;
import java.time.LocalDate;
import com.example.projeto3.model.Situacao;

@Data
public class AtividadeDTO {
    private Long id;
    private String atividade;
    private LocalDate dataExpiracao;
    private Situacao situacao;
    private PessoaNomeDTO pessoa;
}