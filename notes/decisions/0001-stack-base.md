# ADR 0001 — Stack base: Java 21, Maven, Spring Boot

- **Status:** aceito
- **Data:** 2026-10-01

## Contexto
Projeto de aprendizado com foco em vagas Java em bancos e empresas grandes.
A stack deve refletir o que esses ambientes realmente usam.

## Decisão

**Java 21 (LTS)**
- LTS = Long-Term Support. Empresas só usam versões LTS (8, 11, 17, 21, 25).
- 21 é a versão mais adotada hoje em ambientes corporativos; traz records,
  pattern matching, sealed classes e virtual threads.
- Java 25 (LTS mais nova) seria válida, mas ainda é minoria em banco; 21 te
  prepara pro que você vai ver no trabalho, e migrar 21→25 depois é trivial.

**Maven** como build tool
- Domina em banco/corporativo. Gradle é mais flexível e rápido, mas Maven é
  declarativo e previsível — exatamente o que empresa conservadora valoriza.

**Spring Boot 4.1** como framework
- Padrão de fato do Java corporativo no Brasil e fora. Saber Spring é requisito
  em quase toda vaga Java backend.

**Monolito modular** (não microserviços) no início
- Microserviço resolve problema de organização de times, não de código.
  Começar com um monolito bem dividido em módulos ensina os limites de domínio;
  se um dia fizer sentido separar, os módulos viram serviços.

## Alternativas consideradas
- Gradle — bom, mas menos comum no alvo
- Quarkus / Micronaut — modernos, mas nicho comparado ao Spring
- Microserviços desde o dia 1 — complexidade operacional sem benefício real aqui

## Consequências
- Precisa instalar JDK 21 + Maven (ou usar o Maven Wrapper `mvnw`, que vem no projeto)
- Toda a documentação/tutoriais de Spring se aplica diretamente
