# Anatomia do projeto Spring Boot

```
pom.xml                         ← "receita" do projeto (Maven)
mvnw / mvnw.cmd / .mvn/         ← Maven Wrapper
src/main/java/...               ← código de produção
src/main/resources/             ← configuração (application.properties)
src/test/java/...               ← testes
target/                         ← saída do build (gerado, ignorado no git)
```

Essa estrutura (`src/main/java`, `src/test/java`) é a **convenção do Maven**.
"Convention over configuration": se você segue o padrão, não precisa configurar nada.

## pom.xml
POM = Project Object Model. Declara *o que* o projeto é, não *como* buildar.

- **`<parent>spring-boot-starter-parent`**: herda configuração pronta do Spring,
  principalmente as **versões de todas as dependências**. Por isso nossas
  dependências não têm `<version>`: o parent garante versões compatíveis entre si.
- **groupId / artifactId / version**: as "coordenadas" do projeto. `com.felippe:banking-platform:0.0.1-SNAPSHOT`.
  `SNAPSHOT` = versão em desenvolvimento, ainda não lançada.
- **Starters**: pacotes de dependências agrupadas por funcionalidade.
  - `starter-webmvc`: servidor HTTP (Tomcat embutido) + Spring MVC + Jackson (JSON)
  - `starter-validation`: Bean Validation (`@NotNull`, `@Positive`...)
  - `starter-actuator`: endpoints operacionais (`/actuator/health`). Em banco,
    health check é obrigatório: é por ele que a infraestrutura sabe se a app está viva.
  - `*-test` com `<scope>test</scope>`: só entram no classpath dos testes, não vão pro jar final.
- **spring-boot-maven-plugin**: empacota tudo num **fat jar** executável
  (seu código + todas as dependências + Tomcat). Roda com `java -jar`.

## Maven Wrapper (mvnw)
Um script que baixa a versão exata do Maven definida em
`.mvn/wrapper/maven-wrapper.properties`. Todo mundo do time e o CI usam a mesma
versão, sem ninguém precisar instalar Maven. Use sempre `./mvnw`, não `mvn`.

## Ciclo de vida do Maven
Fases em ordem; rodar uma executa todas as anteriores:

`validate → compile → test → package → verify → install → deploy`

- `./mvnw test`: compila e roda os testes
- `./mvnw package`: gera o jar em `target/`
- `./mvnw verify`: package + verificações extras (o que o CI costuma rodar)
- `./mvnw spring-boot:run`: sobe a aplicação direto do código

Dependências baixadas ficam em `~/.m2/repository` (cache local, compartilhado entre projetos).

## BankingPlatformApplication.java
- `main` é o ponto de entrada, como em qualquer programa Java.
- `@SpringBootApplication` junta três anotações:
  - `@Configuration`: a classe pode declarar beans
  - `@EnableAutoConfiguration`: o Spring olha o classpath e configura sozinho
    (viu Tomcat no classpath? sobe um servidor web)
  - `@ComponentScan`: procura componentes (`@Service`, `@RestController`...)
    **neste pacote e nos subpacotes**. Por isso todo o código deve ficar debaixo
    de `com.felippe.banking`.

## BankingPlatformApplicationTests
`@SpringBootTest` sobe o contexto inteiro do Spring. O teste `contextLoads()` vazio
parece inútil, mas falha se a configuração da aplicação estiver quebrada. É um smoke test.

## application.properties
Configuração externa (porta, banco, logs...). Mais tarde vamos usar **profiles**
(`application-dev.properties`, `application-prod.properties`) pra separar ambientes.
