package com.petshop.controller;

import com.petshop.model.Contato;
import com.petshop.repository.ContatoRepository;
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
@RequestMapping("/api/contatos")
@CrossOrigin(origins = "*")
public class ContatoController {
    
    @Autowired
    private ContatoRepository contatoRepository;
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Contato>> getAllContatos() {
        List<Contato> contatos = contatoRepository.findAll();
        return ResponseEntity.ok(contatos);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Contato> getContatoById(@PathVariable Long id) {
        Optional<Contato> contato = contatoRepository.findById(id);
        if (contato.isPresent()) {
            if (!roleService.canAccessCliente(contato.get().getCliente().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(contato.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Contato>> getContatosByCliente(@PathVariable Long clienteId) {
        if (!roleService.canAccessCliente(clienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Contato> contatos = contatoRepository.findByClienteId(clienteId);
        return ResponseEntity.ok(contatos);
    }
    
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Contato>> getContatosByTipo(@PathVariable String tipo) {
        List<Contato> contatos = contatoRepository.findByTipo(Contato.TipoContato.valueOf(tipo));
        
        if (!roleService.isAdmin()) {
            contatos = contatos.stream()
                    .filter(contato -> roleService.canAccessCliente(contato.getCliente().getId()))
                    .toList();
        }
        
        return ResponseEntity.ok(contatos);
    }
    
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Contato>> getContatos(@RequestParam(required = false) Long clienteId,
                                                     @RequestParam(required = false) String tipo,
                                                     @RequestParam(required = false) String valor,
                                                     @RequestParam(required = false) String tag) {
        if (clienteId != null && !roleService.canAccessCliente(clienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Contato> contatos;
        
        if (clienteId != null && tipo != null) {
            contatos = contatoRepository.findByClienteIdAndTipo(clienteId, Contato.TipoContato.valueOf(tipo));
        } else if (clienteId != null && valor != null) {
            contatos = contatoRepository.findByClienteIdAndValorContaining(clienteId, valor);
        } else if (clienteId != null) {
            contatos = contatoRepository.findByClienteId(clienteId);
        } else if (tipo != null) {
            contatos = contatoRepository.findByTipo(Contato.TipoContato.valueOf(tipo));
        } else if (valor != null) {
            contatos = contatoRepository.findByValorContaining(valor);
        } else if (tag != null) {
            contatos = contatoRepository.findByTagContaining(tag);
        } else {
            contatos = contatoRepository.findAll();
        }
        
        if (!roleService.isAdmin()) {
            contatos = contatos.stream()
                    .filter(contato -> roleService.canAccessCliente(contato.getCliente().getId()))
                    .toList();
        }
        
        return ResponseEntity.ok(contatos);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Contato> createContato(@Valid @RequestBody Contato contato) {
        if (!roleService.canAccessCliente(contato.getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Contato savedContato = contatoRepository.save(contato);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContato);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Contato> updateContato(@PathVariable Long id, @Valid @RequestBody Contato contato) {
        Optional<Contato> existingContato = contatoRepository.findById(id);
        if (existingContato.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        if (!roleService.canAccessCliente(existingContato.get().getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        contato.setId(id);
        Contato updatedContato = contatoRepository.save(contato);
        return ResponseEntity.ok(updatedContato);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContato(@PathVariable Long id) {
        Optional<Contato> contato = contatoRepository.findById(id);
        if (contato.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        if (!roleService.canAccessCliente(contato.get().getCliente().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        contatoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}









