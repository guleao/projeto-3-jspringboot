package com.example.projeto3.controller;

import com.example.projeto3.model.Atividade;
import com.example.projeto3.model.Pessoa;
import com.example.projeto3.repository.AtividadeRepository;
import com.example.projeto3.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @GetMapping
    public List<Atividade> getAllAtividades() {
        return atividadeRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createAtividade(@RequestBody Atividade atividade) {
        if (atividade.getPessoa() != null) {
            Long pessoaId = atividade.getPessoa().getId();
            Pessoa pessoa = pessoaRepository.findById(pessoaId)
                    .orElse(null);
            if (pessoa == null) {
                return ResponseEntity.badRequest().body("Pessoa com ID " + pessoaId + " não encontrada.");
            }
            atividade.setPessoa(pessoa);
        }
        Atividade atividadeSalva = atividadeRepository.save(atividade);
        return ResponseEntity.ok(atividadeSalva);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atividade> getAtividadeById(@PathVariable Long id) {
        return atividadeRepository.findById(id)
                .map(atividade -> ResponseEntity.ok().body(atividade))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAtividade(@PathVariable Long id, @RequestBody Atividade detalhesAtividade) {
        return atividadeRepository.findById(id)
                .map(atividade -> {
                    atividade.setAtividade(detalhesAtividade.getAtividade());
                    atividade.setDataExpiracao(detalhesAtividade.getDataExpiracao());
                    atividade.setSituacao(detalhesAtividade.getSituacao());

                    if (detalhesAtividade.getPessoa() != null) {
                        Long pessoaId = detalhesAtividade.getPessoa().getId();
                        Pessoa pessoa = pessoaRepository.findById(pessoaId).orElse(null);
                        if (pessoa == null) {
                            return ResponseEntity.badRequest().body("Pessoa com ID " + pessoaId + " não encontrada.");
                        }
                        atividade.setPessoa(pessoa);
                    }

                    Atividade atualizado = atividadeRepository.save(atividade);
                    return ResponseEntity.ok().body(atualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAtividade(@PathVariable Long id) {
        return atividadeRepository.findById(id)
                .map(atividade -> {
                    atividadeRepository.delete(atividade);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}