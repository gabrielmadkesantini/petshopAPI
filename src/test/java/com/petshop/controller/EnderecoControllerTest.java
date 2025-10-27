package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.model.Endereco;
import com.petshop.model.Cliente;
import com.petshop.repository.EnderecoRepository;
import com.petshop.service.RoleService;
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

@WebMvcTest(EnderecoController.class)
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnderecoRepository enderecoRepository;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Endereco endereco;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");

        endereco = new Endereco();
        endereco.setId(1L);
        endereco.setCliente(cliente);
        endereco.setLogradouro("Rua das Flores");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setTag("Residencial");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllEnderecos_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Endereco> enderecos = Arrays.asList(endereco);
        when(enderecoRepository.findAll()).thenReturn(enderecos);

        mockMvc.perform(get("/api/enderecos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].logradouro").value("Rua das Flores"))
                .andExpect(jsonPath("$[0].cidade").value("São Paulo"));

        verify(enderecoRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllEnderecos_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/enderecos"))
                .andExpect(status().isForbidden());

        verify(enderecoRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEnderecoById_WithValidIdAndPermission_ShouldReturnOk() throws Exception {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(get("/api/enderecos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"));

        verify(enderecoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEnderecoById_WithValidIdButNoPermission_ShouldReturnForbidden() throws Exception {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/enderecos/1"))
                .andExpect(status().isForbidden());

        verify(enderecoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEnderecoById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/enderecos/999"))
                .andExpect(status().isNotFound());

        verify(enderecoRepository).findById(999L);
        verify(roleService, never()).canAccessCliente(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEnderecosByCliente_WithValidClienteAndPermission_ShouldReturnOk() throws Exception {
        List<Endereco> enderecos = Arrays.asList(endereco);
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(enderecoRepository.findByClienteId(1L)).thenReturn(enderecos);

        mockMvc.perform(get("/api/enderecos/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository).findByClienteId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEnderecosByCliente_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/enderecos/cliente/1"))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository, never()).findByClienteId(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEnderecos_WithClienteIdAndPermission_ShouldReturnOk() throws Exception {
        List<Endereco> enderecos = Arrays.asList(endereco);
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(enderecoRepository.findByAllFilters(1L, null, null, null, null)).thenReturn(enderecos);

        mockMvc.perform(get("/api/enderecos/buscar")
                .param("clienteId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository).findByAllFilters(1L, null, null, null, null);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getEnderecos_WithClienteIdButNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/enderecos/buscar")
                .param("clienteId", "1"))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository, never()).findByAllFilters(any(), any(), any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnderecos_WithCidadeOnly_ShouldReturnAllEnderecos() throws Exception {
        List<Endereco> enderecos = Arrays.asList(endereco);
        when(enderecoRepository.findByAllFilters(null, "São Paulo", null, null, null)).thenReturn(enderecos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/enderecos/buscar")
                .param("cidade", "São Paulo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(enderecoRepository).findByAllFilters(null, "São Paulo", null, null, null);
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnderecos_WithBairroOnly_ShouldReturnAllEnderecos() throws Exception {
        List<Endereco> enderecos = Arrays.asList(endereco);
        when(enderecoRepository.findByAllFilters(null, null, "Centro", null, null)).thenReturn(enderecos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/enderecos/buscar")
                .param("bairro", "Centro"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(enderecoRepository).findByAllFilters(null, null, "Centro", null, null);
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnderecos_WithLogradouroOnly_ShouldReturnAllEnderecos() throws Exception {
        List<Endereco> enderecos = Arrays.asList(endereco);
        when(enderecoRepository.findByAllFilters(null, null, null, "Rua das Flores", null)).thenReturn(enderecos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/enderecos/buscar")
                .param("logradouro", "Rua das Flores"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(enderecoRepository).findByAllFilters(null, null, null, "Rua das Flores", null);
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnderecos_WithTagOnly_ShouldReturnAllEnderecos() throws Exception {
        List<Endereco> enderecos = Arrays.asList(endereco);
        when(enderecoRepository.findByAllFilters(null, null, null, null, "Residencial")).thenReturn(enderecos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/enderecos/buscar")
                .param("tag", "Residencial"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(enderecoRepository).findByAllFilters(null, null, null, null, "Residencial");
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnderecos_WithMultipleFilters_ShouldReturnAllEnderecos() throws Exception {
        List<Endereco> enderecos = Arrays.asList(endereco);
        when(enderecoRepository.findByAllFilters(1L, "São Paulo", "Centro", "Rua das Flores", "Residencial")).thenReturn(enderecos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/enderecos/buscar")
                .param("clienteId", "1")
                .param("cidade", "São Paulo")
                .param("bairro", "Centro")
                .param("logradouro", "Rua das Flores")
                .param("tag", "Residencial"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(enderecoRepository).findByAllFilters(1L, "São Paulo", "Centro", "Rua das Flores", "Residencial");
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "USER")
    void createEndereco_WithValidDataAndPermission_ShouldReturnCreated() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        mockMvc.perform(post("/api/enderecos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endereco)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.logradouro").value("Rua das Flores"));

        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createEndereco_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(post("/api/enderecos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endereco)))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateEndereco_WithValidIdAndPermission_ShouldReturnOk() throws Exception {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(enderecoRepository.save(any(Endereco.class))).thenReturn(endereco);

        mockMvc.perform(put("/api/enderecos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endereco)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(enderecoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository).save(any(Endereco.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateEndereco_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/enderecos/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endereco)))
                .andExpect(status().isNotFound());

        verify(enderecoRepository).findById(999L);
        verify(roleService, never()).canAccessCliente(any());
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateEndereco_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(put("/api/enderecos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endereco)))
                .andExpect(status().isForbidden());

        verify(enderecoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteEndereco_WithValidIdAndPermission_ShouldReturnNoContent() throws Exception {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/enderecos/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(enderecoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteEndereco_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(enderecoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/enderecos/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(enderecoRepository).findById(999L);
        verify(roleService, never()).canAccessCliente(any());
        verify(enderecoRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteEndereco_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/enderecos/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(enderecoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
        verify(enderecoRepository, never()).deleteById(any());
    }
}

