# Dinheiro em Java: por que `BigDecimal` (e nunca `double`)

Pergunta clássica de entrevista em banco. Provas executáveis em
`src/test/java/com/felippe/banking/shared/domain/BigDecimalLearningTest.java`.

## 1. Por que `0.1 + 0.2 != 0.3` com `double`
`double` guarda números em **binário** (base 2), no padrão IEEE 754.
Assim como 1/3 não tem representação finita em decimal (0.3333...),
**0.1 não tem representação finita em binário** (0.0001100110011...).
O computador corta em algum ponto, e o valor guardado é *quase* 0.1.

Somando dois "quases", o resultado é `0.30000000000000004`.
Em 10 milhões de transações, esses centavos fantasmas viram dinheiro real
sumindo ou aparecendo, e auditoria de banco não perdoa.

**Resposta de entrevista:** "`double` é ponto flutuante binário; frações decimais
como 0.1 não têm representação exata em base 2, então há erro de arredondamento
que se acumula. Para dinheiro uso `BigDecimal`, que representa o número em base 10
com precisão exata."

## 2. Como `BigDecimal` funciona
Guarda dois inteiros: **unscaledValue** e **scale**.
`12.34` = unscaledValue `1234`, scale `2` (ou seja, 1234 × 10⁻²). Sem binário, sem perda.

## 3. Crie a partir de `String`, nunca de `double`
```java
new BigDecimal(0.1)      // 0.1000000000000000055511151231257827... ❌ herdou o erro
new BigDecimal("0.1")    // 0.1 ✅
BigDecimal.valueOf(0.1)  // 0.1 ✅ (usa Double.toString, que arredonda pra exibição)
```
O `double` já chegou errado; o construtor só preserva fielmente o erro.

## 4. `equals` vs `compareTo` — a pegadinha
```java
new BigDecimal("2.0").equals(new BigDecimal("2.00"))     // false! scale 1 ≠ scale 2
new BigDecimal("2.0").compareTo(new BigDecimal("2.00"))  // 0 → numericamente iguais
```
- `equals` compara **valor e scale**; `compareTo` compara só o valor.
- Consequência: `HashSet`/`HashMap` usam `equals`/`hashCode`, então 2.0 e 2.00
  viram **duas chaves diferentes**. Bug silencioso.
- Regra: para comparar valores, use `compareTo`. Ou, melhor, **normalize o scale**
  (no nosso `Money`, sempre 2 casas), aí `equals` passa a funcionar.

## 5. Divisão precisa de regra de arredondamento
`10 / 3` = 3.333... infinito. `BigDecimal` se recusa a adivinhar e lança
`ArithmeticException`. Você precisa dizer quantas casas e como arredondar:
```java
a.divide(b, 2, RoundingMode.HALF_EVEN)
```
O mesmo vale para `setScale(2)`: se precisar arredondar e você não disser como, lança exceção.

## Regras do projeto
1. Dinheiro é sempre `Money`, nunca `BigDecimal` solto nem `double`.
2. `Money` tem sempre scale 2; entrada com mais casas é rejeitada.
3. Nunca existe `Money.of(double)`.
