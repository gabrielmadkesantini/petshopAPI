#!/bin/bash

# Script para executar testes da API PetShop
# Este script executa todos os tipos de testes: unitários, de integração e de repositório

echo "🐾 Iniciando testes da API PetShop..."
echo "======================================"

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado. Por favor, instale o Maven primeiro."
    exit 1
fi

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Por favor, instale o Java primeiro."
    exit 1
fi

echo "✅ Maven e Java encontrados"
echo ""

# Limpar e compilar o projeto
echo "🔨 Compilando o projeto..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo "❌ Falha na compilação do projeto"
    exit 1
fi
echo "✅ Compilação concluída"
echo ""

# Executar testes unitários
echo "🧪 Executando testes unitários..."
mvn test -Dtest="*Test" -q
if [ $? -ne 0 ]; then
    echo "❌ Falha nos testes unitários"
    exit 1
fi
echo "✅ Testes unitários concluídos"
echo ""

# Executar testes de repositório
echo "🗄️ Executando testes de repositório..."
mvn test -Dtest="*RepositoryTest" -q
if [ $? -ne 0 ]; then
    echo "❌ Falha nos testes de repositório"
    exit 1
fi
echo "✅ Testes de repositório concluídos"
echo ""

# Executar testes de integração
echo "🔗 Executando testes de integração..."
mvn test -Dtest="*IntegrationTest" -q
if [ $? -ne 0 ]; then
    echo "❌ Falha nos testes de integração"
    exit 1
fi
echo "✅ Testes de integração concluídos"
echo ""

# Executar todos os testes com relatório
echo "📊 Gerando relatório de cobertura..."
mvn test jacoco:report -q
if [ $? -ne 0 ]; then
    echo "❌ Falha na geração do relatório"
    exit 1
fi
echo "✅ Relatório de cobertura gerado"
echo ""

# Mostrar resumo dos testes
echo "📈 Resumo dos testes:"
echo "====================="
mvn test -q | grep -E "(Tests run|Failures|Errors|Skipped)"

echo ""
echo "🎉 Todos os testes foram executados com sucesso!"
echo "📁 Relatórios disponíveis em: target/site/jacoco/index.html"
echo "🐾 API PetShop está pronta para uso!"
