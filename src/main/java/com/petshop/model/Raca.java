package com.petshop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "Raca")
public class Raca {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Column(name = "descricao", length = 100, nullable = false)
    private String descricao;
    
    @OneToMany(mappedBy = "raca", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pets> pets;
    
    public Raca() {}
    
    public Raca(String descricao) {
        this.descricao = descricao;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public List<Pets> getPets() {
        return pets;
    }
    
    public void setPets(List<Pets> pets) {
        this.pets = pets;
    }
}









