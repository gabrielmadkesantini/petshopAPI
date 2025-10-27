package com.petshop.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.model.Atendimento;
import com.petshop.model.Cliente;
import com.petshop.model.Pets;
import com.petshop.model.Raca;
import com.petshop.model.Usuario;
import com.petshop.model.PerfilUsuario;
import com.petshop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
class PetshopApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("petshop_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RacaRepository racaRepository;

    @Autowired
    private PetsRepository petsRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    private MockMvc mockMvc;
    private BCryptPasswordEncoder passwordEncoder;

    private Usuario adminUser;
    private Usuario regularUser;
    private Cliente cliente;
    private Raca raca;
    private Pets pet;
    private Atendimento atendimento;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        passwordEncoder = new BCryptPasswordEncoder();

        atendimentoRepository.deleteAll();
        petsRepository.deleteAll();
        clienteRepository.deleteAll();
        racaRepository.deleteAll();
        usuarioRepository.deleteAll();

        adminUser = new Usuario();
        adminUser.setCpf("12345678901");
        adminUser.setNome("Admin User");
        adminUser.setSenha(passwordEncoder.encode("admin123"));
        adminUser.setPerfil(PerfilUsuario.ADMIN);
        adminUser = usuarioRepository.save(adminUser);

        regularUser = new Usuario();
        regularUser.setCpf("98765432100");
        regularUser.setNome("Regular User");
        regularUser.setSenha(passwordEncoder.encode("user123"));
        regularUser.setPerfil(PerfilUsuario.CLIENTE);
        regularUser = usuarioRepository.save(regularUser);

        cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setCpf("11122233344");
        cliente = clienteRepository.save(cliente);

        raca = new Raca();
        raca.setDescricao("Golden Retriever");
        raca = racaRepository.save(raca);

        pet = new Pets();
        pet.setNome("Rex");
        pet.setDataNascimento(LocalDate.of(2020, 5, 15));
        pet.setCliente(cliente);
        pet.setRaca(raca);
        pet = petsRepository.save(pet);

        atendimento = new Atendimento();
        atendimento.setPet(pet);
        atendimento.setData(LocalDate.now());
        atendimento.setDescricao("Consulta veterinária");
        atendimento.setValor(new BigDecimal("100.00"));
        atendimento = atendimentoRepository.save(atendimento);

        adminToken = performLogin("12345678901", "admin123");
        userToken = performLogin("98765432100", "user123");
    }

    private String performLogin(String cpf, String senha) throws Exception {
        String loginRequest = String.format("""
                {
                    "cpf": "%s",
                    "senha": "%s"
                }
                """, cpf, senha);

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void completeWorkflow_AdminUser_ShouldWorkCorrectly() throws Exception {
        mockMvc.perform(get("/api/pets")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Rex"));

        mockMvc.perform(get("/api/atendimentos")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].descricao").value("Consulta veterinária"));

        String novoPetJson = String.format("""
                {
                    "nome": "Buddy",
                    "dataNascimento": "2022-01-01",
                    "cliente": {
                        "id": %d
                    },
                    "raca": {
                        "id": %d
                    }
                }
                """, cliente.getId(), raca.getId());

        mockMvc.perform(post("/api/pets")
                .header("Authorization", "Bearer " + adminToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(novoPetJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Buddy"));

        String novoAtendimentoJson = String.format("""
                {
                    "pet": {
                        "id": %d
                    },
                    "data": "%s",
                    "descricao": "Vacinação",
                    "valor": 80.00
                }
                """, pet.getId(), LocalDate.now().toString());

        mockMvc.perform(post("/api/atendimentos")
                .header("Authorization", "Bearer " + adminToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(novoAtendimentoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Vacinação"));

        String petAtualizadoJson = """
                {
                    "nome": "Rex Atualizado",
                    "dataNascimento": "2020-05-15",
                    "cliente": {
                        "id": 1
                    },
                    "raca": {
                        "id": 1
                    }
                }
                """;

        mockMvc.perform(put("/api/pets/1")
                .header("Authorization", "Bearer " + adminToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(petAtualizadoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Rex Atualizado"));

        mockMvc.perform(delete("/api/atendimentos/1")
                .header("Authorization", "Bearer " + adminToken)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void completeWorkflow_RegularUser_ShouldHaveLimitedAccess() throws Exception {
        mockMvc.perform(get("/api/pets")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/atendimentos")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/pets/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Rex"));

        mockMvc.perform(get("/api/atendimentos/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Consulta veterinária"));

        String novoPetJson = String.format("""
                {
                    "nome": "Buddy",
                    "dataNascimento": "2022-01-01",
                    "cliente": {
                        "id": %d
                    },
                    "raca": {
                        "id": %d
                    }
                }
                """, cliente.getId(), raca.getId());

        mockMvc.perform(post("/api/pets")
                .header("Authorization", "Bearer " + userToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(novoPetJson))
                .andExpect(status().isForbidden());

        String novoAtendimentoJson = String.format("""
                {
                    "pet": {
                        "id": %d
                    },
                    "data": "%s",
                    "descricao": "Vacinação",
                    "valor": 80.00
                }
                """, pet.getId(), LocalDate.now().toString());

        mockMvc.perform(post("/api/atendimentos")
                .header("Authorization", "Bearer " + userToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(novoAtendimentoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void authentication_WithInvalidCredentials_ShouldReturnError() throws Exception {
        String invalidLoginRequest = """
                {
                    "cpf": "99999999999",
                    "senha": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidLoginRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuário não encontrado"));
    }

    @Test
    void authentication_WithWrongPassword_ShouldReturnError() throws Exception {
        String wrongPasswordRequest = """
                {
                    "cpf": "12345678901",
                    "senha": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(wrongPasswordRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Senha incorreta"));
    }

    @Test
    void apiAccess_WithoutToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/atendimentos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void apiAccess_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/pets")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void searchFunctionality_ShouldWorkCorrectly() throws Exception {
        mockMvc.perform(get("/api/pets/cliente/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Rex"));

        mockMvc.perform(get("/api/pets/raca/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Rex"));

        mockMvc.perform(get("/api/atendimentos/pet/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].descricao").value("Consulta veterinária"));

        mockMvc.perform(get("/api/atendimentos/data/" + LocalDate.now())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].descricao").value("Consulta veterinária"));

        mockMvc.perform(get("/api/atendimentos/buscar")
                .param("valorMin", "50.00")
                .param("valorMax", "150.00")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].valor").value(100.00));
    }

    @Test
    void dataValidation_ShouldReturnBadRequest() throws Exception {
        String invalidPetJson = """
                {
                    "dataNascimento": "2022-01-01",
                    "cliente": {
                        "id": 1
                    },
                    "raca": {
                        "id": 1
                    }
                }
                """;

        mockMvc.perform(post("/api/pets")
                .header("Authorization", "Bearer " + adminToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPetJson))
                .andExpect(status().isBadRequest());

        String invalidAtendimentoJson = """
                {
                    "pet": {
                        "id": 1
                    },
                    "data": "2024-01-01",
                    "descricao": "Consulta"
                }
                """;

        mockMvc.perform(post("/api/atendimentos")
                .header("Authorization", "Bearer " + adminToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidAtendimentoJson))
                .andExpect(status().isBadRequest());
    }
}

