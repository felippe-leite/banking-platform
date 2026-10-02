# JDK, JRE, JVM — o que é cada um

Pergunta clássica de entrevista.

- **JVM (Java Virtual Machine):** a máquina que *executa* bytecode (`.class`).
  É por isso que Java é "write once, run anywhere": você compila pra bytecode,
  e a JVM de cada sistema operacional roda esse bytecode.
- **JRE (Java Runtime Environment):** JVM + bibliotecas padrão. O suficiente pra
  *rodar* um programa Java. (Desde o Java 11 não é mais distribuído separado.)
- **JDK (Java Development Kit):** JRE + ferramentas de desenvolvimento
  (`javac` compilador, `jar`, `jshell`, `jdb`...). É o que você instala pra *programar*.

Fluxo: `Conta.java` --(javac)--> `Conta.class` (bytecode) --(JVM)--> execução.

A JVM também faz **JIT (Just-In-Time compilation)**: identifica trechos "quentes"
do código e os compila pra código de máquina nativo em tempo de execução. Por isso
uma aplicação Java fica mais rápida depois de "aquecer".

## Distribuições
Java é especificação aberta (OpenJDK). Várias empresas distribuem builds:
Eclipse Temurin, Amazon Corretto, Azul Zulu, Oracle JDK... Pra nós, **Temurin**
é uma escolha neutra e gratuita.
