# Arquitetura — mapa do projeto

O esqueleto completo já existe em `src/`. Cada arquivo ainda vazio tem um
comentário `// Fase N — ...` dizendo **o que vai ser** e **quando** vamos
escrevê-lo. Este documento é o mapa: leia antes de abrir os arquivos.

## A ideia em uma frase
**Pacotes por feature** (`customer`, `account`, `transaction`, `auth`), e dentro
de cada feature, **4 camadas** com responsabilidades separadas (ADR 0002).

## As 4 camadas

| Camada | O que faz | Pode depender de | Exemplo |
|---|---|---|---|
| `domain/` | Regras de negócio. Java puro, **sem Spring/JPA** | só `shared/domain` | `Account.withdraw()` lança `InsufficientFundsException` |
| `application/` | Casos de uso: orquestra domínio + repositório, abre transação | `domain` | `TransactionService.transfer()` |
| `api/` | HTTP: recebe JSON, valida, devolve JSON | `application` | `POST /transfers` |
| `infra/` | Detalhes técnicos: banco de dados (JPA), segurança | `domain` | `AccountEntity`, `JpaAccountRepository` |

Regra de ouro: **as setas apontam para dentro, para o domínio**. O domínio não
sabe que existe HTTP nem Postgres. Isso será verificado automaticamente pelo
`ArchitectureTest` (ArchUnit) na Fase 2.

## Caminho de uma requisição (ex.: transferência)

```
Cliente HTTP
   │  POST /transfers  {"from": "...", "to": "...", "amount": "50.00"}
   ▼
TransferRequest         (api)          JSON → objeto Java, validado
   ▼
TransactionController   (api)          só traduz HTTP → chamada de método
   ▼
TransactionService      (application)  @Transactional: tudo ou nada
   │   1. busca as duas contas         ── AccountRepository (porta, domain)
   │   2. origem.withdraw(valor)                     │
   │   3. destino.deposit(valor)                     ▼
   │   4. grava Transaction + LedgerEntry   JpaAccountRepository (infra)
   ▼                                                 ▼
TransactionResponse     (api)                    PostgreSQL
   ▼
Cliente HTTP  ← 201 Created
```

Se qualquer passo falhar (ex.: saldo insuficiente), o `@Transactional` desfaz
tudo e o `GlobalExceptionHandler` transforma a exceção em um erro HTTP legível.

## Por que três arquivos para persistir uma coisa?
Em `infra/` cada feature tem:
- `XxxEntity` — como a linha é gravada na tabela (anotações JPA)
- `XxxJpaRepository` — interface do Spring Data (o Spring gera o SQL)
- `JpaXxxRepository` — **adaptador** que implementa a porta do domínio
  (`XxxRepository`) e converte `Entity ↔ objeto de domínio`

Parece burocracia, e é um pouco. O ganho: o domínio fica testável sem banco
e sem Spring (testes em milissegundos), e trocar a persistência não toca regra
de negócio. Em banco, onde as regras são o ativo mais valioso, essa troca vale.

## Features

| Feature | Fase | Responsabilidade |
|---|---|---|
| `shared` | 1–2 | `Money`, exceção base, tratamento global de erros |
| `customer` | 1–2 | Cadastro de clientes (com CPF validado) |
| `account` | 1–2 | Abertura de conta, saldo, status |
| `transaction` | 3 | Depósito, saque, transferência, ledger (partidas dobradas), idempotência |
| `auth` | 4 | Login, JWT, Spring Security |

## Fora do `src/`
- `src/main/resources/db/migration/` — scripts Flyway (estrutura do banco versionada)
- `compose.yaml` — Postgres local (Fase 2)
- `Dockerfile` — imagem da aplicação (Fase 5)
- `.github/workflows/ci.yml` — pipeline (Fase 5); **não criado ainda**, porque um
  workflow vazio faria o GitHub Actions falhar a cada push

## Testes espelham o código
`src/test/java` tem a mesma estrutura de pacotes. Convenção:
- `*Test` — testes rápidos (unitários, `@WebMvcTest`)
- `*IT` — testes de integração com banco real (Testcontainers, Fase 5)

## Isso é um plano, não um contrato
Nomes e arquivos vão mudar quando o código ensinar algo que o plano não previu.
Quando mudar, atualizamos este mapa.
