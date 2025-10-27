package com.petshop.repository;

import com.petshop.model.Usuario;
import com.petshop.model.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    @Query("SELECT u FROM Usuario u WHERE u.nome LIKE %:nome%")
    List<Usuario> findByNomeContaining(@Param("nome") String nome);

    Optional<Usuario> findByCpf(String cpf);

    List<Usuario> findByPerfil(PerfilUsuario perfil);
    
    @Query("SELECT u FROM Usuario u WHERE u.nome LIKE %:nome% AND u.perfil = :perfil")
    List<Usuario> findByNomeAndPerfil(@Param("nome") String nome, @Param("perfil") PerfilUsuario perfil);
    
    boolean existsByCpf(String cpf);
    
    void deleteByCpf(String cpf);
}


