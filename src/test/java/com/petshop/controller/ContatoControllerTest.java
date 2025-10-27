package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.model.Contato;
import com.petshop.model.Cliente;
import com.petshop.repository.ContatoRepository;
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

@WebMvcTest(ContatoController.class)
class ContatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContatoRepository contatoRepository;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private Contato contato;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");

        contato = new Contato();
        contato.setId(1L);
        contato.setCliente(cliente);
        contato.setTipo(Contato.TipoContato.email);
        contato.setValor("joao@email.com");
        contato.setTag("Principal");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllContatos_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(contatoRepository.findAll()).thenReturn(contatos);

        mockMvc.perform(get("/api/contatos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].valor").value("joao@email.com"))
                .andExpect(jsonPath("$[0].tipo").value("email"));

        verify(contatoRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllContatos_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/contatos"))
                .andExpect(status().isForbidden());

        verify(contatoRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatoById_WithValidIdAndPermission_ShouldReturnOk() throws Exception {
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(get("/api/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value("joao@email.com"));

        verify(contatoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatoById_WithValidIdButNoPermission_ShouldReturnForbidden() throws Exception {
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/contatos/1"))
                .andExpect(status().isForbidden());

        verify(contatoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatoById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(contatoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/contatos/999"))
                .andExpect(status().isNotFound());

        verify(contatoRepository).findById(999L);
        verify(roleService, never()).canAccessCliente(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatosByCliente_WithValidClienteAndPermission_ShouldReturnOk() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(contatoRepository.findByClienteId(1L)).thenReturn(contatos);

        mockMvc.perform(get("/api/contatos/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository).findByClienteId(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatosByCliente_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/contatos/cliente/1"))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository, never()).findByClienteId(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getContatosByTipo_WithAdminRole_ShouldReturnAllContatos() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(contatoRepository.findByTipo(Contato.TipoContato.email)).thenReturn(contatos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/contatos/tipo/email"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(contatoRepository).findByTipo(Contato.TipoContato.email);
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatosByTipo_WithUserRole_ShouldReturnFilteredContatos() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(contatoRepository.findByTipo(Contato.TipoContato.email)).thenReturn(contatos);
        when(roleService.isAdmin()).thenReturn(false);
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(get("/api/contatos/tipo/email"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(contatoRepository).findByTipo(Contato.TipoContato.email);
        verify(roleService).isAdmin();
        verify(roleService).canAccessCliente(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatos_WithClienteIdAndTipo_ShouldReturnOk() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(contatoRepository.findByClienteIdAndTipo(1L, Contato.TipoContato.email)).thenReturn(contatos);

        mockMvc.perform(get("/api/contatos/buscar")
                .param("clienteId", "1")
                .param("tipo", "email"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository).findByClienteIdAndTipo(1L, Contato.TipoContato.email);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatos_WithClienteIdButNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(get("/api/contatos/buscar")
                .param("clienteId", "1"))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository, never()).findByClienteId(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getContatos_WithClienteIdAndValor_ShouldReturnOk() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(contatoRepository.findByClienteIdAndValorContaining(1L, "joao")).thenReturn(contatos);

        mockMvc.perform(get("/api/contatos/buscar")
                .param("clienteId", "1")
                .param("valor", "joao"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository).findByClienteIdAndValorContaining(1L, "joao");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getContatos_WithTipoOnly_ShouldReturnAllContatos() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(contatoRepository.findByTipo(Contato.TipoContato.email)).thenReturn(contatos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/contatos/buscar")
                .param("tipo", "email"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(contatoRepository).findByTipo(Contato.TipoContato.email);
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getContatos_WithValorOnly_ShouldReturnAllContatos() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(contatoRepository.findByValorContaining("joao")).thenReturn(contatos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/contatos/buscar")
                .param("valor", "joao"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(contatoRepository).findByValorContaining("joao");
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getContatos_WithTagOnly_ShouldReturnAllContatos() throws Exception {
        List<Contato> contatos = Arrays.asList(contato);
        when(contatoRepository.findByTagContaining("Principal")).thenReturn(contatos);
        when(roleService.isAdmin()).thenReturn(true);

        mockMvc.perform(get("/api/contatos/buscar")
                .param("tag", "Principal"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(contatoRepository).findByTagContaining("Principal");
        verify(roleService).isAdmin();
    }

    @Test
    @WithMockUser(roles = "USER")
    void createContato_WithValidDataAndPermission_ShouldReturnCreated() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(contatoRepository.save(any(Contato.class))).thenReturn(contato);

        mockMvc.perform(post("/api/contatos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valor").value("joao@email.com"));

        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository).save(any(Contato.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createContato_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(post("/api/contatos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isForbidden());

        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateContato_WithValidIdAndPermission_ShouldReturnOk() throws Exception {
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(roleService.canAccessCliente(1L)).thenReturn(true);
        when(contatoRepository.save(any(Contato.class))).thenReturn(contato);

        mockMvc.perform(put("/api/contatos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(contatoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository).save(any(Contato.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateContato_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(contatoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/contatos/999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isNotFound());

        verify(contatoRepository).findById(999L);
        verify(roleService, never()).canAccessCliente(any());
        verify(contatoRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateContato_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(put("/api/contatos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contato)))
                .andExpect(status().isForbidden());

        verify(contatoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteContato_WithValidIdAndPermission_ShouldReturnNoContent() throws Exception {
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(roleService.canAccessCliente(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/contatos/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(contatoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteContato_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(contatoRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/contatos/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(contatoRepository).findById(999L);
        verify(roleService, never()).canAccessCliente(any());
        verify(contatoRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteContato_WithNoPermission_ShouldReturnForbidden() throws Exception {
        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(roleService.canAccessCliente(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/contatos/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(contatoRepository).findById(1L);
        verify(roleService).canAccessCliente(1L);
        verify(contatoRepository, never()).deleteById(any());
    }
}

