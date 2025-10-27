package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.model.Raca;
import com.petshop.repository.RacaRepository;
import com.petshop.repository.PetsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RacaController.class)
class RacaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RacaRepository racaRepository;

    @MockBean
    private PetsRepository petsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Raca raca;

    @BeforeEach
    void setUp() {
        raca = new Raca();
        raca.setId(1L);
        raca.setDescricao("Golden Retriever");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRacas_WithUserRole_ShouldReturnOk() throws Exception {
        List<Raca> racas = Arrays.asList(raca);
        when(racaRepository.findAll()).thenReturn(racas);

        mockMvc.perform(get("/api/racas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Golden Retriever"));

        verify(racaRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRacas_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Raca> racas = Arrays.asList(raca);
        when(racaRepository.findAll()).thenReturn(racas);

        mockMvc.perform(get("/api/racas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Golden Retriever"));

        verify(racaRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRacaById_WithValidId_ShouldReturnOk() throws Exception {
        when(racaRepository.findById(1L)).thenReturn(Optional.of(raca));

        mockMvc.perform(get("/api/racas/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Golden Retriever"));

        verify(racaRepository).findById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRacaById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(racaRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/racas/999"))
                .andExpect(status().isNotFound());

        verify(racaRepository).findById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRacas_WithDescricao_ShouldReturnOk() throws Exception {
        List<Raca> racas = Arrays.asList(raca);
        when(racaRepository.findByDescricaoContaining("Golden")).thenReturn(racas);

        mockMvc.perform(get("/api/racas/buscar")
                .param("descricao", "Golden"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(racaRepository).findByDescricaoContaining("Golden");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRacas_WithDescricaoCaseInsensitive_ShouldReturnOk() throws Exception {
        List<Raca> racas = Arrays.asList(raca);
        when(racaRepository.findByDescricaoContainingIgnoreCase("golden")).thenReturn(racas);

        mockMvc.perform(get("/api/racas/buscar")
                .param("descricao", "golden")
                .param("caseInsensitive", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(racaRepository).findByDescricaoContainingIgnoreCase("golden");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRacas_WithoutFilters_ShouldReturnAllRacas() throws Exception {
        List<Raca> racas = Arrays.asList(raca);
        when(racaRepository.findAll()).thenReturn(racas);

        mockMvc.perform(get("/api/racas/buscar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(racaRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRaca_WithValidData_ShouldReturnCreated() throws Exception {
        when(racaRepository.findByDescricao("Golden Retriever")).thenReturn(Optional.empty());
        when(racaRepository.save(any(Raca.class))).thenReturn(raca);

        mockMvc.perform(post("/api/racas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(raca)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Golden Retriever"));

        verify(racaRepository).findByDescricao("Golden Retriever");
        verify(racaRepository).save(any(Raca.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRaca_WithExistingDescricao_ShouldReturnBadRequest() throws Exception {
        when(racaRepository.findByDescricao("Golden Retriever")).thenReturn(Optional.of(raca));

        mockMvc.perform(post("/api/racas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(raca)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(racaRepository).findByDescricao("Golden Retriever");
        verify(racaRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRaca_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/racas")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(raca)))
                .andExpect(status().isForbidden());

        verify(racaRepository, never()).findByDescricao(any());
        verify(racaRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRaca_WithValidId_ShouldReturnOk() throws Exception {
        when(racaRepository.existsById(1L)).thenReturn(true);
        when(racaRepository.save(any(Raca.class))).thenReturn(raca);

        mockMvc.perform(put("/api/racas/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(raca)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(racaRepository).existsById(1L);
        verify(racaRepository).save(any(Raca.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRaca_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(racaRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(put("/api/racas/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(raca)))
                .andExpect(status().isNotFound());

        verify(racaRepository).existsById(999L);
        verify(racaRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateRaca_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/racas/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(raca)))
                .andExpect(status().isForbidden());

        verify(racaRepository, never()).existsById(any());
        verify(racaRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRaca_WithValidId_ShouldReturnNoContent() throws Exception {
        when(racaRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/racas/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(racaRepository).existsById(1L);
        verify(petsRepository).deleteByRacaId(1L);
        verify(racaRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRaca_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(racaRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/racas/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(racaRepository).existsById(999L);
        verify(petsRepository, never()).deleteByRacaId(any());
        verify(racaRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteRaca_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/racas/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(racaRepository, never()).existsById(any());
        verify(petsRepository, never()).deleteByRacaId(any());
        verify(racaRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRaca_WithCascadeDelete_ShouldDeletePetsFirst() throws Exception {
        when(racaRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/racas/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(racaRepository).existsById(1L);
        verify(petsRepository).deleteByRacaId(1L);
        verify(racaRepository).deleteById(1L);
    }
}

