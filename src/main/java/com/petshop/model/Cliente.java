package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Cliente")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;
    
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    @Column(name = "cpf", length = 14, unique = true)
    private String cpf;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpf", referencedColumnName = "cpf", insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuario;
    
    @CreationTimestamp
    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;
    
    @Column(name = "foto", length = 255)
    private String foto;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Contato> contatos;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Endereco> enderecos;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pets> pets;
    
    public Cliente() {}
    
    public Cliente(String nome, String cpf, String foto) {
        this.nome = nome;
        this.cpf = cpf;
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
    
    public List<Contato> getContatos() {
        return contatos;
    }
    
    public void setContatos(List<Contato> contatos) {
        this.contatos = contatos;
    }
    
    public List<Endereco> getEnderecos() {
        return enderecos;
    }
    
    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }
    
    public List<Pets> getPets() {
        return pets;
    }
    
    public void setPets(List<Pets> pets) {
        this.pets = pets;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}


