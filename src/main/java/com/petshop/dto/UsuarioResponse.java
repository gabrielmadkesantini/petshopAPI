package com.petshop.dto;

import com.petshop.model.PerfilUsuario;

public class UsuarioResponse {
    private String cpf;
    private String nome;
    private PerfilUsuario perfil;
    private ClienteResponse cliente;
    
    public UsuarioResponse() {}
    
    public UsuarioResponse(String cpf, String nome, PerfilUsuario perfil) {
        this.cpf = cpf;
        this.nome = nome;
        this.perfil = perfil;
    }
    
    public UsuarioResponse(String cpf, String nome, PerfilUsuario perfil, ClienteResponse cliente) {
        this.cpf = cpf;
        this.nome = nome;
        this.perfil = perfil;
        this.cliente = cliente;
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
    
    public ClienteResponse getCliente() {
        return cliente;
    }
    
    public void setCliente(ClienteResponse cliente) {
        this.cliente = cliente;
    }
}

