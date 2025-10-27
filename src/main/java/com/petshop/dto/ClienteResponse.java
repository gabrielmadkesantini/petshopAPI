package com.petshop.dto;

import java.time.LocalDate;

public class ClienteResponse {
    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataCadastro;
    private String foto;
    
    public ClienteResponse() {}
    
    public ClienteResponse(Long id, String nome, String cpf, LocalDate dataCadastro, String foto) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataCadastro = dataCadastro;
        this.foto = foto;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public LocalDate getDataCadastro() {
        return dataCadastro;
    }
    
    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    public String getFoto() {
        return foto;
    }
    
    public void setFoto(String foto) {
        this.foto = foto;
    }
}


