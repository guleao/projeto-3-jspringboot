package com.example.projeto3.controller;

import com.example.projeto3.DTO.AtividadeNomeDTO;
import com.example.projeto3.DTO.EnderecoDTO;
import com.example.projeto3.DTO.PessoaDTO;
import com.example.projeto3.model.Endereco;
import com.example.projeto3.model.Pessoa;
import com.example.projeto3.repository.PessoaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<PessoaDTO> getAllPessoas() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        return pessoas.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<PessoaDTO> getPessoaById(@PathVariable Long id) {
        return pessoaRepository.findById(id)
                .map(pessoa -> ResponseEntity.ok().body(convertToDto(pessoa)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PessoaDTO createPessoa(@RequestBody PessoaDTO pessoaDTO) {
        Pessoa pessoa = convertPessoaDTOToEntity(pessoaDTO);

        if (pessoa.getEnderecos() != null) {
            pessoa.getEnderecos().forEach(endereco -> endereco.setPessoa(pessoa));
        }

        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        return convertToDto(pessoaSalva);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PessoaDTO> updatePessoa(@PathVariable Long id, @RequestBody PessoaDTO pessoaDTO) {
        return pessoaRepository.findById(id)
                .map(pessoaExistente -> {
                    pessoaExistente.setNome(pessoaDTO.getNome());
                    pessoaExistente.setEmail(pessoaDTO.getEmail());

                    // Atualizar endereços
                    pessoaExistente.getEnderecos().clear();
                    if (pessoaDTO.getEnderecos() != null) {
                        List<Endereco> enderecosAtualizados = pessoaDTO.getEnderecos().stream()
                                .map(this::convertEnderecoDTOToEntity)
                                .collect(Collectors.toList());
                        enderecosAtualizados.forEach(endereco -> endereco.setPessoa(pessoaExistente));
                        pessoaExistente.getEnderecos().addAll(enderecosAtualizados);
                    }

                    Pessoa pessoaAtualizada = pessoaRepository.save(pessoaExistente);
                    return ResponseEntity.ok().body(convertToDto(pessoaAtualizada));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deletePessoa(@PathVariable Long id) {
        return pessoaRepository.findById(id)
                .map(pessoa -> {
                    pessoaRepository.delete(pessoa);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    private PessoaDTO convertToDto(Pessoa pessoa) {
        PessoaDTO pessoaDTO = new PessoaDTO();
        pessoaDTO.setId(pessoa.getId());
        pessoaDTO.setNome(pessoa.getNome());
        pessoaDTO.setEmail(pessoa.getEmail());

        if (pessoa.getEnderecos() != null) {
            List<EnderecoDTO> enderecosDTO = pessoa.getEnderecos().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            pessoaDTO.setEnderecos(enderecosDTO);
        }

        if (pessoa.getAtividades() != null) {
            List<AtividadeNomeDTO> atividadesNomeDTO = pessoa.getAtividades().stream()
                    .map(atividade -> {
                        AtividadeNomeDTO dto = new AtividadeNomeDTO();
                        dto.setAtividade(atividade.getAtividade());
                        return dto;
                    })
                    .collect(Collectors.toList());
            pessoaDTO.setAtividades(atividadesNomeDTO);
        }

        return pessoaDTO;
    }

    private Pessoa convertPessoaDTOToEntity(PessoaDTO pessoaDTO) {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(pessoaDTO.getId());
        pessoa.setNome(pessoaDTO.getNome());
        pessoa.setEmail(pessoaDTO.getEmail());

        if (pessoaDTO.getEnderecos() != null) {
            List<Endereco> enderecos = pessoaDTO.getEnderecos().stream()
                    .map(this::convertEnderecoDTOToEntity)
                    .collect(Collectors.toList());
            pessoa.setEnderecos(enderecos);
        }

        return pessoa;
    }

    private EnderecoDTO convertToDto(Endereco endereco) {
        return modelMapper.map(endereco, EnderecoDTO.class);
    }

    private Endereco convertEnderecoDTOToEntity(EnderecoDTO enderecoDTO) {
        return modelMapper.map(enderecoDTO, Endereco.class);
    }
}