### Minesweeper

Primeira versão do README, dedicada a utilização do repositório.

Este é um projeto Maven simples com a seguinte estrutura:

```sh
$ tree --gitignore
.
├── pom.xml
├── README.md
└── src
    ├── main
    │   └── java
    │       └── org
    │           └── minesweeper
    │               └── Minesweeper.java
    └── test
        └── java
            └── org
                └── minesweeper
                    └── MinesweeperTest.java
```

O arquivo `pom.xml` é a raiz do projeto. No momento temos 3 plugins e uma depêndencia:

- `maven-compiler-plugin`: O plugin básico do Maven, que explica ao Maven como compilar o projeto. Vamos compilar de Java 17 para Java 17.

- `maven-jar-plugin`: Um plugin para gerar um .jar como saída do `mvn package`. No momento a classe principal é `org.minesweeper.Minesweeper`.

- `maven-surefire-plugin`: O plugin de test running. Procura as classes de teste e as executa adequadamente. Seus reports ficam em `target/surefire-reports`.

- `junit-jupiter`: Depêndencia que adicionar o maquinário necessário para escrever os casos de teste.

Ao longo da construção é possível que alguma dessas coisas sejam modificadas.

### Escrevendo testes de unidades

Classes de teste devem ficar no path análogo ao que eles se encontram em `main.java`, só que em `test`. Eles devem SEMPRE serem nomeados como `<nome-original>Test.java`.

As marcações de teste são as mesmas que as usadas no arquivo de exemplo. Leiam a documentação do JUnit caso necessitem de formas mais poderosas de testas.

Ademais, segue as políticas quanto a criação de código:

- Códigos sem testes unitários não serão aceitos em pull requests.
- Códigos sem documentação não serão aceitos em pull requests.
- Códigos que falhem em seus próprios testes unitários não serão aceitos.
- Códigos sem uma cobertura adequada de teste será enviado para revisão.
- Testes unitários não devem ser removidos nem alterados sem justificativas. Adicionar mais testes é bem vindo, mas seja sucinto e não adicione boilerplate a suíte de testes.
- Pull requests que sujem a árvore de commits com arquivos desnecessários serão recusados.

### Usando o Maven

Para compilar o projeto em um .jar, use:

```sh
mvn clean package
```

Isso gera o arquivo `target/MineSweeper-1.0.jar`, que pode ser executado com:

```sh
java -jar target/MineSweeper-1.0.jar
```

A suíte de testes por sua vez pode ser executada por completo usando:

```sh
mvn test
```

Também é possivel especificar qual arquivo de testes deve ser executado:

```sh
mvn -Dtest=MineSweeperTest test
```
