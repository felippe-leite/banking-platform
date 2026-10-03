# ADR 0002 — Pacotes por feature, código em inglês

- **Status:** aceito
- **Data:** 2026-10-01

## Contexto
Antes de escrever as primeiras classes de domínio, precisamos definir como
organizar os pacotes e em que idioma nomear o código. Essas decisões são caras
de mudar depois, porque cada classe nova segue o padrão das anteriores.

## Decisão

**Pacotes por feature, com camadas dentro**
```
com.felippe.banking
├── shared/domain/       value objects usados por várias features (ex.: Money)
├── customer/
│   ├── domain/          regras de negócio — Java puro, sem Spring
│   ├── application/     casos de uso (orquestram o domínio)
│   ├── api/             controllers REST, DTOs
│   └── infra/           persistência (JPA), integrações
└── account/
    └── (mesmas camadas)
```

- **Coesão:** tudo de uma feature fica junto; achar e mudar código é local.
- **Domínio isolado:** `domain/` não importa Spring, JPA nem HTTP. As regras de
  negócio ficam testáveis em milissegundos e não dependem de framework.
  É uma versão simplificada de arquitetura hexagonal / clean architecture.
- **Combina com o monolito modular do ADR 0001:** cada feature é um candidato
  natural a módulo (ou serviço) no futuro.
- Pastas só são criadas quando a primeira classe delas existir.

**Código em inglês; notas e conversas em português**
- Padrão do mercado corporativo, inclusive em bancos brasileiros.
- Abre portas para vagas internacionais e deixa o código consistente com o
  vocabulário do Java e do Spring (`Repository`, `Service`...).

## Alternativas consideradas
- **Pacotes por camada** (`controller/`, `service/`, `repository/`) — simples no
  começo, mas mistura features e não protege o domínio de dependências de framework.
- **Hexagonal completo** (ports/adapters explícitos) — mais cerimônia do que o
  projeto precisa agora; podemos evoluir pra lá se fizer sentido.
- **Código em português** — mais próximo da linguagem do negócio, mas mistura
  idiomas com o framework (`ContaRepository`, `getSaldo`).

## Consequências
- Regra a respeitar em code review: nada em `*/domain/` importa `org.springframework.*`
  ou `jakarta.persistence.*`.
- Termos de domínio têm tradução fixa: Cliente → `Customer`, Conta → `Account`,
  Saldo → `balance`, Dinheiro → `Money`, Depósito → `deposit`, Saque → `withdraw`,
  Transferência → `transfer`.
