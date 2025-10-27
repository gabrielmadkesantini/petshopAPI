package com.petshop.dto;

import com.petshop.model.PerfilUsuario;

public class LoginResponse {
    private String token;
    private String cpf; // CPF agora é o identificador principal
    private String nome;
    private PerfilUsuario perfil;
    private ClienteResponse cliente;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, String cpf, String nome, PerfilUsuario perfil) {
        this.token = token;
        this.cpf = cpf;
        this.nome = nome;
        this.perfil = perfil;
    }
    
    public LoginResponse(String token, String cpf, String nome, PerfilUsuario perfil, ClienteResponse cliente) {
        this.token = token;
        this.cpf = cpf;
        this.nome = nome;
        this.perfil = perfil;
        this.cliente = cliente;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
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


