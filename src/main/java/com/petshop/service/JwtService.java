package com.petshop.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.petshop.model.Usuario;
import com.petshop.model.PerfilUsuario;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {
    
    private final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long EXPIRATION_TIME = 86400000; // 24 horas em millisegundos
    private final String ISSUER = "petshop-api";
    
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET_KEY);
    }
    
    public Usuario validateToken(String token) {
        try {
            if (blacklistedTokens.contains(token)) {
                throw new RuntimeException("Token foi invalidado (logout)");
            }
            
            DecodedJWT decodedJWT = JWT.require(getAlgorithm())
                        .withIssuer(ISSUER)
                        .build()
                        .verify(token);
            
            String cpf = decodedJWT.getClaim("cpf").asString();
            String nome = decodedJWT.getClaim("nome").asString();
            String perfilStr = decodedJWT.getClaim("perfil").asString();
            
            PerfilUsuario perfil = PerfilUsuario.valueOf(perfilStr);
            
            Usuario usuario = new Usuario();
            usuario.setCpf(cpf);
            usuario.setNome(nome);
            usuario.setPerfil(perfil);
            
            return usuario;
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Erro ao verificar token: " + e.getMessage(), e);
        }
    }

    /**
     * Cria um JWT com payload customizado
     * @param payload Map com os dados do payload
     * @return String do JWT gerado
     */
    public String createJwtWithPayload(Map<String, Object> payload) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withPayload(payload)
                .sign(getAlgorithm());
    }
    
    /**
     * Cria um JWT com dados específicos do usuário
     * @param cpf Usuario do usuário
     * @param nome Nome do usuário
     * @param perfil Perfil do usuário
     * @return String do JWT gerado
     */
    public   String generateToken(Usuario usuario) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("cpf", usuario.getCpf());
        payload.put("nome", usuario.getNome());
        payload.put("perfil", usuario.getPerfil().name());
        
        return createJwtWithPayload(payload);
    }
    
    /**
     * Invalida um token adicionando-o à blacklist
     * @param token Token a ser invalidado
     */
    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }
    
    /**
     * Verifica se um token está na blacklist
     * @param token Token a ser verificado
     * @return true se o token está invalidado
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Remove tokens expirados da blacklist (limpeza periódica)
     * Em produção, implementar com scheduler ou usar Redis com TTL
     */
    public void cleanupExpiredTokens() {
    }
    
}

