# Sistema de Controle de Acesso Baseado em Roles

Este documento descreve a implementação do sistema de controle de acesso baseado em roles (RBAC) no projeto PetShop API.

## Roles Implementados

### 1. ADMIN
- **Permissões**: Pode incluir, excluir, alterar e visualizar qualquer cadastro
- **Características**:
  - Acesso total a todos os recursos
  - Pode criar, editar e deletar qualquer cliente, pet, contato ou endereço
  - Pode visualizar todos os dados do sistema

### 2. CLIENTE (USER)
- **Permissões**: Pode visualizar e alterar apenas os seus registros e/ou registros dos seus pets
- **Características**:
  - Acesso limitado aos seus próprios dados
  - Pode criar, editar e deletar apenas seus próprios pets
  - Pode criar, editar e deletar apenas seus próprios contatos e endereços
  - Não pode criar outros clientes (apenas admins podem)

## Implementação Técnica

### 1. Modelo de Usuário
- O enum `PerfilUsuario` define os perfis: `ADMIN` e `CLIENTE`
- O método `getAuthorities()` retorna as roles apropriadas:
  - ADMIN: `ROLE_ADMIN` + `ROLE_USER`
  - CLIENTE: `ROLE_USER`

### 2. Serviço de Roles (`RoleService`)
O `RoleService` centraliza toda a lógica de verificação de permissões:

#### Métodos Principais:
- `isAdmin()`: Verifica se o usuário atual é admin
- `isCliente()`: Verifica se o usuário atual é cliente
- `getCurrentUser()`: Obtém o usuário autenticado atual
- `canAccessCliente(Long clienteId)`: Verifica se pode acessar um cliente específico
- `canAccessPet(Long petId)`: Verifica se pode acessar um pet específico
- `canModifyCliente(Long clienteId)`: Verifica se pode modificar um cliente
- `canModifyPet(Long petId, Long clienteId)`: Verifica se pode modificar um pet
- `canDeleteCliente()`: Verifica se pode deletar clientes
- `canDeletePet(Long petId)`: Verifica se pode deletar um pet

### 3. Controllers Atualizados
Todos os controllers foram atualizados para implementar o controle de acesso:

#### ClienteController:
- **GET /api/clientes**: Apenas ADMIN
- **GET /api/clientes/{id}**: USER ou ADMIN (com verificação de propriedade)
- **GET /api/clientes/cpf/{cpf}**: USER ou ADMIN (com verificação de propriedade)
- **GET /api/clientes/buscar**: USER ou ADMIN (filtrado por propriedade)
- **POST /api/clientes**: Apenas ADMIN
- **PUT /api/clientes/{id}**: USER ou ADMIN (com verificação de propriedade)
- **DELETE /api/clientes/{id}**: Apenas ADMIN

#### PetsController:
- **GET /api/pets**: Apenas ADMIN
- **GET /api/pets/{id}**: USER ou ADMIN (com verificação de propriedade)
- **GET /api/pets/cliente/{clienteId}**: USER ou ADMIN (com verificação de propriedade)
- **GET /api/pets/raca/{racaId}**: USER ou ADMIN (filtrado por propriedade)
- **GET /api/pets/buscar-avancado**: USER ou ADMIN (filtrado por propriedade)
- **POST /api/pets**: USER ou ADMIN (com verificação de propriedade do cliente)
- **PUT /api/pets/{id}**: USER ou ADMIN (com verificação de propriedade)
- **DELETE /api/pets/{id}**: USER ou ADMIN (com verificação de propriedade)

#### ContatoController:
- **GET /api/contatos**: Apenas ADMIN
- **GET /api/contatos/{id}**: USER ou ADMIN (com verificação de propriedade)
- **GET /api/contatos/cliente/{clienteId}**: USER ou ADMIN (com verificação de propriedade)
- **GET /api/contatos/tipo/{tipo}**: USER ou ADMIN (filtrado por propriedade)
- **GET /api/contatos/buscar**: USER ou ADMIN (filtrado por propriedade)
- **POST /api/contatos**: USER ou ADMIN (com verificação de propriedade do cliente)
- **PUT /api/contatos/{id}**: USER ou ADMIN (com verificação de propriedade)
- **DELETE /api/contatos/{id}**: USER ou ADMIN (com verificação de propriedade)

#### EnderecoController:
- **GET /api/enderecos**: Apenas ADMIN
- **GET /api/enderecos/{id}**: USER ou ADMIN (com verificação de propriedade)
- **GET /api/enderecos/cliente/{clienteId}**: USER ou ADMIN (com verificação de propriedade)
- **GET /api/enderecos/buscar**: USER ou ADMIN (filtrado por propriedade)
- **POST /api/enderecos**: USER ou ADMIN (com verificação de propriedade do cliente)
- **PUT /api/enderecos/{id}**: USER ou ADMIN (com verificação de propriedade)
- **DELETE /api/enderecos/{id}**: USER ou ADMIN (com verificação de propriedade)

### 4. Configuração de Segurança
A `SecurityConfig` foi atualizada para:
- Permitir acesso público apenas aos endpoints de autenticação e cadastro de usuários
- Requerer autenticação para todos os outros endpoints
- As verificações de roles são feitas nos controllers usando `@PreAuthorize`

## Lógica de Verificação de Propriedade

### Para Clientes:
- ADMIN: Pode acessar qualquer cliente
- CLIENTE: Só pode acessar clientes com o mesmo CPF do usuário logado

### Para Pets:
- ADMIN: Pode acessar qualquer pet
- CLIENTE: Só pode acessar pets cujo cliente tenha o mesmo CPF do usuário logado

### Para Contatos e Endereços:
- ADMIN: Pode acessar qualquer contato/endereço
- CLIENTE: Só pode acessar contatos/endereços cujo cliente tenha o mesmo CPF do usuário logado

## Respostas de Erro

Quando um usuário tenta acessar recursos sem permissão:
- **Status Code**: 403 (Forbidden)
- **Mensagem**: "Acesso Negado"
- **Descrição**: "Você não tem permissão para acessar este recurso. Verifique se você possui as credenciais necessárias."

## Exemplos de Uso

### Cenário 1: Cliente tentando acessar dados de outro cliente
```http
GET /api/clientes/123
Authorization: Bearer <token_do_cliente>
```
**Resultado**: 403 Forbidden (se o cliente 123 não pertencer ao usuário logado)

### Cenário 2: Admin acessando qualquer recurso
```http
GET /api/clientes/123
Authorization: Bearer <token_do_admin>
```
**Resultado**: 200 OK (admin pode acessar qualquer cliente)

### Cenário 3: Cliente acessando seus próprios dados
```http
GET /api/clientes/456
Authorization: Bearer <token_do_cliente>
```
**Resultado**: 200 OK (se o cliente 456 pertencer ao usuário logado)

## Considerações de Segurança

1. **Verificação Dupla**: Tanto `@PreAuthorize` quanto verificações manuais no código
2. **Filtragem de Resultados**: Listas são filtradas para mostrar apenas dados permitidos
3. **Validação de Propriedade**: Verificação baseada em CPF para garantir que usuários só acessem seus próprios dados
4. **Logs de Segurança**: Todas as tentativas de acesso negado são registradas

## Manutenção

Para adicionar novos endpoints ou modificar permissões:
1. Atualize o `RoleService` se necessário
2. Adicione verificações nos controllers
3. Atualize a documentação
4. Teste as permissões com diferentes tipos de usuário









