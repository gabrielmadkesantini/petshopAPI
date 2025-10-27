# Exemplo de Uso da Nova Funcionalidade de Criação de Atendimento

## Endpoint
`POST /api/atendimentos`

## Request Body
```json
{
  "pet": {
    "id": 16
  },
  "cliente": {
    "id": 44
  },
  "descricao": "Vacinação anual",
  "valor": 120.00
}
```

## Response
```json
{
  "id": 1,
  "pet": {
    "id": 16,
    "nome": "Rex",
    "dataNascimento": "2020-05-15",
    "foto": "rex.jpg",
    "raca": {
      "id": 1,
      "nome": "Golden Retriever"
    },
    "cliente": {
      "id": 44,
      "nome": "João Silva",
      "cpf": "123.456.789-01",
      "dataCadastro": "2023-01-15",
      "foto": "joao.jpg"
    }
  },
  "cliente": {
    "id": 44,
    "nome": "João Silva",
    "cpf": "123.456.789-01",
    "dataCadastro": "2023-01-15",
    "foto": "joao.jpg"
  },
  "descricao": "Vacinação anual",
  "valor": 120.00,
  "data": "2025-10-27"
}
```

## Funcionalidades Implementadas

1. **DTO AtendimentoCreateRequest**: Criado para receber apenas os IDs do cliente e pet, com validações apropriadas.

2. **Busca Automática**: O sistema automaticamente busca os dados completos do cliente e pet pelos IDs fornecidos.

3. **Validação**: Verifica se o cliente e pet existem antes de criar o atendimento.

4. **Retorno Completo**: Retorna o atendimento criado com todos os dados relacionados (cliente e pet completos).

5. **Tratamento de Erros**: Retorna erro 400 (Bad Request) se cliente ou pet não forem encontrados.

## Código Implementado

### DTO AtendimentoCreateRequest
- Validação de campos obrigatórios
- Classes internas para referência de Pet e Cliente
- Validação de valores positivos para o valor

### Controller AtendimentoController
- Método `createAtendimento` modificado para usar o novo DTO
- Busca automática de cliente e pet pelos IDs
- Validação de existência antes da criação
- Retorno do atendimento com dados completos usando `findByIdWithDetails`

### Testes Atualizados
- Testes modificados para usar o novo DTO
- Mocks adicionados para ClienteRepository e PetsRepository
- Validação de todos os cenários de criação
