package com.petshop.repository;

import com.petshop.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    @Query("SELECT c FROM Cliente c WHERE c.nome LIKE %:nome%")
    List<Cliente> findByNomeContaining(@Param("nome") String nome);
    
    @Query(value = "SELECT * FROM Cliente WHERE cpf = :cpf LIMIT 1", nativeQuery = true)
    Optional<Cliente> findByCpf(@Param("cpf") String cpf);
    
    @Query("SELECT c FROM Cliente c WHERE c.nome LIKE %:nome% AND c.cpf = :cpf")
    List<Cliente> findByNomeAndCpf(@Param("nome") String nome, @Param("cpf") String cpf);
    
    List<Cliente> findByDataCadastro(LocalDate dataCadastro);
    
    @Query("SELECT c FROM Cliente c WHERE c.dataCadastro BETWEEN :dataInicio AND :dataFim")
    List<Cliente> findByDataCadastroBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    boolean existsByCpf(String cpf);
    
    void deleteByCpf(String cpf);
}









