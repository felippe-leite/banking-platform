# Learning log

## 2026-10-01 — Sessão 1: kickoff
**Feito**
- Definido objetivo: plataforma bancária em Java, foco em mercado corporativo/bancário
- Criada a pasta `notes/` (roadmap, ADRs, conceitos)
- Proposta a stack base → `decisions/0001-stack-base.md`

**Estado do ambiente**
- Sem JDK, Maven ou Docker instalados ainda

**Próximo passo**
- Aprovar (ou questionar!) o ADR 0001
- Instalar JDK 21 via SDKMAN
- Gerar o projeto Spring Boot

**Para estudar**
- `concepts/jdk-jre-jvm.md`

## 2026-10-01 — Sessão 2: Fase 0 (fundação)
**Feito**
- ADR 0001 aceito
- SDKMAN instalado + JDK 21 (Eclipse Temurin 21.0.12)
- Projeto gerado no Spring Initializr: Spring Boot 4.1.1, Maven, pacote `com.felippe.banking`,
  starters: webmvc, validation, actuator
- `./mvnw verify` passou; app sobe e `/actuator/health` responde `UP`
- `.idea/` removido do versionamento (continua no disco, agora no `.gitignore`)

**Comandos úteis**
- `./mvnw spring-boot:run` → sobe a app em http://localhost:8080
- `./mvnw test` → roda os testes
- `curl localhost:8080/actuator/health`

**Para estudar**
- `concepts/anatomia-projeto-spring-boot.md`

**Próximo passo**
- Primeiro commit da fundação
- Fase 1: modelar Conta e Cliente, entender por que `BigDecimal` pra dinheiro
