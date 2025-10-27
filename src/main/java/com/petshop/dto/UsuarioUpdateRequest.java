package com.petshop.dto;

import com.petshop.model.PerfilUsuario;
import jakarta.validation.constraints.Pattern;

public class UsuarioUpdateRequest {
    
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    private String cpf;
    
    private String nome;
    
    private PerfilUsuario perfil;
    
    private String senha;
    
    public UsuarioUpdateRequest() {}
    
    public UsuarioUpdateRequest(String cpf, String nome, PerfilUsuario perfil, String senha) {
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
}









