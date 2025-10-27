package com.petshop.dto;

import com.petshop.model.PerfilUsuario;
import com.petshop.model.Cliente;

public class UsuarioCreateResponse {
    private String token;
    private String cpf;
    private String nome;
    private PerfilUsuario perfil;
    private Cliente cliente; // Informações do cliente quando perfil é CLIENTE
    
    public UsuarioCreateResponse() {}
    
    public UsuarioCreateResponse(String token, String cpf, String nome, PerfilUsuario perfil) {
        this.token = token;
        this.cpf = cpf;
        this.nome = nome;
        this.perfil = perfil;
    }
    
    public UsuarioCreateResponse(String token, String cpf, String nome, PerfilUsuario perfil, Cliente cliente) {
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
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}





