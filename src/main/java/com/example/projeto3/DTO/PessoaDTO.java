package com.example.projeto3.DTO;

import lombok.Data;
import java.util.List;

@Data
public class PessoaDTO {
    private Long id;
    private String nome;
    private String email;
    private List<EnderecoDTO> enderecos;
    private List<AtividadeNomeDTO> atividades;
}