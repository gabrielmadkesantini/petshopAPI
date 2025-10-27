package com.petshop.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.petshop.model.Usuario;
import com.petshop.model.Cliente;
import com.petshop.dto.ErrorResponse;
import com.petshop.dto.UsuarioCreateResponse;
import com.petshop.dto.UsuarioUpdateRequest;
import com.petshop.model.PerfilUsuario;
import com.petshop.repository.UsuarioRepository;
import com.petshop.repository.ClienteRepository;
import com.petshop.service.JwtService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/{cpf}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUsuarioByCpf(@PathVariable String cpf) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findByCpf(cpf);
            if (usuario.isPresent()) {
                return ResponseEntity.ok(usuario.get());
            } else {
                ErrorResponse error = new ErrorResponse(
                    404, 
                    "Usuário não encontrado", 
                    "Não foi encontrado um usuário com o CPF: " + cpf,
                    "/api/usuarios/" + cpf
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                500, 
                "Erro interno do servidor", 
                "Ocorreu um erro inesperado ao buscar o usuário: " + e.getMessage(),
                "/api/usuarios/" + cpf
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    
    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getUsuariosByNome(@RequestParam(required = false) String nome,
                                                          @RequestParam(required = false) String perfil) {
        List<Usuario> usuarios;
        
        if (nome != null && perfil != null) {
            usuarios = usuarioRepository.findByNomeAndPerfil(nome, PerfilUsuario.valueOf(perfil));
        } else if (nome != null) {
            usuarios = usuarioRepository.findByNomeContaining(nome);
        } else if (perfil != null) {
            usuarios = usuarioRepository.findByPerfil(PerfilUsuario.valueOf(perfil));
        } else {
            usuarios = usuarioRepository.findAll();
        }
        
        return ResponseEntity.ok(usuarios);
    }
    
    @PostMapping
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            if (usuarioRepository.existsByCpf(usuario.getCpf())) {
                ErrorResponse error = new ErrorResponse(
                    400, 
                    "CPF já cadastrado", 
                    "Já existe um usuário cadastrado com o CPF: " + usuario.getCpf(),
                    "/api/usuarios"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            usuario.setSenha(bcrypt.encode(usuario.getSenha()));
            
            Usuario savedUsuario = usuarioRepository.save(usuario);

            if(savedUsuario == null){
                ErrorResponse error = new ErrorResponse(
                    500, 
                    "Erro ao salvar usuário", 
                    "Não foi possível salvar o usuário no banco de dados",
                    "/api/usuarios"
                );
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }

            Cliente clienteCriado = null;
            if (savedUsuario.getPerfil() == PerfilUsuario.CLIENTE) {
                Cliente cliente = new Cliente();
                cliente.setNome(savedUsuario.getNome());
                cliente.setCpf(savedUsuario.getCpf());
                cliente.setFoto(null); // Pode ser definido posteriormente
                
                try {
                    clienteCriado = clienteRepository.save(cliente);
                } catch (Exception e) {
                    usuarioRepository.delete(savedUsuario);
                    ErrorResponse error = new ErrorResponse(
                        500, 
                        "Erro ao criar cliente", 
                        "Usuário criado mas falhou ao criar registro de cliente: " + e.getMessage(),
                        "/api/usuarios"
                    );
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
                }
            }

            String token = jwtService.generateToken(savedUsuario);

            UsuarioCreateResponse response = new UsuarioCreateResponse(token, savedUsuario.getCpf(), savedUsuario.getNome(), savedUsuario.getPerfil(), clienteCriado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                500, 
                "Erro interno do servidor", 
                "Ocorreu um erro inesperado ao criar o usuário: " + e.getMessage(),
                "/api/usuarios"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PutMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUsuario(@PathVariable String cpf, @Valid @RequestBody UsuarioUpdateRequest usuarioUpdate) {
        try {
            Usuario existingUsuario = usuarioRepository.findByCpf(cpf)
                .orElse(null);

            if (existingUsuario == null) {
                ErrorResponse error = new ErrorResponse(
                    404, 
                    "Usuário não encontrado", 
                    "Não foi encontrado um usuário com o CPF: " + cpf,
                    "/api/usuarios/" + cpf
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            if (usuarioUpdate.getNome() != null && !usuarioUpdate.getNome().trim().isEmpty()) {
                existingUsuario.setNome(usuarioUpdate.getNome());
            }
            
            if (usuarioUpdate.getPerfil() != null) {
                existingUsuario.setPerfil(usuarioUpdate.getPerfil());
            }
            
            if (usuarioUpdate.getSenha() != null && !usuarioUpdate.getSenha().trim().isEmpty()) {
                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                existingUsuario.setSenha(bcrypt.encode(usuarioUpdate.getSenha()));
            }

            Usuario updatedUsuario = usuarioRepository.save(existingUsuario);

            return ResponseEntity.ok(updatedUsuario);

        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                500, 
                "Erro interno do servidor", 
                "Ocorreu um erro inesperado ao atualizar o usuário: " + e.getMessage(),
                "/api/usuarios/" + cpf
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @DeleteMapping("/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> deleteUsuario(@PathVariable String cpf) {
        try {
            if (!usuarioRepository.existsByCpf(cpf)) {
                ErrorResponse error = new ErrorResponse(
                    404, 
                    "Usuário não encontrado", 
                    "Não foi encontrado um usuário com o CPF: " + cpf,
                    "/api/usuarios/" + cpf
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            if (clienteRepository.existsByCpf(cpf)) {
                clienteRepository.deleteByCpf(cpf);
            }
            
            usuarioRepository.deleteByCpf(cpf);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                500, 
                "Erro interno do servidor", 
                "Ocorreu um erro inesperado ao deletar o usuário: " + e.getMessage(),
                "/api/usuarios/" + cpf
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}


