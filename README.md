# Pokémon API — Checkpoint 6

Projeto individual de Java da FIAP · **RM 569138**

API para cadastrar, consultar, atualizar e excluir Pokémon, com **Java 25, Spring Boot, Maven e Oracle JDBC**. O backend está organizado em TO, Resource, BO, DAO e ConnectionFactory, seguindo a estrutura utilizada nas aulas A39–A44.

A Pokédex em HTML, CSS e JavaScript permite utilizar a API pelo navegador.

## Acessar

| Acesso | Link |
|---|---|
| Pokédex — interface do sistema | [Abrir a Pokédex](https://pokemon-api-rm569138.onrender.com/) |
| API — lista em JSON | [Consultar os Pokémon](https://pokemon-api-rm569138.onrender.com/pokemon) |

A versão publicada funciona sem manter o IntelliJ aberto. Para apresentar em outra máquina, abra a Pokédex no navegador com acesso à internet. Após um período sem uso, o serviço gratuito pode demorar um pouco para responder.

O repositório é privado: para visualizar o código no GitHub, é necessário ter acesso a ele.

## Usar a Pokédex

1. Abra a Pokédex.
2. Clique em **Adicionar Pokémon**.
3. Preencha nome, altura, peso, categoria e data da captura.
4. Salve e confira o Pokémon na coleção.
5. Para editar, utilize o **lápis** do cartão e salve as alterações.
6. Para excluir, utilize o **×** do cartão e confirme a operação.

A busca permite encontrar Pokémon pelo nome ou código. Também existe um filtro por categoria.

Os cadastros ficam no Oracle. As imagens são consultadas na PokéAPI pelo nome: elas são um complemento da interface e não são gravadas na tabela. Um nome sem imagem disponível pode ser cadastrado normalmente.

## Campos e validações

| Campo | Regra | Exemplo |
|---|---|---|
| `codigo` | Gerado automaticamente pelo Oracle | `1` |
| `nome` | Obrigatório, até 100 caracteres | `Bulbasaur` |
| `altura` | Obrigatória e maior que zero, em metros | `0.7` |
| `peso` | Obrigatório e maior que zero, em quilogramas | `6.9` |
| `categoria` | Obrigatória, até 100 caracteres | `Semente` |
| `dataDaCaptura` | Obrigatória e anterior ao dia atual | `2026-10-01` |

**A data de hoje e datas futuras não são aceitas.** O projeto utiliza `@Past` no TO e a verificação da data no BO.

No JSON, a data usa o formato `AAAA-MM-DD` e os números decimais usam ponto. O nome exato do campo é `dataDaCaptura`.

## Endereços da API

Endereço base publicado: `https://pokemon-api-rm569138.onrender.com`

| Método | Caminho | Operação | Sucesso |
|---|---|---|---|
| GET | `/pokemon` | Listar todos | `200 OK` |
| GET | `/pokemon/{codigo}` | Consultar por código | `200 OK` |
| POST | `/pokemon` | Cadastrar | `201 Created` |
| PUT | `/pokemon/{codigo}` | Atualizar | `201 Created` |
| DELETE | `/pokemon/{codigo}` | Excluir | `204 No Content` |

O PUT retorna `201 Created` conforme o exemplo seguido na aula A43.

O navegador permite consultar os endereços GET diretamente. Para POST, PUT e DELETE, use os botões da Pokédex ou um cliente HTTP, como o Insomnia. **O Insomnia é opcional para usar o sistema pela interface.**

### Cadastrar — POST

Selecione **POST**, informe o endereço abaixo e configure o corpo como **JSON**, com o cabeçalho `Content-Type: application/json`.

```text
https://pokemon-api-rm569138.onrender.com/pokemon
```

Corpo da requisição:

```json
{
  "nome": "Bulbasaur",
  "altura": 0.7,
  "peso": 6.9,
  "categoria": "Semente",
  "dataDaCaptura": "2026-10-01"
}
```

O código não precisa ser enviado. A resposta de sucesso contém os dados e o código gerado pelo Oracle. Exemplo ilustrativo:

```json
{
  "codigo": 1,
  "nome": "Bulbasaur",
  "altura": 0.7,
  "peso": 6.9,
  "categoria": "Semente",
  "dataDaCaptura": "2026-10-01"
}
```

**Guarde o código retornado na sua resposta:** ele pode ser diferente de `1`. Cada envio de POST cria um novo registro, mesmo com os mesmos dados.

### Consultar — GET

Para listar todos:

```text
GET https://pokemon-api-rm569138.onrender.com/pokemon
```

O resultado é uma lista JSON. Se não houver cadastros, a resposta será `[]` com status `200 OK`.

Para consultar um cadastro específico, substitua `1` pelo código retornado no cadastro:

```text
GET https://pokemon-api-rm569138.onrender.com/pokemon/1
```

Se esse código não existir, a resposta será `404 Not Found`.

### Atualizar — PUT

Selecione **PUT**, use o código do Pokémon que deseja alterar e configure o corpo como JSON:

```text
PUT https://pokemon-api-rm569138.onrender.com/pokemon/1
```

```json
{
  "nome": "Ivysaur",
  "altura": 1.0,
  "peso": 13.0,
  "categoria": "Semente",
  "dataDaCaptura": "2026-10-01"
}
```

Envie todos os campos acima. O código vem do endereço da requisição. A atualização bem-sucedida retorna `201 Created` com os dados atualizados.

### Excluir — DELETE

Selecione **DELETE** e informe o código do cadastro que deseja remover:

```text
DELETE https://pokemon-api-rm569138.onrender.com/pokemon/1
```

Não é necessário enviar JSON. O sucesso retorna `204 No Content`, sem conteúdo na resposta. Se o código não existir, retorna `404 Not Found`. Essa operação remove o registro do Oracle.

### Entender os status

| Status | Significado neste projeto |
|---|---|
| `200 OK` | Consulta realizada |
| `201 Created` | Cadastro ou atualização realizada |
| `204 No Content` | Exclusão realizada, sem corpo na resposta |
| `400 Bad Request` | Dados inválidos, JSON incorreto ou falha ao cadastrar/atualizar; o PUT também retorna esse status se não atualizar um registro |
| `404 Not Found` | Código não encontrado na consulta ou exclusão |
| `500 Internal Server Error` | Falha interna ao consultar ou excluir, como problema de conexão com o Oracle |

## Organização do código

As classes Java ficam em `src/main/java/br/com/fiap`:

| Classe | Pacote | Responsabilidade |
|---|---|---|
| `PokemonApplication` | `br.com.fiap` | Inicia a aplicação Spring Boot |
| `PokemonTO` | `to` | Representa os dados e declara as validações com Bean Validation |
| `PokemonResource` | `resource` | Recebe as requisições HTTP e devolve dados e status |
| `CorsFilter` | `resource` | Configura o acesso pelo navegador via CORS |
| `PokemonBO` | `bo` | Aplica a regra da data de captura e chama o DAO |
| `PokemonDAO` | `dao` | Executa SQL com JDBC e PreparedStatement |
| `ConnectionFactory` | `dao` | Abre a conexão Oracle com variáveis de ambiente |

Fluxo de uma operação:

```text
Navegador ou cliente HTTP
        ↓
PokemonResource → PokemonBO → PokemonDAO → Oracle
```

O `PokemonTO` transporta os dados entre as camadas. O DAO utiliza a `ConnectionFactory` para obter a conexão.

| Arquivo ou pasta | Conteúdo |
|---|---|
| `src/main/resources/static` | Interface: `index.html`, `styles.css` e `app.js` |
| `src/main/resources/application.properties` | Nome da aplicação, porta e configurações do servidor |
| `src/test/java/br/com/fiap/PokemonApplicationTests.java` | Teste de inicialização do contexto Spring |
| `criar-tabela.sql` | Criação inicial da tabela `DDD_POKEMONS` |
| `pom.xml` | Versão do Java e dependências Maven |
| `Dockerfile` | Construção e execução no Render |
| `.mvn`, `mvnw` e `mvnw.cmd` | Maven Wrapper para executar o build sem instalar Maven separadamente |

Os exemplos de cadastro e atualização estão neste README, sem arquivos de exemplo separados.

## Executar na própria máquina

### Requisitos

- JDK **25**.
- IntelliJ IDEA ou terminal para executar o projeto.
- Internet para baixar as dependências na primeira execução e acessar o Oracle.
- Credenciais válidas do banco Oracle.

### Preparar o banco

Se a tabela ainda não existir, abra o SQL Developer, conecte-se ao seu usuário Oracle e execute o arquivo [criar-tabela.sql](criar-tabela.sql) uma única vez.

Se o banco já estiver preparado, não é necessário recriar a tabela. O SQL Developer é uma ferramenta de administração e não precisa ficar aberto para a API funcionar.

### Configurar o ambiente

| Variável | Valor |
|---|---|
| `DB_URL` | `jdbc:oracle:thin:@//oracle.fiap.com.br:1521/ORCL` |
| `DB_USER` | Seu usuário Oracle, por exemplo `rm569138` |
| `DB_PASSWORD` | Sua senha Oracle |
| `PORT` | Opcional; o padrão local é `8080` |

A conexão acima utiliza **nome de serviço ORCL**. Configure a senha nas variáveis de ambiente da execução. Não coloque a senha nos arquivos do projeto ou no GitHub.

### Executar pelo IntelliJ

1. Abra a pasta do projeto ou o `pom.xml` como projeto Maven.
2. Selecione o **JDK 25** e aguarde o Maven carregar as dependências.
3. Abra a classe `PokemonApplication`.
4. Em **Run → Edit Configurations**, selecione a configuração de `PokemonApplication`.
5. Em **Modify options**, habilite **Environment variables**, caso o campo ainda não apareça.
6. Abra o editor desse campo e adicione `DB_URL`, `DB_USER` e `DB_PASSWORD` com os valores acima.
7. Salve a configuração e execute `PokemonApplication`.
8. Aguarde a inicialização e abra `http://localhost:8080/`.

A API local estará em `http://localhost:8080/pokemon`. Se a porta estiver ocupada, adicione `PORT=8081` nas variáveis e utilize `http://localhost:8081/` e `http://localhost:8081/pokemon`.

### Executar pelo terminal e verificar o build

Com o JDK 25 disponível e as variáveis do banco configuradas na mesma sessão, abra o terminal na pasta que contém o `pom.xml`.

No Windows PowerShell, para iniciar:

```powershell
.\mvnw.cmd spring-boot:run
```

Para compilar e executar o teste incluído:

```powershell
.\mvnw.cmd clean verify
```

Após o build, também é possível iniciar o arquivo gerado:

```powershell
java "-Duser.timezone=America/Sao_Paulo" -jar target/pokemon-0.0.1-SNAPSHOT.jar
```

Em Linux ou macOS, utilize `./mvnw` no lugar de `.\mvnw.cmd`.

O teste incluído verifica a inicialização do Spring. Para verificar a integração com o Oracle, consulte e cadastre um Pokémon com o banco configurado.

## Execução no Render

O serviço publicado utiliza o `Dockerfile` do repositório. As variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD` ficam no ambiente do serviço. A porta é recebida pela variável `PORT` fornecida pelo Render.

A interface e a API são servidas pela mesma aplicação. O JavaScript chama `/pokemon`, utilizando o servidor em que a página foi aberta, localmente ou no Render.

Alterações enviadas à branch conectada ao serviço podem iniciar uma nova publicação automática. Confira no painel do Render se a nova publicação foi concluída.

## Dúvidas comuns

**Preciso abrir o IntelliJ para apresentar a versão publicada?**

Não. Abra o link da Pokédex. Para mostrar o código, abra o projeto no IntelliJ ou no GitHub com uma conta que tenha acesso ao repositório.

**Preciso instalar o Insomnia?**

Não para usar a Pokédex. O Insomnia é uma alternativa para testar a API diretamente.

**Por que aparece `[]` na API?**

A consulta funcionou, mas não existem registros na tabela consultada.

**Por que não aceita a data de hoje?**

A validação exige uma captura anterior ao dia atual. Utilize uma data passada.

**Por que apareceu erro ao consultar Pokémon?**

Confira as variáveis do banco, as credenciais, a existência da tabela no usuário conectado e a conexão com o Oracle.

**O que mostrar se o professor pedir o código?**

Apresente o fluxo Resource → BO → DAO, o TO com suas validações e a ConnectionFactory. A interface fica em `src/main/resources/static`.
