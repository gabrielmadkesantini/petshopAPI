package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Pets")
public class Pets {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "Cliente é obrigatório")
    @JsonIgnoreProperties({"pets", "contatos", "enderecos", "hibernateLazyInitializer", "handler"})
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_raca")
    @JsonIgnoreProperties({"pets", "hibernateLazyInitializer", "handler"})
    private Raca raca;
    
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;
    
    @Column(name = "foto", length = 255)
    private String foto;
    
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Atendimento> atendimentos;
    
    public Pets() {}
    
    public Pets(Cliente cliente, Raca raca, LocalDate dataNascimento, String nome, String foto) {
        this.cliente = cliente;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.nome = nome;
        this.foto = foto;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public Raca getRaca() {
        return raca;
    }
    
    public void setRaca(Raca raca) {
        this.raca = raca;
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
    
    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }
    
    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }
}









