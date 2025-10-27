package com.petshop.controller;

import com.petshop.model.Endereco;
import com.petshop.repository.EnderecoRepository;
import com.petshop.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enderecos")
@CrossOrigin(origins = "*")
public class EnderecoController {
    
    @Autowired
    private EnderecoRepository enderecoRepository;
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Endereco>> getAllEnderecos() {
        List<Endereco> enderecos = enderecoRepository.findAll();
        return ResponseEntity.ok(enderecos);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Endereco> getEnderecoById(@PathVariable Long id) {
        Optional<Endereco> endereco = enderecoRepository.findById(id);
        if (endereco.isPresent()) {
            if (!roleService.canAccessCliente(endereco.get().getCliente().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(endereco.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Endereco>> getEnderecosByCliente(@PathVariable Long clienteId) {
        if (!roleService.canAccessCliente(clienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Endereco> enderecos = enderecoRepository.findByClienteId(clienteId);
        return ResponseEntity.ok(enderecos);
    }
    
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Endereco>> getEnderecos(@RequestParam(required = false) Long clienteId,
                                                       @RequestParam(required = false) String cidade,
                                                       @RequestParam(required = false) String bairro,
                                                       @RequestParam(required = false) String logradouro,
                                                       @RequestParam(required = false) String tag) {
        if (clienteId != null && !roleService.canAccessCliente(clienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Endereco> enderecos = enderecoRepository.findByAllFilters(clienteId, cidade, bairro, logradouro, tag);
        
        if (!roleService.isAdmin()) {
            enderecos = enderecos.stream()
                    .filter(endereco -> roleService.canAccessCliente(endereco.getCliente().getId()))
                    .toList();
        }
        
        return ResponseEntity.ok(enderecos);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Endereco> createEndereco(@Valid @RequestBody Endereco endereco) {
        if (!roleService.canAccessCliente(endereco.getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Endereco savedEndereco = enderecoRepository.save(endereco);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEndereco);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Endereco> updateEndereco(@PathVariable Long id, @Valid @RequestBody Endereco endereco) {
        Optional<Endereco> existingEndereco = enderecoRepository.findById(id);
        if (existingEndereco.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        if (!roleService.canAccessCliente(existingEndereco.get().getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        endereco.setId(id);
        Endereco updatedEndereco = enderecoRepository.save(endereco);
        return ResponseEntity.ok(updatedEndereco);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) {
        Optional<Endereco> endereco = enderecoRepository.findById(id);
        if (endereco.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        if (!roleService.canAccessCliente(endereco.get().getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        enderecoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}









