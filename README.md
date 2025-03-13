# 🚀 Projeto de Testes com Java e Spring Boot

Este repositório foi criado para aprimorar conhecimentos em testes com **Java** e **Spring Boot**. Durante o desenvolvimento, foram exploradas diversas bibliotecas e ferramentas para garantir a qualidade e confiabilidade do código.

## 📌 Tecnologias Utilizadas

- ✅ **Java 17** - Linguagem de programação
- ✅ **Maven** - Gerenciador de dependências
- ✅ **Spring Boot** - Framework para criação de aplicações Java
- ✅ **H2 Database** - Banco de dados em memória
- ✅ **PostgreSQL** - Banco de dados relacional
- ✅ **Flyway** - Controle de versão do banco de dados
- ✅ **Lombok** - Reduz a verbosidade do código
- ✅ **Junit 5** - Framework de testes unitários
- ✅ **AssertJ** - Assertions fluentes e expressivos
- ✅ **Mockito** - Mocks para testes de integração
- ✅ **Hamcrest** - Biblioteca para asserts mais legíveis
- ✅ **RestAssured** - Facilita testes de APIs REST
- ✅ **Testcontainers** - Banco de dados dinâmico para testes de integração
- ✅ **Jacoco** - Cobertura de testes
- ✅ **SpringDoc** - Documentação automática da API

## 📁 Estrutura do Projeto

```
/src
  /main
    /java
      /io/github/diegorscs
  /test
    /java
      /io/github/diegorscs
        /integration   # Testes de integração
        /unit          # Testes unitários
```

## 🛠️ Como Executar os Testes

### Com Maven:

```sh
./mvnw test
```

## 🏗️ Testes de Integração com Testcontainers

Os testes de integração utilizam **Testcontainers** para criar um ambiente isolado com **PostgreSQL**, garantindo que os testes sejam confiáveis e reproduzíveis.

Exemplo de configuração:

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");
```

## 🧪 Testes com BDD

Os testes seguem **Behavior-Driven Development (BDD)** para descrever cenários de forma intuitiva, tornando-os mais legíveis e fáceis de manter. Utilizamos **RestAssured** e **AssertJ** para facilitar esse processo.

Exemplo de teste BDD:

```java
@Test
void shouldReturnListPersons_whenFindAll() {
    given()
        .when().get()
        .then()
        .statusCode(200)
        .body("size()", greaterThan(0));
}
```

## 🔥 Considerações Finais

Este projeto foi fundamental para consolidar conceitos de **testes unitários**, **testes de integração** e **boas práticas** para garantir a qualidade do software. Feedbacks e contribuições são bem-vindos! 🚀

---

Feito por [Diego Ruescas](https://www.linkedin.com/in/diegoru/) 👨‍💻
