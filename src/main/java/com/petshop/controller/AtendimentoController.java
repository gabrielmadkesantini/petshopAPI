package com.petshop.controller;

import com.petshop.dto.AtendimentoCreateRequest;
import com.petshop.model.Atendimento;
import com.petshop.model.Cliente;
import com.petshop.model.Pets;
import com.petshop.repository.AtendimentoRepository;
import com.petshop.repository.ClienteRepository;
import com.petshop.repository.PetsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/atendimentos")
@CrossOrigin(origins = "*")
public class AtendimentoController {
    
    @Autowired
    private AtendimentoRepository atendimentoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PetsRepository petsRepository;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Atendimento>> getAllAtendimentos() {
        List<Atendimento> atendimentos = atendimentoRepository.findAll();
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Atendimento> getAtendimentoById(@PathVariable Long id) {
        Optional<Atendimento> atendimento = atendimentoRepository.findByIdWithDetails(id);
        return atendimento.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/pet/{petId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Atendimento>> getAtendimentosByPet(@PathVariable Long petId) {
        List<Atendimento> atendimentos = atendimentoRepository.findByPetId(petId);
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Atendimento>> getAtendimentosByCliente(@PathVariable Long clienteId) {
        List<Atendimento> atendimentos = atendimentoRepository.findByClienteId(clienteId);
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/cliente/{clienteId}/pet/{petId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Atendimento>> getAtendimentosByClienteAndPet(@PathVariable Long clienteId, @PathVariable Long petId) {
        List<Atendimento> atendimentos = atendimentoRepository.findByClienteIdAndPetId(clienteId, petId);
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/data/{data}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Atendimento>> getAtendimentosByData(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<Atendimento> atendimentos = atendimentoRepository.findByData(data);
        return ResponseEntity.ok(atendimentos);
    }
    
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Atendimento>> getAtendimentos(@RequestParam(required = false) Long petId,
                                                             @RequestParam(required = false) Long clienteId,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                                                             @RequestParam(required = false) BigDecimal valorMin,
                                                             @RequestParam(required = false) BigDecimal valorMax,
                                                             @RequestParam(required = false) String descricao) {
        List<Atendimento> atendimentos;
        
        if (clienteId != null && petId != null && data != null) {
            atendimentos = atendimentoRepository.findByClienteIdAndPetIdAndData(clienteId, petId, data);
        }
        else if (clienteId != null && petId != null) {
            atendimentos = atendimentoRepository.findByClienteIdAndPetId(clienteId, petId);
        }
        else if (clienteId != null && data != null) {
            atendimentos = atendimentoRepository.findByClienteIdAndData(clienteId, data);
        }
        else if (clienteId != null && dataInicio != null && dataFim != null) {
            atendimentos = atendimentoRepository.findByClienteIdAndDataBetween(clienteId, dataInicio, dataFim);
        }
        else if (clienteId != null && valorMin != null && valorMax != null) {
            atendimentos = atendimentoRepository.findByClienteIdAndValorBetween(clienteId, valorMin, valorMax);
        }
        else if (clienteId != null && descricao != null) {
            atendimentos = atendimentoRepository.findByClienteIdAndDescricaoContaining(clienteId, descricao);
        }
        else if (petId != null && data != null) {
            atendimentos = atendimentoRepository.findByPetIdAndData(petId, data);
        }
        else if (petId != null && dataInicio != null && dataFim != null) {
            atendimentos = atendimentoRepository.findByPetIdAndDataBetween(petId, dataInicio, dataFim);
        }
        else if (dataInicio != null && dataFim != null) {
            atendimentos = atendimentoRepository.findByDataBetween(dataInicio, dataFim);
        }
        else if (valorMin != null && valorMax != null) {
            atendimentos = atendimentoRepository.findByValorBetween(valorMin, valorMax);
        }
        else if (clienteId != null) {
            atendimentos = atendimentoRepository.findByClienteId(clienteId);
        }
        else if (petId != null) {
            atendimentos = atendimentoRepository.findByPetId(petId);
        }
        else if (data != null) {
            atendimentos = atendimentoRepository.findByData(data);
        }
        else if (descricao != null) {
            atendimentos = atendimentoRepository.findByDescricaoContaining(descricao);
        }
        else {
            atendimentos = atendimentoRepository.findAll();
        }
        
        return ResponseEntity.ok(atendimentos);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = false)
    public ResponseEntity<Atendimento> createAtendimento(@Valid @RequestBody AtendimentoCreateRequest request) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(request.getCliente().getId());
        Optional<Pets> petOpt = petsRepository.findById(request.getPet().getId());
        
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Cliente não encontrado
        }
        
        if (petOpt.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Pet não encontrado
        }
        
        Atendimento atendimento = new Atendimento();
        atendimento.setPet(petOpt.get());
        atendimento.setCliente(clienteOpt.get());
        atendimento.setDescricao(request.getDescricao());
        atendimento.setValor(request.getValor());
        
        Atendimento savedAtendimento = atendimentoRepository.save(atendimento);
        
        Optional<Atendimento> atendimentoCompleto = atendimentoRepository.findByIdWithDetails(savedAtendimento.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(atendimentoCompleto.orElse(savedAtendimento));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = false)
    public ResponseEntity<Atendimento> updateAtendimento(@PathVariable Long id, @Valid @RequestBody Atendimento atendimento) {
        if (!atendimentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        atendimento.setId(id);
        Atendimento updatedAtendimento = atendimentoRepository.save(atendimento);
        Optional<Atendimento> atendimentoCompleto = atendimentoRepository.findByIdWithDetails(updatedAtendimento.getId());
        return ResponseEntity.ok(atendimentoCompleto.orElse(updatedAtendimento));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAtendimento(@PathVariable Long id) {
        if (!atendimentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        atendimentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}









