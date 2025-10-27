# Testes Unitários da API PetShop

Este projeto inclui uma suíte completa de testes unitários para garantir a qualidade e funcionalidade da API PetShop.

## 📋 Tipos de Testes

### 1. Testes de Serviços (`*ServiceTest`)
- **JwtServiceTest**: Testa geração e validação de tokens JWT
- **RoleServiceTest**: Testa controle de acesso baseado em roles

### 2. Testes de Controllers (`*ControllerTest`)
- **AtendimentoControllerTest**: Testa endpoints de atendimentos
- **AuthControllerTest**: Testa autenticação e login
- **PetsControllerTest**: Testa endpoints de pets

### 3. Testes de Repositórios (`*RepositoryTest`)
- **AtendimentoRepositoryTest**: Testa queries e operações de banco para atendimentos
- **PetsRepositoryTest**: Testa queries e operações de banco para pets

### 4. Testes de Integração (`*IntegrationTest`)
- **PetshopApiIntegrationTest**: Testa fluxos completos da API com TestContainers

## 🚀 Como Executar os Testes

### Pré-requisitos
- Java 21+
- Maven 3.6+
- Docker (para TestContainers)

### Executar Todos os Testes
```bash
./run-tests.sh
```

### Executar Testes Específicos

#### Testes Unitários
```bash
mvn test -Dtest="*ServiceTest"
mvn test -Dtest="*ControllerTest"
```

#### Testes de Repositório
```bash
mvn test -Dtest="*RepositoryTest"
```

#### Testes de Integração
```bash
mvn test -Dtest="*IntegrationTest"
```

#### Teste Específico
```bash
mvn test -Dtest="JwtServiceTest"
mvn test -Dtest="AtendimentoControllerTest"
```

## 📊 Cobertura de Testes

Os testes cobrem:

### ✅ Funcionalidades Testadas
- **Autenticação JWT**: Geração, validação e tratamento de erros
- **Controle de Acesso**: Verificação de permissões por role
- **CRUD Completo**: Create, Read, Update, Delete para todas as entidades
- **Validação de Dados**: Campos obrigatórios e formatos
- **Busca Avançada**: Filtros por múltiplos critérios
- **Segurança**: Autorização e autenticação
- **Tratamento de Erros**: Respostas adequadas para diferentes cenários

### 🎯 Cenários de Teste
- **Usuário Admin**: Acesso completo a todas as funcionalidades
- **Usuário Regular**: Acesso limitado baseado em permissões
- **Dados Inválidos**: Validação e tratamento de erros
- **Tokens Inválidos**: Segurança e autenticação
- **Recursos Não Encontrados**: Tratamento de 404
- **Permissões Insuficientes**: Tratamento de 403

## 🔧 Configuração dos Testes

### TestContainers
Os testes de integração usam TestContainers com PostgreSQL para simular um ambiente real:
- Banco de dados isolado para cada teste
- Dados limpos entre execuções
- Configuração automática

### MockMvc
Testes de controllers usam MockMvc para simular requisições HTTP:
- Testa endpoints completos
- Valida respostas JSON
- Simula autenticação

### @DataJpaTest
Testes de repositório usam @DataJpaTest para testar queries:
- Banco de dados em memória
- Transações automáticas
- Rollback após cada teste

## 📈 Relatórios

Após executar os testes, os relatórios são gerados em:
- **Cobertura**: `target/site/jacoco/index.html`
- **Testes**: `target/surefire-reports/`

## 🐛 Solução de Problemas

### Erro de Conexão com Banco
```bash
# Verificar se o Docker está rodando
docker ps

# Verificar logs do TestContainer
mvn test -X
```

### Erro de Compilação
```bash
# Limpar e recompilar
mvn clean compile

# Verificar versão do Java
java -version
```

### Testes Falhando
```bash
# Executar com logs detalhados
mvn test -X

# Executar teste específico com debug
mvn test -Dtest="NomeDoTeste" -X
```

## 📝 Adicionando Novos Testes

### Estrutura Recomendada
```
src/test/java/com/petshop/
├── controller/
│   └── NovoControllerTest.java
├── service/
│   └── NovoServiceTest.java
├── repository/
│   └── NovoRepositoryTest.java
└── integration/
    └── NovoIntegrationTest.java
```

### Convenções
- Nome da classe: `NomeDaClasseTest`
- Métodos de teste: `metodo_Cenario_ResultadoEsperado`
- Uso de `@ExtendWith(MockitoExtension.class)` para testes unitários
- Uso de `@WebMvcTest` para testes de controller
- Uso de `@DataJpaTest` para testes de repositório
- Uso de `@SpringBootTest` para testes de integração

## 🎯 Objetivos dos Testes

1. **Garantir Qualidade**: Código funcionando conforme especificado
2. **Detectar Regressões**: Mudanças que quebram funcionalidades existentes
3. **Documentar Comportamento**: Testes servem como documentação viva
4. **Facilitar Refatoração**: Confiança para melhorar o código
5. **Validar Integração**: Componentes trabalhando juntos corretamente

## 📚 Recursos Adicionais

- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [TestContainers](https://www.testcontainers.org/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
