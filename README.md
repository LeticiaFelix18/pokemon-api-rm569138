# Pokemon API

Projeto individual do Checkpoint 6 de Java — FIAP.  
RM: **569138**

API para cadastrar, consultar, atualizar e excluir Pokémon, desenvolvida com Java 25, Spring Boot, Maven e Oracle JDBC.

## Usar a Pokédex

Abra [Minha Pokédex](https://pokemon-api-rm569138.onrender.com/) para visualizar a coleção com imagens. Use **Adicionar Pokémon** para cadastrar, o lápis para editar e o botão de exclusão para remover um registro após confirmar. A busca filtra pelo nome ou código; o seletor filtra pela categoria.

O front está em `src/main/resources/static`, com HTML, CSS e JavaScript. Ele consome a Pokemon API usando `fetch`. As imagens vêm da [PokéAPI](https://pokeapi.co/), buscadas pelo nome da espécie; os dados das capturas permanecem no Oracle. Nomes sem imagem correspondente continuam podendo ser cadastrados.

## Usar no Insomnia

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

A data da captura deve estar no passado. Nome e categoria são obrigatórios; altura e peso devem ser positivos. Dados inválidos retornam **400**. Busca ou exclusão de um código inexistente retornam **404**.

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
