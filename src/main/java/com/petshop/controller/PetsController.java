package com.petshop.controller;

import com.petshop.model.Cliente;
import com.petshop.model.Pets;
import com.petshop.model.Raca;
import com.petshop.repository.ClienteRepository;
import com.petshop.repository.PetsRepository;
import com.petshop.repository.RacaRepository;
import com.petshop.service.RoleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.hibernate.Hibernate;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetsController {
    
    private static final Logger logger = LoggerFactory.getLogger(PetsController.class);
    
    @Autowired
    private PetsRepository petsRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private RacaRepository racaRepository;
    
    @Autowired
    private RoleService roleService;
    
    private void initializeRelationships(Pets pet) {
        if (pet != null) {
            Hibernate.initialize(pet.getCliente());
            Hibernate.initialize(pet.getRaca());
        }
    }
    
    private void initializeRelationships(List<Pets> pets) {
        if (pets != null) {
            pets.forEach(this::initializeRelationships);
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Pets>> getAllPets() {
        List<Pets> pets = petsRepository.findAll();
        initializeRelationships(pets);
        return ResponseEntity.ok(pets);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<Pets> getPetById(@PathVariable Long id) {
        if (!roleService.canAccessPet(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Optional<Pets> pet = petsRepository.findById(id);
        if (pet.isPresent()) {
            initializeRelationships(pet.get());
            return ResponseEntity.ok(pet.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Pets>> getPetsByCliente(@PathVariable Long clienteId) {
        if (!roleService.canAccessCliente(clienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Pets> pets = petsRepository.findByClienteId(clienteId);
        initializeRelationships(pets);
        return ResponseEntity.ok(pets);
    }
    
    @GetMapping("/raca/{racaId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Pets>> getPetsByRaca(@PathVariable Long racaId) {
        List<Pets> pets = petsRepository.findByRacaId(racaId);
        
        if (!roleService.isAdmin()) {
            pets = pets.stream()
                    .filter(pet -> roleService.canAccessPet(pet.getId()))
                    .toList();
        }
        
        initializeRelationships(pets);
        return ResponseEntity.ok(pets);
    }
    
    @GetMapping("/buscar-avancado")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Pets>> getPets(@RequestParam(required = false) Long clienteId,
                                              @RequestParam(required = false) String nome,
                                              @RequestParam(required = false) Long racaId,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        if (clienteId != null && !roleService.canAccessCliente(clienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<Pets> pets;
        
        if (clienteId != null && nome != null) {
            pets = petsRepository.findByClienteIdAndNomeContaining(clienteId, nome);
        } else if (clienteId != null && racaId != null) {
            pets = petsRepository.findByClienteIdAndRacaId(clienteId, racaId);
        } else if (nome != null && racaId != null) {
            pets = petsRepository.findByNomeContainingAndRacaId(nome, racaId);
        } else if (clienteId != null) {
            pets = petsRepository.findByClienteId(clienteId);
        } else if (nome != null) {
            pets = petsRepository.findByNomeContaining(nome);
        } else if (racaId != null) {
            pets = petsRepository.findByRacaId(racaId);
        } else if (dataInicio != null && dataFim != null) {
            pets = petsRepository.findByDataNascimentoBetween(dataInicio, dataFim);
        } else {
            pets = petsRepository.findAll();
        }
        
        if (!roleService.isAdmin()) {
            pets = pets.stream()
                    .filter(pet -> roleService.canAccessPet(pet.getId()))
                    .toList();
        }
        
        initializeRelationships(pets);
        return ResponseEntity.ok(pets);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Pets> createPet(@Valid @RequestBody Pets request) {
        try {
            logger.info("Tentando criar pet para cliente ID: {}", request.getCliente().getId());
            
            if (!roleService.canModifyPet(null, request.getCliente().getId())) {
                logger.warn("Usuário não tem permissão para criar pet para cliente ID: {}", request.getCliente().getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Optional<Cliente> clienteOpt = clienteRepository.findById(request.getCliente().getId());
            if (clienteOpt.isEmpty()) {
                logger.error("Cliente não encontrado com ID: {}", request.getCliente().getId());
                return ResponseEntity.badRequest().build();
            }
            
            Raca raca = null;
                if (request.getRaca().getId() != null) {
                Optional<Raca> racaOpt = racaRepository.findById(request.getRaca().getId());
                if (racaOpt.isEmpty()) {
                    logger.error("Raça não encontrada com ID: {}", request.getRaca().getId());
                    return ResponseEntity.badRequest().build(); 
                }
                raca = racaOpt.get();
            }
            
            Pets pet = new Pets();
            pet.setCliente(clienteOpt.get());
            pet.setRaca(raca);
            pet.setDataNascimento(request.getDataNascimento());
            pet.setNome(request.getNome());
            pet.setFoto(request.getFoto());
            
            Pets savedPet = petsRepository.save(pet);
            logger.info("Pet criado com sucesso - ID: {}, Nome: {}", savedPet.getId(), savedPet.getNome());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPet);
            
        } catch (Exception e) {
            logger.error("Erro ao criar pet: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Pets> updatePet(@PathVariable Long id, @Valid @RequestBody Pets pet) {
        try {
            logger.info("Tentando atualizar pet ID: {}", id);
            
            if (!roleService.canModifyPet(id, null)) {
                logger.warn("Usuário não tem permissão para modificar pet ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            if (!petsRepository.existsById(id)) {
                logger.error("Pet não encontrado com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            pet.setId(id);
            Pets updatedPet = petsRepository.save(pet);
            logger.info("Pet atualizado com sucesso - ID: {}, Nome: {}", updatedPet.getId(), updatedPet.getNome());
            return ResponseEntity.ok(updatedPet);
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar pet ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        try {
            logger.info("Tentando deletar pet ID: {}", id);
            
            if (!roleService.canDeletePet(id)) {
                logger.warn("Usuário não tem permissão para deletar pet ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            if (!petsRepository.existsById(id)) {
                logger.error("Pet não encontrado com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            petsRepository.deleteById(id);
            logger.info("Pet deletado com sucesso - ID: {}", id);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Erro ao deletar pet ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}








