# Implementação JWT - PetShop API

## Resumo das Alterações

Foi implementada a geração de token JWT para autenticação na API do PetShop. As seguintes alterações foram realizadas:

### 1. Dependências Adicionadas (pom.xml)
- `io.jsonwebtoken:jjwt-api:0.12.3`
- `io.jsonwebtoken:jjwt-impl:0.12.3`
- `io.jsonwebtoken:jjwt-jackson:0.12.3`

### 2. Novos Arquivos Criados

#### LoginResponse.java (DTO)
- Classe para encapsular a resposta do login
- Contém: token, id, nome, cpf e perfil do usuário

#### JwtService.java (Serviço)
- Classe responsável pela geração e validação de tokens JWT
- Métodos principais:
  - `generateToken()`: Gera token com payload do usuário
  - `validateToken()`: Valida token e CPF
  - `extractUsername()`, `extractCpf()`, etc.: Extraem dados do token

### 3. AuthController.java (Atualizado)
- Método `login()` agora retorna `LoginResponse` em vez de `Usuario`
- Gera token JWT após validação de credenciais
- Retorna token junto com dados do usuário

## Como Usar

### Endpoint de Login
```
POST /api/auth/login
Content-Type: application/json

{
    "cpf": "123.456.789-00",
    "senha": "senha123"
}
```

### Resposta de Sucesso
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "id": 1,
    "nome": "João Silva",
    "cpf": "123.456.789-00",
    "perfil": "ADMIN"
}
```

### Payload do Token
O token JWT contém as seguintes informações:
- `cpf`: CPF do usuário
- `nome`: Nome do usuário
- `perfil`: Perfil do usuário (ADMIN, FUNCIONARIO, etc.)
- `id`: ID do usuário
- `exp`: Data de expiração (24 horas)
- `iat`: Data de criação

### Configurações
- **Chave de assinatura**: Definida em `JwtService.SECRET_KEY`
- **Tempo de expiração**: 24 horas (86400000 ms)
- **Algoritmo**: HS256

## Próximos Passos
Para completar a implementação de segurança, considere:
1. Criar filtro JWT para proteger endpoints
2. Implementar refresh token
3. Adicionar validação de token em outros controllers
4. Configurar CORS adequadamente
5. Implementar logout (blacklist de tokens)

