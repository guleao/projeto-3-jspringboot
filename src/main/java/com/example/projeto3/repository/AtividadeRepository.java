package com.example.projeto3.repository;

import com.example.projeto3.model.Atividade;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    List<Atividade> findAllByOrderByDataExpiracaoAsc();
}