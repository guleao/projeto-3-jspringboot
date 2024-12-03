package com.example.projeto3.controller;

import com.example.projeto3.DTO.AtividadeDTO;
import com.example.projeto3.DTO.PessoaNomeDTO;
import com.example.projeto3.model.Atividade;
import com.example.projeto3.model.Pessoa;
import com.example.projeto3.repository.AtividadeRepository;
import com.example.projeto3.repository.PessoaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/atividades")
public class AtividadeController {

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @Transactional(readOnly = true)
    public List<AtividadeDTO> getAllAtividades() {
        List<Atividade> atividades = atividadeRepository.findAll();
        return atividades.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/ordenadas-expiracao")
    @Transactional(readOnly = true)
    public List<AtividadeDTO> getAtividadesOrdenadasPorExpiracao() {
        List<Atividade> atividades = atividadeRepository.findAllByOrderByDataExpiracaoAsc();
        return atividades.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createAtividade(@RequestBody AtividadeDTO atividadeDTO) {
        Atividade atividade = convertToEntity(atividadeDTO);

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
        AtividadeDTO respostaDTO = convertToDto(atividadeSalva);
        return ResponseEntity.ok(respostaDTO);
    }

    // Obter uma atividade por ID
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<AtividadeDTO> getAtividadeById(@PathVariable Long id) {
        return atividadeRepository.findById(id)
                .map(atividade -> ResponseEntity.ok().body(convertToDto(atividade)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateAtividade(@PathVariable Long id, @RequestBody AtividadeDTO atividadeDTO) {
        return atividadeRepository.findById(id)
                .map(atividadeExistente -> {
                    atividadeExistente.setAtividade(atividadeDTO.getAtividade());
                    atividadeExistente.setDataExpiracao(atividadeDTO.getDataExpiracao());

                    if (atividadeDTO.getSituacao() != null) {
                        atividadeExistente.setSituacao(atividadeDTO.getSituacao());
                    }

                    if (atividadeDTO.getPessoa() != null) {
                        Long pessoaId = atividadeDTO.getPessoa().getId();
                        Pessoa pessoa = pessoaRepository.findById(pessoaId).orElse(null);
                        if (pessoa == null) {
                            return ResponseEntity.badRequest().body("Pessoa com ID " + pessoaId + " não encontrada.");
                        }
                        atividadeExistente.setPessoa(pessoa);
                    }

                    Atividade atividadeAtualizada = atividadeRepository.save(atividadeExistente);
                    AtividadeDTO respostaDTO = convertToDto(atividadeAtualizada);
                    return ResponseEntity.ok().body(respostaDTO);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteAtividade(@PathVariable Long id) {
        return atividadeRepository.findById(id)
                .map(atividade -> {
                    atividadeRepository.delete(atividade);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    private AtividadeDTO convertToDto(Atividade atividade) {
        AtividadeDTO atividadeDTO = new AtividadeDTO();
        atividadeDTO.setId(atividade.getId());
        atividadeDTO.setAtividade(atividade.getAtividade());
        atividadeDTO.setDataExpiracao(atividade.getDataExpiracao());
        atividadeDTO.setSituacao(atividade.getSituacao());

        if (atividade.getPessoa() != null) {
            PessoaNomeDTO pessoaNomeDTO = new PessoaNomeDTO();
            pessoaNomeDTO.setId(atividade.getPessoa().getId());
            pessoaNomeDTO.setNome(atividade.getPessoa().getNome());
            atividadeDTO.setPessoa(pessoaNomeDTO);
        }

        return atividadeDTO;
    }

    private Atividade convertToEntity(AtividadeDTO atividadeDTO) {
        Atividade atividade = new Atividade();
        atividade.setId(atividadeDTO.getId());
        atividade.setAtividade(atividadeDTO.getAtividade());
        atividade.setDataExpiracao(atividadeDTO.getDataExpiracao());
        atividade.setSituacao(atividadeDTO.getSituacao());

        if (atividadeDTO.getPessoa() != null) {
            Pessoa pessoa = new Pessoa();
            pessoa.setId(atividadeDTO.getPessoa().getId());
            atividade.setPessoa(pessoa);
        }

        return atividade;
    }
}