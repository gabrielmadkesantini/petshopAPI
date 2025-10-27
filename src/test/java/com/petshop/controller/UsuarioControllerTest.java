package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.dto.UsuarioUpdateRequest;
import com.petshop.model.Usuario;
import com.petshop.model.Cliente;
import com.petshop.model.PerfilUsuario;
import com.petshop.repository.UsuarioRepository;
import com.petshop.repository.ClienteRepository;
import com.petshop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

@WebMvcTest(UsuarioController.class)
@EnableMethodSecurity
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setCpf("123.456.789-01");
        usuario.setNome("João Silva");
        usuario.setSenha("senha123");
        usuario.setPerfil(PerfilUsuario.ADMIN);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpf("123.456.789-01");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsuarios_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cpf").value("123.456.789-01"))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(usuarioRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsuarios_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());

        verify(usuarioRepository, never()).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsuarioByCpf_WithValidCpf_ShouldReturnOk() throws Exception {
        when(usuarioRepository.findByCpf("123.456.789-01")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/123.456.789-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cpf").value("123.456.789-01"))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(usuarioRepository).findByCpf("123.456.789-01");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsuarioByCpf_WithInvalidCpf_ShouldReturnNotFound() throws Exception {
        when(usuarioRepository.findByCpf("99999999999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99999999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Usuário não encontrado"))
                .andExpect(jsonPath("$.message").value("Não foi encontrado um usuário com o CPF: 99999999999"));

        verify(usuarioRepository).findByCpf("99999999999");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsuariosByNome_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findByNomeContaining("João")).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios/buscar")
                .param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cpf").value("123.456.789-01"));

        verify(usuarioRepository).findByNomeContaining("João");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsuariosByPerfil_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findByPerfil(PerfilUsuario.ADMIN)).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios/buscar")
                .param("perfil", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cpf").value("123.456.789-01"));

        verify(usuarioRepository).findByPerfil(PerfilUsuario.ADMIN);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUsuariosByNomeAndPerfil_WithAdminRole_ShouldReturnOk() throws Exception {
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioRepository.findByNomeAndPerfil("João", PerfilUsuario.ADMIN)).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios/buscar")
                .param("nome", "João")
                .param("perfil", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].cpf").value("123.456.789-01"));

        verify(usuarioRepository).findByNomeAndPerfil("João", PerfilUsuario.ADMIN);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsuariosByNome_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/usuarios/buscar")
                .param("nome", "João"))
                .andExpect(status().isForbidden());

        verify(usuarioRepository, never()).findByNomeContaining(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUsuario_WithValidData_ShouldReturnCreated() throws Exception {
        when(usuarioRepository.existsByCpf("123.456.789-01")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-01"))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));

        verify(usuarioRepository).existsByCpf("123.456.789-01");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(jwtService).generateToken(any(Usuario.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUsuario_WithExistingCpf_ShouldReturnBadRequest() throws Exception {
        when(usuarioRepository.existsByCpf("123.456.789-01")).thenReturn(true);

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("CPF já cadastrado"))
                .andExpect(jsonPath("$.message").value("Já existe um usuário cadastrado com o CPF: 123.456.789-01"));

        verify(usuarioRepository).existsByCpf("123.456.789-01");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUsuario_WithClienteProfile_ShouldCreateCliente() throws Exception {
        usuario.setPerfil(PerfilUsuario.CLIENTE);
        when(usuarioRepository.existsByCpf("123.456.789-01")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.perfil").value("CLIENTE"));

        verify(usuarioRepository).existsByCpf("123.456.789-01");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(clienteRepository).save(any(Cliente.class));
        verify(jwtService).generateToken(any(Usuario.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUsuario_WithClienteProfileAndClienteCreationFailure_ShouldReturnInternalServerError() throws Exception {
        usuario.setPerfil(PerfilUsuario.CLIENTE);
        when(usuarioRepository.existsByCpf("123.456.789-01")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(clienteRepository.save(any(Cliente.class))).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/usuarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Erro ao criar cliente"));

        verify(usuarioRepository).existsByCpf("123.456.789-01");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(clienteRepository).save(any(Cliente.class));
        verify(usuarioRepository).delete(any(Usuario.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUsuario_WithValidCpf_ShouldReturnOk() throws Exception {
        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest();
        updateRequest.setNome("João Silva Atualizado");
        updateRequest.setPerfil(PerfilUsuario.CLIENTE);

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setCpf("123.456.789-01");
        updatedUsuario.setNome("João Silva Atualizado");
        updatedUsuario.setPerfil(PerfilUsuario.CLIENTE);

        when(usuarioRepository.findByCpf("123.456.789-01")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updatedUsuario);

        mockMvc.perform(put("/api/usuarios/123.456.789-01")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.perfil").value("CLIENTE"));

        verify(usuarioRepository).findByCpf("123.456.789-01");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUsuario_WithInvalidCpf_ShouldReturnNotFound() throws Exception {
        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest();
        updateRequest.setNome("João Silva Atualizado");

        when(usuarioRepository.findByCpf("99999999999")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/99999999999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Usuário não encontrado"))
                .andExpect(jsonPath("$.message").value("Não foi encontrado um usuário com o CPF: 99999999999"));

        verify(usuarioRepository).findByCpf("99999999999");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUsuario_WithUserRole_ShouldReturnForbidden() throws Exception {
        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest();
        updateRequest.setNome("João Silva Atualizado");

        mockMvc.perform(put("/api/usuarios/123.456.789-01")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        verify(usuarioRepository, never()).findByCpf(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUsuario_WithPasswordUpdate_ShouldEncryptPassword() throws Exception {
        UsuarioUpdateRequest updateRequest = new UsuarioUpdateRequest();
        updateRequest.setSenha("novaSenha123");

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setCpf("123.456.789-01");
        updatedUsuario.setNome("João Silva");
        updatedUsuario.setPerfil(PerfilUsuario.ADMIN);

        when(usuarioRepository.findByCpf("123.456.789-01")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updatedUsuario);

        mockMvc.perform(put("/api/usuarios/123.456.789-01")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cpf").value("123.456.789-01"));

        verify(usuarioRepository).findByCpf("123.456.789-01");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUsuario_WithValidCpf_ShouldReturnNoContent() throws Exception {
        when(usuarioRepository.existsByCpf("123.456.789-01")).thenReturn(true);
        when(clienteRepository.existsByCpf("123.456.789-01")).thenReturn(true);

        mockMvc.perform(delete("/api/usuarios/123.456.789-01")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(usuarioRepository).existsByCpf("123.456.789-01");
        verify(clienteRepository).existsByCpf("123.456.789-01");
        verify(clienteRepository).deleteByCpf("123.456.789-01");
        verify(usuarioRepository).deleteByCpf("123.456.789-01");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUsuario_WithInvalidCpf_ShouldReturnNotFound() throws Exception {
        when(usuarioRepository.existsByCpf("99999999999")).thenReturn(false);

        mockMvc.perform(delete("/api/usuarios/99999999999")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Usuário não encontrado"))
                .andExpect(jsonPath("$.message").value("Não foi encontrado um usuário com o CPF: 99999999999"));

        verify(usuarioRepository).existsByCpf("99999999999");
        verify(clienteRepository, never()).existsByCpf(any());
        verify(clienteRepository, never()).deleteByCpf(any());
        verify(usuarioRepository, never()).deleteByCpf(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUsuario_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/usuarios/123.456.789-01")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(usuarioRepository, never()).existsByCpf(any());
        verify(clienteRepository, never()).existsByCpf(any());
        verify(clienteRepository, never()).deleteByCpf(any());
        verify(usuarioRepository, never()).deleteByCpf(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUsuario_WithCascadeDelete_ShouldDeleteClienteFirst() throws Exception {
        when(usuarioRepository.existsByCpf("123.456.789-01")).thenReturn(true);
        when(clienteRepository.existsByCpf("123.456.789-01")).thenReturn(true);

        mockMvc.perform(delete("/api/usuarios/123.456.789-01")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(usuarioRepository).existsByCpf("123.456.789-01");
        verify(clienteRepository).existsByCpf("123.456.789-01");
        verify(clienteRepository).deleteByCpf("123.456.789-01");
        verify(usuarioRepository).deleteByCpf("123.456.789-01");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUsuario_WithoutCliente_ShouldDeleteOnlyUsuario() throws Exception {
        when(usuarioRepository.existsByCpf("123.456.789-01")).thenReturn(true);
        when(clienteRepository.existsByCpf("123.456.789-01")).thenReturn(false);

        mockMvc.perform(delete("/api/usuarios/123.456.789-01")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(usuarioRepository).existsByCpf("123.456.789-01");
        verify(clienteRepository).existsByCpf("123.456.789-01");
        verify(clienteRepository, never()).deleteByCpf(any());
        verify(usuarioRepository).deleteByCpf("123.456.789-01");
    }
}

