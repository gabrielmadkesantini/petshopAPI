package com.petshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PetCreateRequest {
    
    @NotNull(message = "ID do cliente é obrigatório")
    private Long id_cliente;
    
    private Long id_raca;
    
    private LocalDate dataNascimento;
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String foto;
    
    public PetCreateRequest() {}
    
    public PetCreateRequest(Long id_cliente, Long id_raca, LocalDate dataNascimento, String nome, String foto) {
        this.id_cliente = id_cliente;
        this.id_raca = id_raca;
        this.dataNascimento = dataNascimento;
        this.nome = nome;
        this.foto = foto;
    }
    
    public Long getId_cliente() {
        return id_cliente;
    }
    
    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }
    
    public Long getId_raca() {
        return id_raca;
    }
    
    public void setId_raca(Long id_raca) {
        this.id_raca = id_raca;
    }
    
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getFoto() {
        return foto;
    }
    
    public void setFoto(String foto) {
        this.foto = foto;
    }
}


