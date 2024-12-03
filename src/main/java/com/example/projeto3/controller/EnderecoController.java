package com.example.projeto3.controller;

import com.example.projeto3.DTO.EnderecoDTO;
import com.example.projeto3.model.Endereco;
import com.example.projeto3.model.Pessoa;
import com.example.projeto3.repository.EnderecoRepository;
import com.example.projeto3.repository.PessoaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ModelMapper modelMapper;

    private EnderecoDTO convertToDto(Endereco endereco) {
        EnderecoDTO enderecoDTO = modelMapper.map(endereco, EnderecoDTO.class);
        if (endereco.getPessoa() != null) {
            enderecoDTO.setPessoaId(endereco.getPessoa().getId());
        }
        return enderecoDTO;
    }

    private Endereco convertToEntity(EnderecoDTO enderecoDTO) {
        Endereco endereco = modelMapper.map(enderecoDTO, Endereco.class);
        if (enderecoDTO.getPessoaId() != null) {
            Pessoa pessoa = pessoaRepository.findById(enderecoDTO.getPessoaId()).orElse(null);
            endereco.setPessoa(pessoa);
        }
        return endereco;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<EnderecoDTO> getAllEnderecos() {
        List<Endereco> enderecos = enderecoRepository.findAll();
        return enderecos.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<EnderecoDTO> getEnderecoById(@PathVariable Long id) {
        return enderecoRepository.findById(id)
                .map(endereco -> ResponseEntity.ok().body(convertToDto(endereco)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EnderecoDTO> createEndereco(@RequestBody EnderecoDTO enderecoDTO) {
        Endereco endereco = convertToEntity(enderecoDTO);
        Endereco enderecoSalvo = enderecoRepository.save(endereco);
        return ResponseEntity.ok(convertToDto(enderecoSalvo));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<EnderecoDTO> updateEndereco(@PathVariable Long id, @RequestBody EnderecoDTO enderecoDTO) {
        return enderecoRepository.findById(id)
                .map(enderecoExistente -> {
                    enderecoExistente.setLogradouro(enderecoDTO.getLogradouro());
                    enderecoExistente.setCidade(enderecoDTO.getCidade());
                    enderecoExistente.setEstado(enderecoDTO.getEstado());

                    if (enderecoDTO.getPessoaId() != null) {
                        Pessoa pessoa = pessoaRepository.findById(enderecoDTO.getPessoaId()).orElse(null);
                        enderecoExistente.setPessoa(pessoa);
                    } else {
                        enderecoExistente.setPessoa(null);
                    }

                    Endereco enderecoAtualizado = enderecoRepository.save(enderecoExistente);
                    return ResponseEntity.ok(convertToDto(enderecoAtualizado));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteEndereco(@PathVariable Long id) {
        return enderecoRepository.findById(id)
                .map(endereco -> {
                    enderecoRepository.delete(endereco);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}