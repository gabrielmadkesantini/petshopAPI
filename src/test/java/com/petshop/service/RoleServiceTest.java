package com.petshop.service;

import com.petshop.model.Atendimento;
import com.petshop.model.Cliente;
import com.petshop.model.Pets;
import com.petshop.model.Usuario;
import com.petshop.model.PerfilUsuario;
import com.petshop.repository.AtendimentoRepository;
import com.petshop.repository.ClienteRepository;
import com.petshop.repository.PetsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class RoleServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PetsRepository petsRepository;

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RoleService roleService;

    private Usuario adminUser;
    private Usuario regularUser;
    private Cliente cliente;
    private Pets pet;
    private Atendimento atendimento;

    @BeforeEach
    void setUp() {
        adminUser = new Usuario();
        adminUser.setCpf("12345678901");
        adminUser.setNome("Admin User");
        adminUser.setPerfil(PerfilUsuario.ADMIN);

        regularUser = new Usuario();
        regularUser.setCpf("98765432100");
        regularUser.setNome("Regular User");
        regularUser.setPerfil(PerfilUsuario.CLIENTE);

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCpf("98765432100");
        cliente.setNome("Cliente Test");

        pet = new Pets();
        pet.setId(1L);
        pet.setNome("Pet Test");
        pet.setCliente(cliente);

        atendimento = new Atendimento();
        atendimento.setId(1L);
        atendimento.setPet(pet);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
     @SuppressWarnings("rawtypes")
    void isAdmin_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.isAdmin();

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void isAdmin_WithRegularUser_ShouldReturnFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.isAdmin();

        assertThat(result).isFalse();
    }

    @Test
    void isAdmin_WithNullAuthentication_ShouldReturnFalse() {
        when(securityContext.getAuthentication()).thenReturn(null);

        boolean result = roleService.isAdmin();

        assertThat(result).isFalse();
    }

    @Test
    void getCurrentUser_WithValidAuthentication_ShouldReturnUsuario() {
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        Usuario result = roleService.getCurrentUser();

        assertThat(result).isNotNull();
        assertThat(result.getCpf()).isEqualTo("98765432100");
        assertThat(result.getNome()).isEqualTo("Regular User");
    }

    @Test
    void getCurrentUser_WithNullAuthentication_ShouldReturnNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Usuario result = roleService.getCurrentUser();

        assertThat(result).isNull();
    }

    @Test
    void getCurrentUserCpf_WithValidUser_ShouldReturnCpf() {
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        String result = roleService.getCurrentUserCpf();

        assertThat(result).isEqualTo("98765432100");
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canAccessCliente_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canAccessCliente(1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canAccessCliente_WithOwnCliente_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        boolean result = roleService.canAccessCliente(1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canAccessCliente_WithDifferentCliente_ShouldReturnFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        
        Cliente differentCliente = new Cliente();
        differentCliente.setId(2L);
        differentCliente.setCpf("11122233344");
        when(clienteRepository.findById(2L)).thenReturn(Optional.of(differentCliente));

        boolean result = roleService.canAccessCliente(2L);

        assertThat(result).isFalse();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canAccessPet_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canAccessPet(1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canAccessPet_WithOwnPet_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(petsRepository.findById(1L)).thenReturn(Optional.of(pet));

        boolean result = roleService.canAccessPet(1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canModifyCliente_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canModifyCliente(1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canModifyCliente_WithNullClienteId_ShouldReturnFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canModifyCliente(null);

        assertThat(result).isFalse();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canDeleteCliente_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canDeleteCliente();

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canDeleteCliente_WithRegularUser_ShouldReturnFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canDeleteCliente();

        assertThat(result).isFalse();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canAccessAtendimento_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canAccessAtendimento(1L);

        assertThat(result).isTrue();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void canAccessAtendimento_WithOwnAtendimento_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(atendimento));

        boolean result = roleService.canAccessAtendimento(1L);

        assertThat(result).isTrue();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void canModifyAtendimento_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canModifyAtendimento(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void canModifyAtendimento_WithOwnPet_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(petsRepository.findById(1L)).thenReturn(Optional.of(pet));

        boolean result = roleService.canModifyAtendimento(null, 1L);

        assertThat(result).isTrue();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void isCliente_WithClienteUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.isCliente();

        assertThat(result).isTrue();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void isCliente_WithAdminUser_ShouldReturnFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.isCliente();

        assertThat(result).isFalse();
    }

    @Test
    void isCliente_WithNullAuthentication_ShouldReturnFalse() {
        when(securityContext.getAuthentication()).thenReturn(null);

        boolean result = roleService.isCliente();

        assertThat(result).isFalse();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canModifyPet_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canModifyPet(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void canModifyPet_WithOwnPet_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(petsRepository.findById(1L)).thenReturn(Optional.of(pet));

        boolean result = roleService.canModifyPet(1L, null);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canModifyPet_WithOwnClienteForCreation_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(clienteRepository.findByCpf("98765432100")).thenReturn(Optional.of(cliente));

        boolean result = roleService.canModifyPet(null, 1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canModifyPet_WithDifferentClienteForCreation_ShouldReturnFalse() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(clienteRepository.findByCpf("98765432100")).thenReturn(Optional.of(cliente));

        boolean result = roleService.canModifyPet(null, 2L);

        assertThat(result).isFalse();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canDeletePet_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canDeletePet(1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canDeletePet_WithOwnPet_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(petsRepository.findById(1L)).thenReturn(Optional.of(pet));

        boolean result = roleService.canDeletePet(1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canDeleteAtendimento_WithAdminUser_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        boolean result = roleService.canDeleteAtendimento(1L);

        assertThat(result).isTrue();
    }

    @Test
     @SuppressWarnings("rawtypes")
    void canDeleteAtendimento_WithOwnAtendimento_ShouldReturnTrue() {
        Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(authentication.getPrincipal()).thenReturn(regularUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(atendimentoRepository.findById(1L)).thenReturn(Optional.of(atendimento));

        boolean result = roleService.canDeleteAtendimento(1L);

        assertThat(result).isTrue();
    }
}

