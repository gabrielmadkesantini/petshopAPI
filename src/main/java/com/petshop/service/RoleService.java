package com.petshop.service;

import com.petshop.model.Atendimento;
import com.petshop.model.Cliente;
import com.petshop.model.Pets;
import com.petshop.model.Usuario;
import com.petshop.repository.AtendimentoRepository;
import com.petshop.repository.ClienteRepository;
import com.petshop.repository.PetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PetsRepository petsRepository;
    
    @Autowired
    private AtendimentoRepository atendimentoRepository;
    
    /**
     * Verifica se o usuário atual é Admin
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
    
    /**
     * Verifica se o usuário atual é Cliente
     */
    public boolean isCliente() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER") && 
                        !auth.getAuthority().equals("ROLE_ADMIN"));
    }
    
    /**
     * Obtém o usuário atual autenticado
     */
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            return (Usuario) authentication.getPrincipal();
        }
        return null;
    }
    
    /**
     * Obtém o CPF do usuário atual autenticado
     */
    public String getCurrentUserCpf() {
        Usuario currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getCpf() : null;
    }
    
    /**
     * Verifica se o usuário pode acessar um cliente específico
     * Admin pode acessar qualquer cliente
     * Cliente só pode acessar seus próprios dados
     */
    public boolean canAccessCliente(Long clienteId) {
        if (isAdmin()) {
            return true;
        }
        
        String currentUserCpf = getCurrentUserCpf();
        if (currentUserCpf == null) {
            return false;
        }
        
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            return false;
        }
        
        return cliente.getCpf().equals(currentUserCpf);
    }
    
    /**
     * Verifica se o usuário pode acessar um pet específico
     * Admin pode acessar qualquer pet
     * Cliente só pode acessar pets de seus próprios clientes
     */
    public boolean canAccessPet(Long petId) {
        if (isAdmin()) {
            return true;
        }
        
        String currentUserCpf = getCurrentUserCpf();
        if (currentUserCpf == null) {
            return false;
        }
        
        Pets pet = petsRepository.findById(petId).orElse(null);
        if (pet == null) {
            return false;
        }
        
        return pet.getCliente().getCpf().equals(currentUserCpf);
    }
    
    /**
     * Verifica se o usuário pode criar/editar um cliente
     * Admin pode criar/editar qualquer cliente
     * Cliente só pode editar seus próprios dados
     */
    public boolean canModifyCliente(Long clienteId) {
        if (isAdmin()) {
            return true;
        }
        
        if (clienteId == null) {
            return false; // Clientes não podem criar outros clientes
        }
        
        return canAccessCliente(clienteId);
    }
    
    /**
     * Verifica se o usuário pode criar/editar um pet
     * Admin pode criar/editar qualquer pet
     * Cliente só pode criar/editar pets de seus próprios clientes
     */
    public boolean canModifyPet(Long petId, Long clienteId) {
        if (isAdmin()) {
            return true;
        }
        
        String currentUserCpf = getCurrentUserCpf();
        if (currentUserCpf == null) {
            return false;
        }
        
        if (petId == null && clienteId != null) {
            Cliente cliente = clienteRepository.findByCpf(currentUserCpf).orElse(null);
            return cliente != null && cliente.getId().equals(clienteId);
        }
        
        if (petId != null) {
            return canAccessPet(petId);
        }
        
        return false;
    }
    
    /**
     * Verifica se o usuário pode deletar um cliente
     * Apenas Admin pode deletar clientes
     */
    public boolean canDeleteCliente() {
        return isAdmin();
    }
    
    /**
     * Verifica se o usuário pode deletar um pet
     * Admin pode deletar qualquer pet
     * Cliente só pode deletar seus próprios pets
     */
    public boolean canDeletePet(Long petId) {
        if (isAdmin()) {
            return true;
        }
        
        return canAccessPet(petId);
    }
    
    /**
     * Verifica se o usuário pode acessar um atendimento específico
     * Admin pode acessar qualquer atendimento
     * Cliente só pode acessar atendimentos de seus próprios pets
     */
    public boolean canAccessAtendimento(Long atendimentoId) {
        if (isAdmin()) {
            return true;
        }
        
        String currentUserCpf = getCurrentUserCpf();
        if (currentUserCpf == null) {
            return false;
        }
        
        Atendimento atendimento = atendimentoRepository.findById(atendimentoId).orElse(null);
        if (atendimento == null) {
            return false;
        }
        
        return atendimento.getPet().getCliente().getCpf().equals(currentUserCpf);
    }
    
    /**
     * Verifica se o usuário pode criar/editar um atendimento
     * Admin pode criar/editar qualquer atendimento
     * Cliente só pode criar/editar atendimentos de seus próprios pets
     */
    public boolean canModifyAtendimento(Long atendimentoId, Long petId) {
        if (isAdmin()) {
            return true;
        }
        
        String currentUserCpf = getCurrentUserCpf();
        if (currentUserCpf == null) {
            return false;
        }
        
        if (atendimentoId == null && petId != null) {
            Pets pet = petsRepository.findById(petId).orElse(null);
            return pet != null && pet.getCliente().getCpf().equals(currentUserCpf);
        }
        
        if (atendimentoId != null) {
            return canAccessAtendimento(atendimentoId);
        }
        
        return false;
    }
    
    /**
     * Verifica se o usuário pode deletar um atendimento
     * Admin pode deletar qualquer atendimento
     * Cliente só pode deletar seus próprios atendimentos
     */
    public boolean canDeleteAtendimento(Long atendimentoId) {
        if (isAdmin()) {
            return true;
        }
        
        return canAccessAtendimento(atendimentoId);
    }
}

