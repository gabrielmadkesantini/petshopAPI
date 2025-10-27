package com.petshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AtendimentoCreateRequest {
    
    @NotNull(message = "Pet é obrigatório")
    private PetReference pet;
    
    @NotNull(message = "Cliente é obrigatório")
    private ClienteReference cliente;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;
    
    @NotNull(message = "Data é obrigatória")
    private LocalDate data;
    
    public AtendimentoCreateRequest() {}
    
    public AtendimentoCreateRequest(PetReference pet, ClienteReference cliente, String descricao, BigDecimal valor, LocalDate data) {
        this.pet = pet;
        this.cliente = cliente;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }
    
    public PetReference getPet() {
        return pet;
    }
    
    public void setPet(PetReference pet) {
        this.pet = pet;
    }
    
    public ClienteReference getCliente() {
        return cliente;
    }
    
    public void setCliente(ClienteReference cliente) {
        this.cliente = cliente;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public void setData(LocalDate data) {
        this.data = data;
    }
    
    public static class PetReference {
        @NotNull(message = "ID do pet é obrigatório")
        private Long id;
        
        public PetReference() {}
        
        public PetReference(Long id) {
            this.id = id;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
    }
    
    public static class ClienteReference {
        @NotNull(message = "ID do cliente é obrigatório")
        private Long id;
        
        public ClienteReference() {}
        
        public ClienteReference(Long id) {
            this.id = id;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
    }
}

