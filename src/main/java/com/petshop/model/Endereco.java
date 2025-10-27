package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Endereco")
public class Endereco {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "Cliente é obrigatório")
    @JsonIgnoreProperties({"pets", "contatos", "enderecos", "usuario"})
    private Cliente cliente;
    
    @NotBlank(message = "Logradouro é obrigatório")
    @Column(name = "logradouro", length = 150, nullable = false)
    private String logradouro;
    
    @NotBlank(message = "Cidade é obrigatória")
    @Column(name = "cidade", length = 100, nullable = false)
    private String cidade;
    
    @Column(name = "bairro", length = 100)
    private String bairro;
    
    @Column(name = "complemento", length = 100)
    private String complemento;
    
    @Column(name = "tag", length = 50)
    private String tag;
    
    public Endereco() {}
    
    public Endereco(Cliente cliente, String logradouro, String cidade, String bairro, String complemento, String tag) {
        this.cliente = cliente;
        this.logradouro = logradouro;
        this.cidade = cidade;
        this.bairro = bairro;
        this.complemento = complemento;
        this.tag = tag;
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
    
    public String getLogradouro() {
        return logradouro;
    }
    
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }
    
    public String getCidade() {
        return cidade;
    }
    
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    
    public String getBairro() {
        return bairro;
    }
    
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    
    public String getComplemento() {
        return complemento;
    }
    
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
}


