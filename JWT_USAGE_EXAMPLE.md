# Exemplo de Uso do JwtService

Este documento demonstra como usar o `JwtService` implementado com a biblioteca `java-jwt`.

## Funcionalidades Implementadas

### 1. Criação de JWT com Payload Customizado

```java
@Autowired
private JwtService jwtService;

// Criar JWT com payload customizado
Map<String, Object> customPayload = new HashMap<>();
customPayload.put("userId", 123L);
customPayload.put("role", "admin");
customPayload.put("permissions", Arrays.asList("read", "write", "delete"));

String token = jwtService.createJwtWithPayload(customPayload);
```

### 2. Criação de JWT com Dados do Usuário

```java
// Criar JWT com dados específicos do usuário
String token = jwtService.generateToken("12345678901", "João Silva", "ADMIN", 1L);
```

### 3. Validação de JWT

```java
// Validar se o token é válido
Boolean isValid = jwtService.validateToken(token);
if (isValid) {
    System.out.println("Token é válido");
} else {
    System.out.println("Token é inválido");
}
```

### 4. Recuperação do Payload Completo

```java
// Recuperar todo o payload do JWT (verifica automaticamente e retorna o payload)
Map<String, Object> payload = jwtService.getPayload(token);
System.out.println("Payload completo: " + payload);
```

### 5. Recuperação de Valores Específicos

```java
// Recuperar valores específicos do payload
String cpf = jwtService.extractCpf(token);
String nome = jwtService.extractNome(token);
String perfil = jwtService.extractPerfil(token);
Long id = jwtService.extractId(token);
String subject = jwtService.extractSubject(token);

// Ou recuperar qualquer valor por chave
Object userId = jwtService.getPayloadValue(token, "userId");
```

### 6. Verificação de Expiração

```java
// Verificar se o token está expirado
Boolean isExpired = jwtService.isTokenExpired(token);
if (isExpired) {
    System.out.println("Token expirado");
} else {
    System.out.println("Token ainda válido");
}
```

## Exemplo Completo de Uso

```java
@Service
public class AuthService {
    
    @Autowired
    private JwtService jwtService;
    
    public String login(String cpf, String nome, String perfil, Long id) {
        // Gerar token após autenticação bem-sucedida
        return jwtService.generateToken(cpf, nome, perfil, id);
    }
    
    public boolean validateUserToken(String token) {
        return jwtService.validateToken(token);
    }
    
    public UserInfo getUserInfo(String token) {
        if (!jwtService.validateToken(token)) {
            throw new RuntimeException("Token inválido");
        }
        
        return new UserInfo(
            jwtService.extractCpf(token),
            jwtService.extractNome(token),
            jwtService.extractPerfil(token),
            jwtService.extractId(token)
        );
    }
}
```

## Configurações

- **SECRET_KEY**: Chave secreta para assinar os tokens (configurada no código)
- **EXPIRATION_TIME**: Tempo de expiração padrão (24 horas)
- **ISSUER**: Emissor dos tokens ("petshop-api")

## Tratamento de Erros

O serviço trata automaticamente:
- Tokens inválidos ou malformados
- Tokens expirados
- Tokens com assinatura inválida

Todos os métodos que podem falhar lançam `RuntimeException` com a mensagem de erro original da biblioteca `java-jwt`, proporcionando informações detalhadas sobre o problema.

### Exemplo de Tratamento de Erro:

```java
try {
    Map<String, Object> payload = jwtService.getPayload(token);
    // Usar o payload...
} catch (RuntimeException e) {
    System.err.println("Erro ao processar JWT: " + e.getMessage());
    // e.getMessage() conterá a mensagem específica da biblioteca java-jwt
}
```
