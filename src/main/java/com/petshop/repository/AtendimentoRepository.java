package com.petshop.repository;

import com.petshop.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
    
    List<Atendimento> findByPetId(Long petId);
    
    List<Atendimento> findByData(LocalDate data);
    
    @Query("SELECT a FROM Atendimento a WHERE a.data BETWEEN :dataInicio AND :dataFim")
    List<Atendimento> findByDataBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT a FROM Atendimento a WHERE a.valor = :valor")
    List<Atendimento> findByValor(@Param("valor") BigDecimal valor);
    
    @Query("SELECT a FROM Atendimento a WHERE a.valor BETWEEN :valorMin AND :valorMax")
    List<Atendimento> findByValorBetween(@Param("valorMin") BigDecimal valorMin, @Param("valorMax") BigDecimal valorMax);
    
    @Query("SELECT a FROM Atendimento a WHERE a.descricao LIKE %:descricao%")
    List<Atendimento> findByDescricaoContaining(@Param("descricao") String descricao);
    
    List<Atendimento> findByPetIdAndData(Long petId, LocalDate data);
    
    @Query("SELECT a FROM Atendimento a WHERE a.pet.id = :petId AND a.data BETWEEN :dataInicio AND :dataFim")
    List<Atendimento> findByPetIdAndDataBetween(@Param("petId") Long petId, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    List<Atendimento> findByClienteId(Long clienteId);
    
    List<Atendimento> findByClienteIdAndData(Long clienteId, LocalDate data);
    
    @Query("SELECT a FROM Atendimento a WHERE a.cliente.id = :clienteId AND a.data BETWEEN :dataInicio AND :dataFim")
    List<Atendimento> findByClienteIdAndDataBetween(@Param("clienteId") Long clienteId, @Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT a FROM Atendimento a WHERE a.cliente.id = :clienteId AND a.valor BETWEEN :valorMin AND :valorMax")
    List<Atendimento> findByClienteIdAndValorBetween(@Param("clienteId") Long clienteId, @Param("valorMin") BigDecimal valorMin, @Param("valorMax") BigDecimal valorMax);
    
    @Query("SELECT a FROM Atendimento a WHERE a.cliente.id = :clienteId AND a.descricao LIKE %:descricao%")
    List<Atendimento> findByClienteIdAndDescricaoContaining(@Param("clienteId") Long clienteId, @Param("descricao") String descricao);
    
    List<Atendimento> findByClienteIdAndPetId(Long clienteId, Long petId);
    
    List<Atendimento> findByClienteIdAndPetIdAndData(Long clienteId, Long petId, LocalDate data);
    
    @Query("SELECT a FROM Atendimento a LEFT JOIN FETCH a.pet p LEFT JOIN FETCH p.raca LEFT JOIN FETCH a.cliente WHERE a.id = :id")
    Optional<Atendimento> findByIdWithDetails(@Param("id") Long id);
}









