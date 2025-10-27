package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Contato")
public class Contato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "Cliente é obrigatório")
    @JsonIgnoreProperties({"pets", "contatos", "enderecos", "usuario"})
    private Cliente cliente;
    
    @Column(name = "tag", length = 50)
    private String tag;
    
    @NotNull(message = "Tipo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20, nullable = false)
    private TipoContato tipo;
    
    @NotBlank(message = "Valor é obrigatório")
    @Column(name = "valor", length = 100, nullable = false)
    private String valor;
    
    public Contato() {}
    
    public Contato(Cliente cliente, String tag, TipoContato tipo, String valor) {
        this.cliente = cliente;
        this.tag = tag;
        this.tipo = tipo;
        this.valor = valor;
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
    
    public String getTag() {
        return tag;
    }
    
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public TipoContato getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoContato tipo) {
        this.tipo = tipo;
    }
    
    public String getValor() {
        return valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
    }
    
    public enum TipoContato {
        email, telefone
    }
}


