package com.petshop.repository;

import com.petshop.model.Raca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RacaRepository extends JpaRepository<Raca, Long> {
    
    @Query("SELECT r FROM Raca r WHERE r.descricao LIKE %:descricao%")
    List<Raca> findByDescricaoContaining(@Param("descricao") String descricao);
    
    Optional<Raca> findByDescricao(String descricao);
    
    @Query("SELECT r FROM Raca r WHERE LOWER(r.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<Raca> findByDescricaoContainingIgnoreCase(@Param("descricao") String descricao);
}









