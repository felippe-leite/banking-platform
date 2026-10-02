# Notes — banking-platform

Caderno do projeto. Tudo que a gente decidir ou aprender fica registrado aqui,
pra você (e eu, numa próxima sessão) retomar o contexto sem depender de memória.

## Estrutura

| Pasta/arquivo | Pra quê serve |
|---|---|
| `roadmap.md` | Fases do projeto e o que cada uma ensina |
| `learning-log.md` | Diário: o que foi feito em cada sessão e o que ficou de dúvida |
| `decisions/` | ADRs (Architecture Decision Records): uma decisão técnica por arquivo |
| `concepts/` | Explicações de conceitos (Java, Spring, banco, domínio bancário) |

## Por que ADR?

Em banco e em empresa grande, ninguém lembra *por que* algo foi feito de um jeito
dois anos depois. O ADR responde isso: contexto, decisão, alternativas, consequências.
É curto, imutável (se mudar de ideia, cria um ADR novo que "substitui" o antigo)
e fica versionado junto com o código. É uma prática que você vai encontrar no mercado.
