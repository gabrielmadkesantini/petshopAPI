package com.petshop.model;

public enum PerfilUsuario {
    CLIENTE("Cliente"),
    ADMIN("Admin");
    
    private final String descricao;
    
    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}







