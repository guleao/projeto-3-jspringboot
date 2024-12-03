package com.example.projeto3.DTO;

import lombok.Data;

@Data
public class EnderecoDTO {
    private Long id;
    private String logradouro;
    private String cidade;
    private String estado;
    private Long pessoaId;
}