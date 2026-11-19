# Atividade 20 - Arquitetura em camadas

Fluxo: `View -> Controller -> Service -> DAO -> banco`. Cada camada conhece só a interface da camada de baixo, recebida pelo construtor. O único lugar que usa `new` para montar as camadas é o `main` (`Prg04JoaoVictorBarretoPimentel`).

## Task 01 - Camadas por entidade

| Entidade | Service | Controller |
|---|---|---|
| Produto | `br.com.ifba.produto.service.ProdutoIService` / `ProdutoService` | `br.com.ifba.produto.controller.ProdutoIController` / `ProdutoController` |
| Categoria | `br.com.ifba.produto.service.CategoriaIService` / `CategoriaService` | `br.com.ifba.produto.controller.CategoriaIController` / `CategoriaController` |

## Task 02 - Regras de negócio no Service

Saíram da `TelaProdutos` e foram para o `ProdutoService`:
- nome obrigatório (e no máximo 120 caracteres);
- preço obrigatório e não negativo;
- estoque não negativo;
- edição e exclusão exigem um produto selecionado (id).

No `CategoriaService`: nome obrigatório, no máximo 80 caracteres e sem repetir o nome de outra categoria.

Violação de regra lança `RegraNegocioException`, que a tela mostra como aviso. A tela continua só convertendo o texto digitado em número.

## Task 03 - Injeção de dependência

- `ProdutoService(ProdutoIDao)` e `CategoriaService(CategoriaIDao)`
- `ProdutoController(ProdutoIService)` e `CategoriaController(CategoriaIService)`
- `TelaProdutos(ProdutoIController, TelaCategorias)` e `TelaCategorias(CategoriaIController)`

Busca por `new ...Dao`, `new ...Service` e `new ...Controller` dentro de `br.com.ifba`:

```
$ grep -rnE "new (Produto|Categoria)(Dao|Service|Controller)" src/main/java/br/com/ifba
(nenhuma ocorrência)
```

## Task 04 - Telas completas

- `TelaProdutos`: cadastrar, listar, buscar por nome, editar e excluir.
- `TelaCategorias` (botão "Categorias" na tela de produtos): cadastrar, listar, editar e excluir.

As duas chamam apenas o controller.

## Task 05 - Service testado sem banco

`FakeProdutoDao` e `FakeCategoriaDao` (em `src/test`) implementam as interfaces do DAO com uma `List` em memória. `ProdutoServiceTest` (15 testes) e `CategoriaServiceTest` (9 testes) usam só esses dublês, sem `JPAUtil` e sem banco.

```
Tests run: 15, Failures: 0, Errors: 0 -- ProdutoServiceTest
Tests run: 9,  Failures: 0, Errors: 0 -- CategoriaServiceTest
Tests run: 13, Failures: 0, Errors: 0 -- ProdutoDaoTest
Tests run: 3,  Failures: 0, Errors: 0 -- CategoriaDaoTest
```

## Task 06 - Isolamento da camada view

```
$ grep -rnE "import .*\.dao\.|EntityManager" src/main/java/br/com/ifba/*/view src/main/java/br/com/ifba/cadastro src/main/java/br/com/ifba/login
(nenhuma ocorrência)
```
