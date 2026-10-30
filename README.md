# Pokemon API

Checkpoint 6 de Java — FIAP · RM 569138

Cadastro de Pokémon com Java 25, Spring Boot, Maven e Oracle JDBC, baseado nas aulas A39–A44.

## Acessar

- [Abrir a Pokédex](https://pokemon-api-rm569138.onrender.com/)
- [Consultar a API](https://pokemon-api-rm569138.onrender.com/pokemon)

Na Pokédex, clique em **Adicionar Pokémon** para cadastrar. Use o **lápis** para editar e o **×** do cartão para excluir, confirmando a operação.

Preencha nome, altura, peso, categoria e data da captura. A captura deve ser anterior a hoje. Na primeira abertura, o Render gratuito pode levar alguns instantes para iniciar.

## Código do projeto

As classes ficam em `src/main/java/br/com/fiap`:

| Pacote | Função |
|---|---|
| `to` | Dados e validação do Pokémon |
| `resource` | Requisições HTTP e CORS |
| `bo` | Regra da data de captura |
| `dao` | Consultas SQL e conexão Oracle |

O front fica em `src/main/resources/static`: HTML, CSS e JavaScript. As imagens são consultadas pelo nome na PokéAPI; os cadastros ficam no Oracle.

## Arquivos de apoio

- `pom.xml`: configuração Maven e dependências.
- `cadastro.txt` e `atualizacao.txt`: exemplos de JSON.
- `criar-tabela.sql`: criação inicial da tabela Oracle.
- `Dockerfile`: execução no Render.
- `.mvn` e `mvnw`: arquivos do Maven Wrapper gerados para executar o build.

## Executar no IntelliJ

Abra o `pom.xml`, selecione **JDK 25** e configure `DB_URL`, `DB_USER` e `DB_PASSWORD` nas variáveis de ambiente. Execute `PokemonApplication`.

O endereço local é `http://localhost:8080/`. Se a porta estiver ocupada, configure `PORT=8081`. Não coloque a senha no código ou no GitHub.
