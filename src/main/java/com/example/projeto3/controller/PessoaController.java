package com.example.projeto3.controller;

import com.example.projeto3.model.Endereco;
import com.example.projeto3.model.Pessoa;
import com.example.projeto3.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    // Obter todas as pessoas
    @GetMapping
    public List<Pessoa> getAllPessoas() {
        return pessoaRepository.findAll();
    }

    // Obter uma pessoa por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> getPessoaById(@PathVariable Long id) {
        return pessoaRepository.findById(id)
                .map(pessoa -> ResponseEntity.ok().body(pessoa))
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar uma nova pessoa com endereços
    @PostMapping
    public Pessoa createPessoa(@RequestBody Pessoa pessoa) {
        if (pessoa.getEnderecos() != null) {
            pessoa.getEnderecos().forEach(endereco -> endereco.setPessoa(pessoa));
        }
        return pessoaRepository.save(pessoa);
    }

    // Atualizar uma pessoa existente
    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> updatePessoa(@PathVariable Long id, @RequestBody Pessoa detalhesPessoa) {
        return pessoaRepository.findById(id)
                .map(pessoa -> {
                    pessoa.setNome(detalhesPessoa.getNome());
                    pessoa.setEmail(detalhesPessoa.getEmail());

                    // Atualizar endereços
                    pessoa.getEnderecos().clear();
                    if (detalhesPessoa.getEnderecos() != null) {
                        detalhesPessoa.getEnderecos().forEach(endereco -> {
                            endereco.setPessoa(pessoa);
                            pessoa.getEnderecos().add(endereco);
                        });
                    }

                    Pessoa pessoaAtualizada = pessoaRepository.save(pessoa);
                    return ResponseEntity.ok().body(pessoaAtualizada);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Deletar uma pessoa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePessoa(@PathVariable Long id) {
        return pessoaRepository.findById(id)
                .map(pessoa -> {
                    pessoaRepository.delete(pessoa);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}