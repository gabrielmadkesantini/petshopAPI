package com.petshop.repository;

import com.petshop.model.Pets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PetsRepository extends JpaRepository<Pets, Long> {
    
    List<Pets> findByClienteId(Long clienteId);
    
    @Query("SELECT p FROM Pets p WHERE p.nome LIKE %:nome%")
    List<Pets> findByNomeContaining(@Param("nome") String nome);
    
    List<Pets> findByRacaId(Long racaId);
    
    List<Pets> findByDataNascimento(LocalDate dataNascimento);
    
    @Query("SELECT p FROM Pets p WHERE p.dataNascimento BETWEEN :dataInicio AND :dataFim")
    List<Pets> findByDataNascimentoBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT p FROM Pets p WHERE p.cliente.id = :clienteId AND p.nome LIKE %:nome%")
    List<Pets> findByClienteIdAndNomeContaining(@Param("clienteId") Long clienteId, @Param("nome") String nome);
    
    List<Pets> findByClienteIdAndRacaId(Long clienteId, Long racaId);
    
    @Query("SELECT p FROM Pets p WHERE p.nome LIKE %:nome% AND p.raca.id = :racaId")
    List<Pets> findByNomeContainingAndRacaId(@Param("nome") String nome, @Param("racaId") Long racaId);
    
    void deleteByRacaId(Long racaId);
}









