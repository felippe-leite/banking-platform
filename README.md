# Banking Platform

Projeto de aprendizado em Java 21 e Spring Boot. A etapa atual oferece cadastro
de clientes, abertura de contas e consultas via REST.

## Executar

```bash
./mvnw spring-boot:run
```

A API usa o perfil padrão `in-memory`: os dados são perdidos ao reiniciar.
PostgreSQL, movimentações e segurança ainda serão implementados.

## Banco local — preparação da persistência

Com Docker e o plugin Docker Compose instalados:

```bash
docker compose up -d --wait postgres
docker compose exec postgres psql -U banking -d banking
```

O banco fica em `localhost:5432`, database e usuário `banking`, senha local
`banking_local`. O volume `postgres-data` mantém os dados ao recriar o container.
Para parar sem apagar dados, use `docker compose down`.

As migrations `V1__create_customer.sql` e `V2__create_account.sql` preparam
clientes e contas com unicidade, vínculo de titularidade e saldo decimal.
Neste incremento elas ainda não são executadas automaticamente: a integração
Flyway/JPA será o próximo passo. A API continua usando o perfil `in-memory`.
O arquivo V3 ainda é um esqueleto reservado para a fase de transações.

## Endpoints

| Método | Caminho | Resultado |
| --- | --- | --- |
| POST | /customers | Cadastra cliente; 201 com Location |
| GET | /customers/{id} | Consulta cliente |
| POST | /accounts | Abre conta; 201 com Location |
| GET | /accounts/{id} | Consulta conta |
| GET | /accounts/{id}/balance | Consulta saldo |

Cadastro de cliente:

```bash
curl -i http://localhost:8080/customers \
  -H 'Content-Type: application/json' \
  -d '{"name":"Felippe","cpf":"52998224725","birthDate":"1995-05-20"}'
```

Copie o `id` retornado para `customerId` na abertura:

```json
{"customerId":"UUID-DO-CLIENTE","branch":"0001","number":"123456","digit":"7"}
```

Envie esse JSON por POST a `/accounts`. Agência tem 4 dígitos, número 6 e
dígito 1; use strings para preservar zeros iniciais. O saldo retorna como número
JSON decimal. Contas novas ficam ACTIVE com saldo zero.

Erros retornam `code`, `message` e `timestamp`: 400 para dados inválidos,
404 para cadastro inexistente e 409 para CPF ou número de conta duplicado.

## Verificar

```bash
./mvnw verify
```

Os testes HTTP usam MockMvc com controllers, serviços e repositórios em memória
reais, sem abrir uma porta de rede.
