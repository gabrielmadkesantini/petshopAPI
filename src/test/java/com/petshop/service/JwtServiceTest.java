package com.petshop.service;

import com.petshop.model.PerfilUsuario;
import com.petshop.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        Usuario usuario = new Usuario();
        usuario.setCpf("12345678901");
        usuario.setNome("João Silva");
        usuario.setPerfil(PerfilUsuario.ADMIN);

        String token = jwtService.generateToken(usuario);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT tem 3 partes separadas por ponto
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnUsuario() {
        Usuario usuario = new Usuario();
        usuario.setCpf("12345678901");
        usuario.setNome("João Silva");
        usuario.setPerfil(PerfilUsuario.ADMIN);
        
        String token = jwtService.generateToken(usuario);

        Usuario result = jwtService.validateToken(token);

        assertThat(result).isNotNull();
        assertThat(result.getCpf()).isEqualTo("12345678901");
        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getPerfil()).isEqualTo(PerfilUsuario.ADMIN);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtService.validateToken(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao verificar token");
    }

    @Test
    void validateToken_WithExpiredToken_ShouldThrowException() {
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJwZXRzaG9wLWFwaSIsImlhdCI6MTYwOTQ1OTIwMCwiZXhwIjoxNjA5NDU5MjAwLCJjcGYiOiIxMjM0NTY3ODkwMSIsIm5vbWUiOiJKb8OjbyBTaWx2YSIsInBlcmZpbCI6IkFETUlOIn0.invalid";

        assertThatThrownBy(() -> jwtService.validateToken(expiredToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao verificar token");
    }

    @Test
    void createJwtWithPayload_ShouldCreateValidToken() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("cpf", "98765432100");
        payload.put("nome", "Maria Santos");
        payload.put("perfil", "USER");

        String token = jwtService.createJwtWithPayload(payload);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void validateToken_WithDifferentPerfil_ShouldReturnCorrectPerfil() {
        Usuario usuario = new Usuario();
        usuario.setCpf("11122233344");
        usuario.setNome("Pedro Costa");
        usuario.setPerfil(PerfilUsuario.CLIENTE);
        
        String token = jwtService.generateToken(usuario);

        Usuario result = jwtService.validateToken(token);

        assertThat(result.getPerfil()).isEqualTo(PerfilUsuario.CLIENTE);
    }

    @Test
    void generateToken_WithNullUsuario_ShouldThrowException() {
        assertThatThrownBy(() -> jwtService.generateToken(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void validateToken_WithMalformedToken_ShouldThrowException() {
        String malformedToken = "not.a.valid.jwt.token";

        assertThatThrownBy(() -> jwtService.validateToken(malformedToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao verificar token");
    }
}

