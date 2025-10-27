#!/bin/bash

# Script para testar a API de Pets e mostrar logs

echo "=== Testando API de Pets ==="
echo

# URL base da API
BASE_URL="http://localhost:8080/api"

# Fazer login para obter token
echo "1. Fazendo login..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "123.456.789-00",
    "senha": "senha123"
  }')

echo "Resposta do login: $LOGIN_RESPONSE"
echo

# Extrair token (assumindo que a resposta é JSON válido)
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "ERRO: Não foi possível obter o token de autenticação"
    echo "Verifique se a aplicação está rodando e se as credenciais estão corretas"
    exit 1
fi

echo "Token obtido: ${TOKEN:0:20}..."
echo

# Testar criação de pet
echo "2. Testando criação de pet..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/pets" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id_cliente": 1,
    "id_raca": 1,
    "nome": "Rex Teste",
    "dataNascimento": "2020-05-15",
    "foto": "https://example.com/foto.jpg"
  }')

echo "Resposta da criação: $CREATE_RESPONSE"
echo

# Testar busca de pets
echo "3. Testando busca de pets..."
GET_RESPONSE=$(curl -s -X GET "$BASE_URL/pets" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

echo "Resposta da busca: $GET_RESPONSE"
echo

echo "=== Teste concluído ==="
echo "Verifique os logs no console da aplicação para ver os detalhes dos erros"







