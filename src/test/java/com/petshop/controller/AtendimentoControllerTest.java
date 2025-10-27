package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.dto.AtendimentoCreateRequest;
import com.petshop.model.Atendimento;
import com.petshop.model.Pets;
import com.petshop.model.Cliente;
import com.petshop.repository.AtendimentoRepository;
import com.petshop.repository.ClienteRepository;
import com.petshop.repository.PetsRepository;
import com.petshop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtendimentoController.class)
class AtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtendimentoRepository atendimentoRepository;
    
    @MockBean
    private ClienteRepository clienteRepository;
    
    @MockBean
    private PetsRepository petsRepository;
    
    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Atendimento atendimento;
    private Pets pet;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");

        pet = new Pets();
        pet.setId(1L);
        pet.setNome("Rex");
        pet.setCliente(cliente);

        atendimento = new Atendimento();
        atendimento.setId(1L);
        atendimento.setPet(pet);
        atendimento.setData(LocalDate.now());
        atendimento.setDescricao("Consulta veterinária");
        atendimento.setValor(new BigDecimal("100.00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAtendimentos_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Atendimento> atendimentos = Arrays.asList(atendimento);
        when(atendimentoRepository.findAll()).thenReturn(atendimentos);

        mockMvc.perform(get("/api/atendimentos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Consulta veterinária"));

        verify(atendimentoRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllAtendimentos_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/atendimentos"))
                .andExpect(status().isForbidden());

        verify(atendimentoRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAtendimentoById_WithValidId_ShouldReturnOk() throws Exception {
        when(atendimentoRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(atendimento));

        mockMvc.perform(get("/api/atendimentos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Consulta veterinária"));

        verify(atendimentoRepository).findByIdWithDetails(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAtendimentoById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(atendimentoRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/atendimentos/999"))
                .andExpect(status().isNotFound());

        verify(atendimentoRepository).findByIdWithDetails(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAtendimentosByPet_ShouldReturnOk() throws Exception {
        List<Atendimento> atendimentos = Arrays.asList(atendimento);
        when(atendimentoRepository.findByPetId(1L)).thenReturn(atendimentos);

        mockMvc.perform(get("/api/atendimentos/pet/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(atendimentoRepository).findByPetId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAtendimentosByData_ShouldReturnOk() throws Exception {
        LocalDate data = LocalDate.now();
        List<Atendimento> atendimentos = Arrays.asList(atendimento);
        when(atendimentoRepository.findByData(data)).thenReturn(atendimentos);

        mockMvc.perform(get("/api/atendimentos/data/{data}", data))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(atendimentoRepository).findByData(data);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAtendimentos_WithPetIdAndData_ShouldReturnOk() throws Exception {
        LocalDate data = LocalDate.now();
        List<Atendimento> atendimentos = Arrays.asList(atendimento);
        when(atendimentoRepository.findByPetIdAndData(1L, data)).thenReturn(atendimentos);

        mockMvc.perform(get("/api/atendimentos/buscar")
                .param("petId", "1")
                .param("data", data.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(atendimentoRepository).findByPetIdAndData(1L, data);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAtendimentos_WithDataRange_ShouldReturnOk() throws Exception {
        LocalDate dataInicio = LocalDate.now().minusDays(7);
        LocalDate dataFim = LocalDate.now();
        List<Atendimento> atendimentos = Arrays.asList(atendimento);
        when(atendimentoRepository.findByDataBetween(dataInicio, dataFim)).thenReturn(atendimentos);

        mockMvc.perform(get("/api/atendimentos/buscar")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(atendimentoRepository).findByDataBetween(dataInicio, dataFim);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAtendimentos_WithValorRange_ShouldReturnOk() throws Exception {
        BigDecimal valorMin = new BigDecimal("50.00");
        BigDecimal valorMax = new BigDecimal("200.00");
        List<Atendimento> atendimentos = Arrays.asList(atendimento);
        when(atendimentoRepository.findByValorBetween(valorMin, valorMax)).thenReturn(atendimentos);

        mockMvc.perform(get("/api/atendimentos/buscar")
                .param("valorMin", "50.00")
                .param("valorMax", "200.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(atendimentoRepository).findByValorBetween(valorMin, valorMax);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAtendimentos_WithDescricao_ShouldReturnOk() throws Exception {
        String descricao = "consulta";
        List<Atendimento> atendimentos = Arrays.asList(atendimento);
        when(atendimentoRepository.findByDescricaoContaining(descricao)).thenReturn(atendimentos);

        mockMvc.perform(get("/api/atendimentos/buscar")
                .param("descricao", descricao))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(atendimentoRepository).findByDescricaoContaining(descricao);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAtendimento_WithValidData_ShouldReturnCreated() throws Exception {
        AtendimentoCreateRequest request = new AtendimentoCreateRequest();
        request.setPet(new AtendimentoCreateRequest.PetReference(1L));
        request.setCliente(new AtendimentoCreateRequest.ClienteReference(1L));
        request.setDescricao("Consulta veterinária");
        request.setValor(new BigDecimal("100.00"));
        
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(petsRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(atendimentoRepository.save(any(Atendimento.class))).thenReturn(atendimento);
        when(atendimentoRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(atendimento));

        mockMvc.perform(post("/api/atendimentos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Consulta veterinária"));

        verify(clienteRepository).findById(1L);
        verify(petsRepository).findById(1L);
        verify(atendimentoRepository).save(any(Atendimento.class));
        verify(atendimentoRepository).findByIdWithDetails(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createAtendimento_WithUserRole_ShouldReturnForbidden() throws Exception {
        AtendimentoCreateRequest request = new AtendimentoCreateRequest();
        request.setPet(new AtendimentoCreateRequest.PetReference(1L));
        request.setCliente(new AtendimentoCreateRequest.ClienteReference(1L));
        request.setDescricao("Consulta veterinária");
        request.setValor(new BigDecimal("100.00"));
        
        mockMvc.perform(post("/api/atendimentos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(atendimentoRepository, never()).save(any(Atendimento.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAtendimento_WithValidId_ShouldReturnOk() throws Exception {
        when(atendimentoRepository.existsById(1L)).thenReturn(true);
        when(atendimentoRepository.save(any(Atendimento.class))).thenReturn(atendimento);

        mockMvc.perform(put("/api/atendimentos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atendimento)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(atendimentoRepository).existsById(1L);
        verify(atendimentoRepository).save(any(Atendimento.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAtendimento_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(atendimentoRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(put("/api/atendimentos/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(atendimento)))
                .andExpect(status().isNotFound());

        verify(atendimentoRepository).existsById(999L);
        verify(atendimentoRepository, never()).save(any(Atendimento.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAtendimento_WithValidId_ShouldReturnNoContent() throws Exception {
        when(atendimentoRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/atendimentos/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(atendimentoRepository).existsById(1L);
        verify(atendimentoRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAtendimento_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(atendimentoRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/atendimentos/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(atendimentoRepository).existsById(999L);
        verify(atendimentoRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteAtendimento_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/atendimentos/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(atendimentoRepository, never()).existsById(any());
        verify(atendimentoRepository, never()).deleteById(any());
    }
}

