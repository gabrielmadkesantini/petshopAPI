package com.petshop.repository;

import com.petshop.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContatoRepository extends JpaRepository<Contato, Long> {
    
    List<Contato> findByClienteId(Long clienteId);
    
    List<Contato> findByTipo(Contato.TipoContato tipo);
    
    @Query("SELECT c FROM Contato c WHERE c.valor LIKE %:valor%")
    List<Contato> findByValorContaining(@Param("valor") String valor);
    
    @Query("SELECT c FROM Contato c WHERE c.tag LIKE %:tag%")
    List<Contato> findByTagContaining(@Param("tag") String tag);
    
    List<Contato> findByClienteIdAndTipo(Long clienteId, Contato.TipoContato tipo);
    
    @Query("SELECT c FROM Contato c WHERE c.cliente.id = :clienteId AND c.valor LIKE %:valor%")
    List<Contato> findByClienteIdAndValorContaining(@Param("clienteId") Long clienteId, @Param("valor") String valor);
}









