# Pokemon API

Projeto individual do Checkpoint 6 de Java — FIAP.  
RM: **569138**

API para cadastrar, consultar, atualizar e excluir Pokémon, seguindo os exercícios das aulas A39–A44. Usa Java 25, Spring Boot, Maven e Oracle JDBC.

## Organização

- `to`: dados do Pokémon e Bean Validation.
- `resource`: requisições HTTP, respostas e CorsFilter.
- `bo`: regra da data de captura no passado.
- `dao`: comandos SQL e ConnectionFactory.
- `static`: front-end com HTML, CSS e JavaScript.

## Usar a Pokédex

Abra [Minha Pokédex](https://pokemon-api-rm569138.onrender.com/) para visualizar a coleção com imagens. Use **Adicionar Pokémon** para cadastrar, o lápis para editar e o botão de exclusão para remover um registro após confirmar. A busca filtra pelo nome ou código; o seletor filtra pela categoria.

O front está em `src/main/resources/static` e consome `/pokemon` usando `fetch`, na própria aplicação. As imagens vêm da [PokéAPI](https://pokeapi.co/), pelo nome da espécie. São um complemento visual; os dados das capturas permanecem no Oracle. Nomes sem imagem correspondente continuam podendo ser cadastrados.

## API

O front permite executar o CRUD pelo navegador. O Insomnia também pode ser usado para testar os métodos HTTP.

Endereço da API:

```text
https://pokemon-api-rm569138.onrender.com/pokemon
```

Crie uma requisição HTTP, escolha o método e informe o endereço correspondente:

| Método | Endereço | Operação | Sucesso |
|---|---|---|---|
| GET | /pokemon | Listar Pokémon | 200 |
| GET | /pokemon/{codigo} | Buscar por código | 200 |
| POST | /pokemon | Cadastrar | 201 |
| PUT | /pokemon/{codigo} | Atualizar | 201 |
| DELETE | /pokemon/{codigo} | Excluir | 204 |

Os caminhos da tabela são relativos a `https://pokemon-api-rm569138.onrender.com`. Substitua `{codigo}` pelo código retornado no cadastro.

Para POST e PUT, selecione **Body → JSON** e preencha:

```json
{
  "nome": "Bulbasaur",
  "altura": 0.7,
  "peso": 6.9,
  "categoria": "Semente",
  "dataDaCaptura": "2026-10-01"
}
```

A data da captura deve ser anterior a hoje, conforme a A40. Hoje e datas futuras retornam **400**. Nome e categoria são obrigatórios; altura e peso devem ser positivos. Busca ou exclusão de um código inexistente retornam **404**. O PUT retorna **201**, conforme a A43.

O código é gerado pelo Oracle no POST. Cada envio de POST cria um novo cadastro. A exclusão bem-sucedida retorna **204**, sem corpo na resposta.

A API está publicada no Render; não é necessário executar o IntelliJ para utilizá-la. Se a primeira chamada demorar, aguarde a inicialização da instância gratuita.

## Executar localmente

1. Abra o projeto no IntelliJ com **JDK 25** e carregue as dependências Maven.
2. Execute `sql/01-criar-tabela.sql` no Oracle uma vez, caso a tabela ainda não exista.
3. Configure as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD` com os dados da sua conexão Oracle. Não coloque a senha no código.
4. Execute `PokemonApplication`.

O endereço local padrão é `http://localhost:8080/pokemon`. Para usar a porta 8081, configure `PORT=8081`.

Para executar os testes no Windows:

```powershell
.\mvnw.cmd clean verify
```
