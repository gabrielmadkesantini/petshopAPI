package com.petshop.controller;

import com.petshop.model.Raca;
import com.petshop.repository.RacaRepository;
import com.petshop.repository.PetsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/racas")
@CrossOrigin(origins = "*")
public class RacaController {
    
    @Autowired
    private RacaRepository racaRepository;
    
    @Autowired
    private PetsRepository petsRepository;
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Raca>> getAllRacas() {
        List<Raca> racas = racaRepository.findAll();
        return ResponseEntity.ok(racas);
    }
    
    @GetMapping("/{id}")    
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Raca> getRacaById(@PathVariable Long id) {
        Optional<Raca> raca = racaRepository.findById(id);
        return raca.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Raca>> getRacas(@RequestParam(required = false) String descricao,
                                               @RequestParam(required = false) Boolean caseInsensitive) {
        List<Raca> racas;
        
        if (descricao != null) {
            if (caseInsensitive != null && caseInsensitive) {
                racas = racaRepository.findByDescricaoContainingIgnoreCase(descricao);
            } else {
                racas = racaRepository.findByDescricaoContaining(descricao);
            }
        } else {
            racas = racaRepository.findAll();
        }
        
        return ResponseEntity.ok(racas);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Raca> createRaca(@Valid @RequestBody Raca raca) {
        Optional<Raca> existingRaca = racaRepository.findByDescricao(raca.getDescricao());
        if (existingRaca.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(existingRaca.get());
        }
        Raca savedRaca = racaRepository.save(raca);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRaca);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Raca> updateRaca(@PathVariable Long id, @Valid @RequestBody Raca raca) {
        if (!racaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        raca.setId(id);
        Raca updatedRaca = racaRepository.save(raca);
        return ResponseEntity.ok(updatedRaca);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteRaca(@PathVariable Long id) {
        if (!racaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        petsRepository.deleteByRacaId(id);
        
        racaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}









