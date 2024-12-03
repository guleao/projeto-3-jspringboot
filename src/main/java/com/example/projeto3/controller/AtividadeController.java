package com.example.projeto3.controller;


import com.example.projeto3.model.Atividade;
import com.example.projeto3.repository.AtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    @Autowired
    private AtividadeRepository atividadeRepository;

    @GetMapping
    public List<Atividade> getAllAtividades() {
        return atividadeRepository.findAll();
    }

    @PostMapping
    public Atividade createAtividade(@RequestBody Atividade atividade) {
        return atividadeRepository.save(atividade);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atividade> getAtividadeById(@PathVariable Long id) {
        return atividadeRepository.findById(id)
                .map(atividade -> ResponseEntity.ok().body(atividade))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Atividade> updateAtividade(@PathVariable Long id, @RequestBody Atividade detalhesAtividade) {
        return atividadeRepository.findById(id)
                .map(atividade -> {
                    atividade.setAtividade(detalhesAtividade.getAtividade());
                    atividade.setPessoa(detalhesAtividade.getPessoa());
                    atividade.setDataExpiracao(detalhesAtividade.getDataExpiracao());
                    atividade.setSituacao(detalhesAtividade.getSituacao());
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