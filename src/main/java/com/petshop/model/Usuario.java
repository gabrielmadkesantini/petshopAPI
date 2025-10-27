package com.petshop.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "Usuario")
public class Usuario {
    
    @Id
    @Column(name = "cpf", length = 14)
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    private String cpf;
    
    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;
    
    @NotNull(message = "Perfil é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", length = 20, nullable = false)
    private PerfilUsuario perfil;
    
    @NotBlank(message = "Senha é obrigatória")
    @Column(name = "senha", length = 255, nullable = false)
    private String senha;
    
    public Usuario() {}
    
    public Usuario(String cpf, String nome, PerfilUsuario perfil, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.perfil = perfil;
        this.senha = senha;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public PerfilUsuario getPerfil() {
        return perfil;
    }
    
    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.perfil == PerfilUsuario.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }
    
}


