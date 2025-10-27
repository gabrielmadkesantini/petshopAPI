package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Atendimento")
public class Atendimento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pet", nullable = false)
    @NotNull(message = "Pet é obrigatório")
    @JsonIgnoreProperties({"atendimentos", "cliente", "hibernateLazyInitializer", "handler"})
    private Pets pet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "Cliente é obrigatório")
    @JsonIgnoreProperties({"pets", "contatos", "enderecos", "atendimentos", "hibernateLazyInitializer", "handler"})
    private Cliente cliente;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Column(name = "descricao", columnDefinition = "TEXT", nullable = false)
    private String descricao;
    
    @Column(name = "valor", precision = 10, scale = 2)
    private BigDecimal valor;
    
    @CreationTimestamp
    @Column(name = "data", nullable = false)
    private LocalDate data;
    
    public Atendimento() {}
    
    public Atendimento(Pets pet, Cliente cliente, String descricao, BigDecimal valor) {
        this.pet = pet;
        this.cliente = cliente;
        this.descricao = descricao;
        this.valor = valor;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Pets getPet() {
        return pet;
    }
    
    public void setPet(Pets pet) {
        this.pet = pet;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
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
}









