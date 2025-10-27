# PetShop API - Guia de Instalação e Execução

## Pré-requisitos

### 1. Java Development Kit (JDK)
- **Versão necessária:** Java 21
- **Download:** [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) ou [OpenJDK 21](https://jdk.java.net/21/)
- **Verificação:** Execute `java -version` no terminal para confirmar a instalação

### 2. Apache Maven
- **Versão recomendada:** Maven 3.8+ ou superior
- **Download:** [Apache Maven](https://maven.apache.org/download.cgi)
- **Configuração:** Adicione o Maven ao PATH do sistema
- **Verificação:** Execute `mvn -version` no terminal

### 3. PostgreSQL
- **Versão recomendada:** PostgreSQL 12+ ou superior
- **Download:** [PostgreSQL](https://www.postgresql.org/download/)
- **Configuração:** Instale e configure o PostgreSQL com as seguintes credenciais:
  - **Host:** localhost
  - **Porta:** 5432
  - **Usuário:** postgres
  - **Senha:** 123
  - **Banco de dados:** pets_db

## Instalação e Configuração

### 1. Clone o Repositório
```bash
git clone <url-do-repositorio>
cd petshopAPI
```

### 2. Configuração do Banco de Dados

#### Criar o banco de dados:
```sql
-- conecte-se ao banco criando o banco com base no arquivo estrutura,db na raiz do projeto

```

#### Para testes (opcional):
```sql
CREATE DATABASE petshop_test;
CREATE USER test WITH PASSWORD 'test';
GRANT ALL PRIVILEGES ON DATABASE petshop_test TO test;
```

### 3. Instalação das Dependências
```bash
mvn clean install
```

## ▶️ Executando a Aplicação

### 1. Execução via Maven
```bash
mvn spring-boot:run
```

### 2. Execução via JAR
```bash
# Primeiro, compile o projeto
mvn clean package

# Execute o JAR gerado
java -jar target/petshop-api-0.0.1-SNAPSHOT.jar
```

### 3. Execução via IDE
- Importe o projeto como projeto Maven
- Execute a classe principal: `com.petshop.PetshopApiApplication`

## 🌐 Acesso à API

- **URL Base:** http://localhost:8081
- **Swagger UI:** http://localhost:8081/swagger-ui.html (se configurado)
- **Health Check:** http://localhost:8081/actuator/health

## Executando Testes

### Executar todos os testes:
```bash
mvn test
```

### Executar testes específicos:
```bash
mvn test -Dtest=NomeDaClasseTest
```

### Executar testes com cobertura:
```bash
mvn test jacoco:report
```

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/petshop/
│   │   ├── config/          # Configurações (Security, Jackson)
│   │   ├── controller/      # Controllers REST
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Tratamento de exceções
│   │   ├── model/          # Entidades JPA
│   │   ├── repository/     # Repositórios JPA
│   │   └── service/        # Serviços de negócio
│   └── resources/
│       ├── application.properties
│       └── logback-spring.xml
└── test/
    ├── java/com/petshop/
    └── resources/
        └── application-test.properties
```

## 🔧 Configurações Importantes

### application.properties
- **Porta:** 8081
- **Banco:** PostgreSQL (pets_db)
- **JPA:** Hibernate com DDL auto-update
- **Logging:** DEBUG para desenvolvimento

### Variáveis de Ambiente (Opcional)
Você pode sobrescrever as configurações usando variáveis de ambiente:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pets_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=123
export SERVER_PORT=8081
```

## Dependências Principais

- **Spring Boot:** 3.3.4
- **Spring Security:** Para autenticação e autorização
- **Spring Data JPA:** Para persistência
- **PostgreSQL Driver:** Para conexão com banco
- **JWT (jjwt):** Para tokens de autenticação
- **Validation:** Para validação de dados
- **TestContainers:** Para testes de integração

## Autenticação

A API utiliza JWT para autenticação. Para obter um token:

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"seu_usuario","password":"sua_senha"}'
```

Use o token retornado no header `Authorization: Bearer <token>` para acessar endpoints protegidos.

## Logs

Os logs são salvos em:
- **Console:** Saída padrão
- **Arquivo:** `logs/petshop-api.log`

Níveis de log configurados:
- `com.petshop`: DEBUG
- `org.springframework.web`: DEBUG
- `com.petshop.controller.PetsController`: DEBUG
