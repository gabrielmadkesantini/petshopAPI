package com.petshop.repository;

import com.petshop.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    
    List<Endereco> findByClienteId(Long clienteId);
    
    @Query("SELECT e FROM Endereco e WHERE e.cidade LIKE %:cidade%")
    List<Endereco> findByCidadeContaining(@Param("cidade") String cidade);
    
    @Query("SELECT e FROM Endereco e WHERE e.bairro LIKE %:bairro%")
    List<Endereco> findByBairroContaining(@Param("bairro") String bairro);
    
    @Query("SELECT e FROM Endereco e WHERE e.logradouro LIKE %:logradouro%")
    List<Endereco> findByLogradouroContaining(@Param("logradouro") String logradouro);
    
    @Query("SELECT e FROM Endereco e WHERE e.tag LIKE %:tag%")
    List<Endereco> findByTagContaining(@Param("tag") String tag);
    
    @Query("SELECT e FROM Endereco e WHERE e.cliente.id = :clienteId AND e.cidade LIKE %:cidade%")
    List<Endereco> findByClienteIdAndCidadeContaining(@Param("clienteId") Long clienteId, @Param("cidade") String cidade);
    
    @Query("SELECT e FROM Endereco e WHERE e.cidade LIKE %:cidade% AND e.bairro LIKE %:bairro%")
    List<Endereco> findByCidadeAndBairroContaining(@Param("cidade") String cidade, @Param("bairro") String bairro);
    
    @Query("SELECT e FROM Endereco e JOIN FETCH e.cliente c WHERE " +
           "(:clienteId IS NULL OR c.id = :clienteId) AND " +
           "(:cidade IS NULL OR e.cidade LIKE %:cidade%) AND " +
           "(:bairro IS NULL OR e.bairro LIKE %:bairro%) AND " +
           "(:logradouro IS NULL OR e.logradouro LIKE %:logradouro%) AND " +
           "(:tag IS NULL OR e.tag LIKE %:tag%)")
    List<Endereco> findByAllFilters(@Param("clienteId") Long clienteId, 
                                   @Param("cidade") String cidade, 
                                   @Param("bairro") String bairro, 
                                   @Param("logradouro") String logradouro, 
                                   @Param("tag") String tag);
}









