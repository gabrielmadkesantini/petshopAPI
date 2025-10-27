package com.petshop.controller;

import com.petshop.model.Cliente;
import com.petshop.repository.ClienteRepository;
import com.petshop.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return ResponseEntity.ok(clientes);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        if (!roleService.canAccessCliente(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Cliente> getClienteByCpf(@PathVariable String cpf) {
        Optional<Cliente> cliente = clienteRepository.findByCpf(cpf);
        if (cliente.isPresent()) {
            if (!roleService.canAccessCliente(cliente.get().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Cliente>> getClientesByNome(@RequestParam(required = false) String nome,
                                                           @RequestParam(required = false) String cpf,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<Cliente> clientes;
        
        if (nome != null && cpf != null) {
            clientes = clienteRepository.findByNomeAndCpf(nome, cpf);
        } else if (nome != null) {
            clientes = clienteRepository.findByNomeContaining(nome);
        } else if (dataInicio != null && dataFim != null) {
            clientes = clienteRepository.findByDataCadastroBetween(dataInicio, dataFim);
        } else {
            clientes = clienteRepository.findAll();
        }
        
        if (!roleService.isAdmin()) {
            clientes = clientes.stream()
                    .filter(cliente -> roleService.canAccessCliente(cliente.getId()))
                    .toList();
        }
        
        return ResponseEntity.ok(clientes);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCliente(@Valid @RequestBody Cliente cliente) {
        if (cliente.getCpf() != null && clienteRepository.existsByCpf(cliente.getCpf())) {
            return ResponseEntity.badRequest().body("CPF já existe: " + cliente.getCpf());
        }
        
        try {
            Cliente savedCliente = clienteRepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar cliente: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        if (!roleService.canModifyCliente(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Optional<Cliente> existingClienteOpt = clienteRepository.findById(id);
        if (!existingClienteOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Cliente existingCliente = existingClienteOpt.get();
        
        LocalDate originalDataCadastro = existingCliente.getDataCadastro();
        
        existingCliente.setNome(cliente.getNome());
        existingCliente.setCpf(cliente.getCpf());
        existingCliente.setFoto(cliente.getFoto());
        existingCliente.setDataCadastro(originalDataCadastro);
        
        Cliente updatedCliente = clienteRepository.save(existingCliente);
        return ResponseEntity.ok(updatedCliente);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        if (!roleService.canDeleteCliente()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}









