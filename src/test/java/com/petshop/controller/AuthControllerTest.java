package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.dto.LoginRequest;
import com.petshop.model.PerfilUsuario;
import com.petshop.model.Usuario;
import com.petshop.repository.UsuarioRepository;
import com.petshop.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;
    private LoginRequest loginRequest;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        
        usuario = new Usuario();
        usuario.setCpf("12345678901");
        usuario.setNome("João Silva");
        usuario.setSenha(passwordEncoder.encode("senha123"));
        usuario.setPerfil(PerfilUsuario.ADMIN);

        loginRequest = new LoginRequest();
        loginRequest.setCpf("12345678901");
        loginRequest.setSenha("senha123");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));

        verify(usuarioRepository).findByCpf("12345678901");
        verify(jwtService).generateToken(any(Usuario.class));
    }

    @Test
    void login_WithInvalidCpf_ShouldReturnNotFound() throws Exception {
        when(usuarioRepository.findByCpf("99999999999")).thenReturn(Optional.empty());

        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setCpf("99999999999");
        invalidRequest.setSenha("senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Usuário não encontrado"))
                .andExpect(jsonPath("$.message").value("Não foi encontrado um usuário com o CPF: 99999999999"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));

        verify(usuarioRepository).findByCpf("99999999999");
        verify(jwtService, never()).generateToken(any(Usuario.class));
    }

    @Test
    void login_WithInvalidPassword_ShouldReturnBadRequest() throws Exception {
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));

        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setCpf("12345678901");
        invalidRequest.setSenha("senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Senha incorreta"))
                .andExpect(jsonPath("$.message").value("A senha fornecida está incorreta"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));

        verify(usuarioRepository).findByCpf("12345678901");
        verify(jwtService, never()).generateToken(any(Usuario.class));
    }

    @Test
    void login_WithEmptyCpf_ShouldReturnNotFound() throws Exception {
        when(usuarioRepository.findByCpf("")).thenReturn(Optional.empty());

        LoginRequest emptyRequest = new LoginRequest();
        emptyRequest.setCpf("");
        emptyRequest.setSenha("senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Usuário não encontrado"));

        verify(usuarioRepository).findByCpf("");
        verify(jwtService, never()).generateToken(any(Usuario.class));
    }

    @Test
    void login_WithNullCpf_ShouldReturnNotFound() throws Exception {
        when(usuarioRepository.findByCpf(null)).thenReturn(Optional.empty());

        LoginRequest nullRequest = new LoginRequest();
        nullRequest.setCpf(null);
        nullRequest.setSenha("senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Usuário não encontrado"));

        verify(usuarioRepository).findByCpf(null);
        verify(jwtService, never()).generateToken(any(Usuario.class));
    }

    @Test
    void login_WithJwtServiceException_ShouldReturnInternalServerError() throws Exception {
        when(usuarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any(Usuario.class))).thenThrow(new RuntimeException("JWT Error"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.message").value("Ocorreu um erro inesperado durante o login: JWT Error"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));

        verify(usuarioRepository).findByCpf("12345678901");
        verify(jwtService).generateToken(any(Usuario.class));
    }

    @Test
    void login_WithUserRole_ShouldReturnToken() throws Exception {
        Usuario userRole = new Usuario();
        userRole.setCpf("98765432100");
        userRole.setNome("Maria Santos");
        userRole.setSenha(passwordEncoder.encode("senha123"));
        userRole.setPerfil(PerfilUsuario.CLIENTE);

        LoginRequest userRequest = new LoginRequest();
        userRequest.setCpf("98765432100");
        userRequest.setSenha("senha123");

        when(usuarioRepository.findByCpf("98765432100")).thenReturn(Optional.of(userRole));
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.perfil").value("CLIENTE"));

        verify(usuarioRepository).findByCpf("98765432100");
        verify(jwtService).generateToken(any(Usuario.class));
    }

    @Test
    void login_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(usuarioRepository, never()).findByCpf(any());
        verify(jwtService, never()).generateToken(any(Usuario.class));
    }
}

