package com.petshop.controller;

import com.petshop.dto.ErrorResponse;
import com.petshop.dto.LoginRequest;
import com.petshop.dto.LoginResponse;
import com.petshop.dto.ClienteResponse;
import com.petshop.dto.UsuarioResponse;
import com.petshop.model.Usuario;
import com.petshop.model.Cliente;
import com.petshop.repository.UsuarioRepository;
import com.petshop.repository.ClienteRepository;
import com.petshop.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(loginRequest.getCpf());
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

            if(usuarioOpt.isEmpty()){
                ErrorResponse error = new ErrorResponse(
                    404, 
                    "Usuário não encontrado", 
                    "Não foi encontrado um usuário com o CPF: " + loginRequest.getCpf(),
                    "/api/auth/login"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Usuario usuarioEncontrado = usuarioOpt.get();
            boolean passMatches = bcrypt.matches(loginRequest.getSenha(), usuarioEncontrado.getSenha());
            
            if(!passMatches){
                ErrorResponse error = new ErrorResponse(
                    400, 
                    "Senha incorreta", 
                    "A senha fornecida está incorreta",
                    "/api/auth/login"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            String token = jwtService.generateToken(
                usuarioEncontrado
            );
            
            Optional<Cliente> clienteOpt = clienteRepository.findByCpf(usuarioEncontrado.getCpf());
            ClienteResponse clienteResponse = null;
            
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                clienteResponse = new ClienteResponse(
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getCpf(),
                    cliente.getDataCadastro(),
                    cliente.getFoto()
                );
            }
            
            LoginResponse response = new LoginResponse(
                token,
                usuarioEncontrado.getCpf(),
                usuarioEncontrado.getNome(),
                usuarioEncontrado.getPerfil(),
                clienteResponse
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                500, 
                "Erro interno do servidor", 
                "Ocorreu um erro inesperado durante o login: " + e.getMessage(),
                "/api/auth/login"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Usuario usuario = jwtService.validateToken(token);
            if (usuario == null) {
                ErrorResponse error = new ErrorResponse(
                    401, 
                    "Token inválido", 
                    "O token fornecido é inválido ou já foi invalidado",
                    "/api/auth/logout"
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            jwtService.invalidateToken(token);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout realizado com sucesso");
            response.put("timestamp", new Date().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                500, 
                "Erro interno do servidor", 
                "Ocorreu um erro inesperado durante o logout: " + e.getMessage(),
                "/api/auth/logout"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Usuario usuario = jwtService.validateToken(token);
            if (usuario == null) {
                ErrorResponse error = new ErrorResponse(
                    401, 
                    "Token inválido", 
                    "O token fornecido é inválido ou foi invalidado",
                    "/api/auth/me"
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            Optional<Cliente> clienteOpt = clienteRepository.findByCpf(usuario.getCpf());
            ClienteResponse clienteResponse = null;
            
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                clienteResponse = new ClienteResponse(
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getCpf(),
                    cliente.getDataCadastro(),
                    cliente.getFoto()
                );
            }
            
            UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getCpf(),
                usuario.getNome(),
                usuario.getPerfil(),
                clienteResponse
            );
            
            return ResponseEntity.ok(usuarioResponse);
            
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(
                500, 
                "Erro interno do servidor", 
                "Ocorreu um erro inesperado: " + e.getMessage(),
                "/api/auth/me"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}


