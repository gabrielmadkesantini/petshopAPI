package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.model.Cliente;
import com.petshop.model.Pets;
import com.petshop.model.Raca;
import com.petshop.repository.ClienteRepository;
import com.petshop.repository.PetsRepository;
import com.petshop.repository.RacaRepository;
import com.petshop.service.RoleService;
import com.petshop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PetsController.class)
@EnableMethodSecurity(prePostEnabled = true)
class PetsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetsRepository petsRepository;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private RacaRepository racaRepository;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Pets pet;
    private Cliente cliente;
    private Raca raca;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");

        raca = new Raca();
        raca.setId(1L);
        raca.setDescricao("Golden Retriever");

        pet = new Pets();
        pet.setId(1L);
        pet.setNome("Rex");
        pet.setDataNascimento(LocalDate.of(2020, 5, 15));
        pet.setCliente(cliente);
        pet.setRaca(raca);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllPets_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Pets> pets = Arrays.asList(pet);
        when(petsRepository.findAll()).thenReturn(pets);

        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Rex"));

        verify(petsRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllPets_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isForbidden());

        verify(petsRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPetById_WithValidIdAndPermission_ShouldReturnOk() throws Exception {
        when(roleService.canAccessPet(1L)).thenReturn(true);
        when(petsRepository.findById(1L)).thenReturn(Optional.of(pet));

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Rex"));

        verify(roleService).canAccessPet(1L);
        verify(petsRepository).findById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPetById_WithValidIdButNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessPet(1L)).thenReturn(false);

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessPet(1L);
        verify(petsRepository, never()).findById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPetById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(roleService.canAccessPet(999L)).thenReturn(true);
        when(petsRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pets/999"))
                .andExpect(status().isNotFound());

        verify(roleService).canAccessPet(999L);
        verify(petsRepository).findById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPetsByCliente_WithValidClienteAndPermission_ShouldReturnOk() throws Exception {
        List<Pets> pets = Arrays.asList(pet);
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(petsRepository.findByClienteId(1L)).thenReturn(pets);

        mockMvc.perform(get("/api/pets/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(roleService).canAccessCliente(1L);
        verify(petsRepository).findByClienteId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPetsByCliente_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/pets/cliente/1"))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(petsRepository, never()).findByClienteId(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPetsByRaca_WithAdminRole_ShouldReturnAllPets() throws Exception {
        List<Pets> pets = Arrays.asList(pet);
        when(roleService.isAdmin()).thenReturn(true);
        when(petsRepository.findByRacaId(1L)).thenReturn(pets);

        mockMvc.perform(get("/api/pets/raca/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(roleService).isAdmin();
        verify(petsRepository).findByRacaId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPetsByRaca_WithUserRole_ShouldReturnFilteredPets() throws Exception {
        List<Pets> pets = Arrays.asList(pet);
        when(roleService.isAdmin()).thenReturn(false);
        when(petsRepository.findByRacaId(1L)).thenReturn(pets);
        when(roleService.canAccessPet(1L)).thenReturn(true);

        mockMvc.perform(get("/api/pets/raca/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(roleService).isAdmin();
        verify(petsRepository).findByRacaId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPets_WithClienteIdAndPermission_ShouldReturnOk() throws Exception {
        List<Pets> pets = Arrays.asList(pet);
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(petsRepository.findByClienteId(1L)).thenReturn(pets);
        when(roleService.isAdmin()).thenReturn(false);
        when(roleService.canAccessPet(1L)).thenReturn(true);

        mockMvc.perform(get("/api/pets/buscar-avancado")
                .param("clienteId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(roleService).canAccessCliente(1L);
        verify(petsRepository).findByClienteId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPets_WithClienteIdButNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/pets/buscar-avancado")
                .param("clienteId", "1"))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(petsRepository, never()).findByClienteId(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPet_WithValidDataAndPermission_ShouldReturnCreated() throws Exception {
        when(roleService.canModifyPet(null, 1L)).thenReturn(true);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(racaRepository.findById(1L)).thenReturn(Optional.of(raca));
        when(petsRepository.save(any(Pets.class))).thenReturn(pet);

        mockMvc.perform(post("/api/pets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Rex"));

        verify(roleService).canModifyPet(null, 1L);
        verify(clienteRepository).findById(1L);
        verify(petsRepository).save(any(Pets.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPet_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canModifyPet(null, 1L)).thenReturn(false);

        mockMvc.perform(post("/api/pets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isForbidden());

        verify(roleService).canModifyPet(null, 1L);
        verify(clienteRepository, never()).findById(any());
        verify(petsRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPet_WithInvalidClienteId_ShouldReturnBadRequest() throws Exception {
        when(roleService.canModifyPet(null, 999L)).thenReturn(true);
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        pet.getCliente().setId(999L);

        mockMvc.perform(post("/api/pets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest());

        verify(roleService).canModifyPet(null, 999L);
        verify(clienteRepository).findById(999L);
        verify(petsRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updatePet_WithValidIdAndPermission_ShouldReturnOk() throws Exception {
        when(roleService.canModifyPet(1L, null)).thenReturn(true);
        when(petsRepository.existsById(1L)).thenReturn(true);
        when(petsRepository.save(any(Pets.class))).thenReturn(pet);

        mockMvc.perform(put("/api/pets/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(roleService).canModifyPet(1L, null);
        verify(petsRepository).existsById(1L);
        verify(petsRepository).save(any(Pets.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updatePet_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canModifyPet(1L, null)).thenReturn(false);

        mockMvc.perform(put("/api/pets/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isForbidden());

        verify(roleService).canModifyPet(1L, null);
        verify(petsRepository, never()).existsById(any());
        verify(petsRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updatePet_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(roleService.canModifyPet(999L, null)).thenReturn(true);
        when(petsRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(put("/api/pets/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isNotFound());

        verify(roleService).canModifyPet(999L, null);
        verify(petsRepository).existsById(999L);
        verify(petsRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deletePet_WithValidIdAndPermission_ShouldReturnNoContent() throws Exception {
        when(roleService.canDeletePet(1L)).thenReturn(true);
        when(petsRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/pets/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(roleService).canDeletePet(1L);
        verify(petsRepository).existsById(1L);
        verify(petsRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deletePet_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canDeletePet(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/pets/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(roleService).canDeletePet(1L);
        verify(petsRepository, never()).existsById(any());
        verify(petsRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deletePet_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(roleService.canDeletePet(999L)).thenReturn(true);
        when(petsRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/pets/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(roleService).canDeletePet(999L);
        verify(petsRepository).existsById(999L);
        verify(petsRepository, never()).deleteById(any());
    }
}

