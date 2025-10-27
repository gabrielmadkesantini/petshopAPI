package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.model.Cliente;
import com.petshop.repository.ClienteRepository;
import com.petshop.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setDataCadastro(LocalDate.now());
        cliente.setFoto("foto.jpg");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllClientes_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findAll()).thenReturn(clientes);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].cpf").value("12345678901"));

        verify(clienteRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllClientes_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isForbidden());

        verify(clienteRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClienteById_WithValidIdAndPermission_ShouldReturnOk() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(roleService).canAccessCliente(1L);
        verify(clienteRepository).findById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClienteById_WithValidIdButNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(clienteRepository, never()).findById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClienteById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(roleService.canAccessCliente(999L)).thenReturn(true);
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clientes/999"))
                .andExpect(status().isNotFound());

        verify(roleService).canAccessCliente(999L);
        verify(clienteRepository).findById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClienteByCpf_WithValidCpfAndPermission_ShouldReturnOk() throws Exception {
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(get("/api/clientes/cpf/12345678901"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("12345678901"));

        verify(clienteRepository).findByCpf("12345678901");
        verify(roleService).canAccessCliente(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClienteByCpf_WithValidCpfButNoPermission_ShouldReturnForbidden() throws Exception {
        when(clienteRepository.findByCpf("12345678901")).thenReturn(Optional.of(cliente));
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/clientes/cpf/12345678901"))
                .andExpect(status().isForbidden());

        verify(clienteRepository).findByCpf("12345678901");
        verify(roleService).canAccessCliente(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClienteByCpf_WithInvalidCpf_ShouldReturnNotFound() throws Exception {
        when(clienteRepository.findByCpf("99999999999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clientes/cpf/99999999999"))
                .andExpect(status().isNotFound());

        verify(clienteRepository).findByCpf("99999999999");
        verify(roleService, never()).canAccessCliente(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getClientesByNome_WithAdminRole_ShouldReturnAllClientes() throws Exception {
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findByNomeContaining("João")).thenReturn(clientes);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/clientes/buscar")
                .param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(clienteRepository).findByNomeContaining("João");
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClientesByNome_WithUserRole_ShouldReturnFilteredClientes() throws Exception {
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findByNomeContaining("João")).thenReturn(clientes);
        when(roleService.isAdmin()).thenReturn(false);
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(get("/api/clientes/buscar")
                .param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(clienteRepository).findByNomeContaining("João");
        verify(roleService).isAdmin();
        verify(roleService).canAccessCliente(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClientesByNomeAndCpf_ShouldReturnOk() throws Exception {
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findByNomeAndCpf("João", "12345678901")).thenReturn(clientes);
        when(roleService.isAdmin()).thenReturn(false);
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(get("/api/clientes/buscar")
                .param("nome", "João")
                .param("cpf", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(clienteRepository).findByNomeAndCpf("João", "12345678901");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getClientesByDataRange_ShouldReturnOk() throws Exception {
        LocalDate dataInicio = LocalDate.now().minusDays(7);
        LocalDate dataFim = LocalDate.now();
        List<Cliente> clientes = Arrays.asList(cliente);
        when(clienteRepository.findByDataCadastroBetween(dataInicio, dataFim)).thenReturn(clientes);
        when(roleService.isAdmin()).thenReturn(false);
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(get("/api/clientes/buscar")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(clienteRepository).findByDataCadastroBetween(dataInicio, dataFim);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCliente_WithValidData_ShouldReturnCreated() throws Exception {
        when(clienteRepository.existsByCpf("12345678901")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(clienteRepository).existsByCpf("12345678901");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCliente_WithExistingCpf_ShouldReturnBadRequest() throws Exception {
        when(clienteRepository.existsByCpf("12345678901")).thenReturn(true);

        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("CPF já existe: 12345678901"));

        verify(clienteRepository).existsByCpf("12345678901");
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCliente_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isForbidden());

        verify(clienteRepository, never()).existsByCpf(any());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCliente_WithValidIdAndPermission_ShouldReturnOk() throws Exception {
        when(roleService.canModifyCliente(1L)).thenReturn(true);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(put("/api/clientes/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(roleService).canModifyCliente(1L);
        verify(clienteRepository).findById(1L);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCliente_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canModifyCliente(1L)).thenReturn(false);

        mockMvc.perform(put("/api/clientes/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isForbidden());

        verify(roleService).canModifyCliente(1L);
        verify(clienteRepository, never()).findById(any());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCliente_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(roleService.canModifyCliente(999L)).thenReturn(true);
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/clientes/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isNotFound());

        verify(roleService).canModifyCliente(999L);
        verify(clienteRepository).findById(999L);
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCliente_WithValidIdAndPermission_ShouldReturnNoContent() throws Exception {
        when(roleService.canDeleteCliente()).thenReturn(true);
        when(clienteRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/clientes/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(roleService).canDeleteCliente();
        verify(clienteRepository).existsById(1L);
        verify(clienteRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCliente_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canDeleteCliente()).thenReturn(false);

        mockMvc.perform(delete("/api/clientes/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(roleService).canDeleteCliente();
        verify(clienteRepository, never()).existsById(any());
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCliente_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(roleService.canDeleteCliente()).thenReturn(true);
        when(clienteRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/clientes/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(roleService).canDeleteCliente();
        verify(clienteRepository).existsById(999L);
        verify(clienteRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteCliente_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/clientes/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(roleService, never()).canDeleteCliente();
        verify(clienteRepository, never()).existsById(any());
        verify(clienteRepository, never()).deleteById(any());
    }
}

