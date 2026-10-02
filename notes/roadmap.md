# Roadmap

Cada fase entrega algo funcionando **e** ensina um conjunto de tópicos que o
mercado corporativo/bancário cobra. A ordem importa: cada fase usa a anterior.

## Fase 0 — Fundação
- Instalar JDK, IDE, build tool
- Projeto Spring Boot "hello world" rodando e com um teste passando
- **Aprende:** JDK vs JRE, build com Maven, estrutura de projeto, ciclo de vida do build

## Fase 1 — Domínio: contas
- Cliente, Conta, saldo. Abrir conta, consultar saldo
- **Aprende:** modelagem de domínio, `BigDecimal` (nunca `double` pra dinheiro!),
  imutabilidade, records, testes unitários com JUnit 5

## Fase 2 — API REST + persistência
- Expor contas via HTTP, salvar em PostgreSQL
- **Aprende:** Spring Web, validação, tratamento de erros, JPA/Hibernate, migrations (Flyway)

## Fase 3 — Transações (o coração de um banco)
- Depósito, saque, transferência entre contas
- **Aprende:** transações ACID, `@Transactional`, concorrência (locking otimista/pessimista),
  idempotência, ledger (partidas dobradas)

## Fase 4 — Segurança
- Autenticação e autorização
- **Aprende:** Spring Security, JWT/OAuth2, princípios de segurança em fintech

## Fase 5 — Produção
- Docker, observabilidade (logs, métricas), CI no GitHub Actions
- **Aprende:** containers, testes de integração com Testcontainers, pipeline

## Fase 6+ — Evolução
- Mensageria (Kafka) para eventos de transação, extração de serviços, auditoria
- Só faz sentido depois do resto estar sólido
