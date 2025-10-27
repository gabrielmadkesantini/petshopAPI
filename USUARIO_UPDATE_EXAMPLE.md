# Exemplo de Atualização de Usuário

## Problema Resolvido

O método `updateUsuario` anterior tinha um problema: quando campos como `senha` vinham vazios ou nulos no body da requisição, a validação `@NotBlank` do modelo `Usuario` falhava antes mesmo de chegar na lógica de atualização.

## Solução Implementada

1. **Criado DTO específico para atualização**: `UsuarioUpdateRequest.java`
   - Campos opcionais (sem validações `@NotBlank`)
   - Permite atualização parcial dos dados

2. **Atualizado método `updateUsuario`**:
   - Usa o novo DTO `UsuarioUpdateRequest`
   - Implementa lógica de atualização parcial
   - Só atualiza campos que foram fornecidos (não nulos/vazios)
   - Criptografa senha apenas quando uma nova senha é fornecida

## Como Usar

### Exemplo 1: Atualizar apenas o nome
```json
PUT /api/usuarios/123.456.789-00
{
    "nome": "Novo Nome"
}
```

### Exemplo 2: Atualizar nome e perfil
```json
PUT /api/usuarios/123.456.789-00
{
    "nome": "Novo Nome",
    "perfil": "ADMIN"
}
```

### Exemplo 3: Atualizar senha
```json
PUT /api/usuarios/123.456.789-00
{
    "senha": "novaSenha123"
}
```

### Exemplo 4: Atualizar todos os campos
```json
PUT /api/usuarios/123.456.789-00
{
    "nome": "Nome Atualizado",
    "perfil": "USER",
    "senha": "novaSenha123"
}
```

## Comportamento

- **Campos não fornecidos**: Mantêm os valores existentes no banco
- **Campos nulos/vazios**: São ignorados (não atualizados)
- **Senha**: Só é atualizada se fornecida e não vazia
- **Criptografia**: Nova senha é automaticamente criptografada com BCrypt

## Vantagens

1. ✅ **Atualização parcial**: Permite atualizar apenas campos específicos
2. ✅ **Sem validação obrigatória**: Não falha quando campos vêm vazios
3. ✅ **Segurança**: Senha é criptografada automaticamente
4. ✅ **Flexibilidade**: API mais robusta para diferentes cenários de uso








