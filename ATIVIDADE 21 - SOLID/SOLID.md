# Atividade 21 - Auditoria do próprio código (SOLID e Clean Code)

Branch: `feature/atividade-21-solid`

## Task 01 - Um exemplo de cada princípio

| Princípio | Onde | Por quê |
|---|---|---|
| **S** - Responsabilidade única | `src/main/java/br/com/ifba/produto/service/ProdutoService.java`, linha 67 (`validar`) | As regras de negócio do produto ficam só no service. O DAO só acessa o banco e a tela só mostra e lê dados. |
| **O** - Aberto/fechado | `src/main/java/br/com/ifba/produto/dao/ProdutoDao.java`, linha 8 | `ProdutoDao` estende `GenericDao` e acrescenta `findByNome` e `findComEstoqueAbaixoDe` sem alterar a classe genérica. |
| **L** - Substituição de Liskov | `src/test/java/br/com/ifba/produto/service/FakeProdutoDao.java`, linha 10 | O `FakeProdutoDao` (lista em memória) entra no lugar do `ProdutoDao` real e o `ProdutoService` funciona igual. Outro exemplo: `EntidadeNaoEncontradaException` (linha 4) pode ser tratada como `PersistenciaException`. |
| **I** - Segregação de interfaces | `src/main/java/br/com/ifba/produto/dao/CategoriaIDao.java`, linha 8 | `CategoriaIDao` só acrescenta `findByNome`. Ela não é obrigada a ter os finders de estoque, que ficam só em `ProdutoIDao`. |
| **D** - Inversão de dependência | `src/main/java/br/com/ifba/produto/service/ProdutoService.java`, linhas 12 a 16 | O service recebe um `ProdutoIDao` (interface) pelo construtor. Quem decide a implementação é o `main`, ou o teste. |

## Task 02 - Violações encontradas

1. **DIP / OCP em `ValidadorUsuario`** (antes: `src/main/java/br/com/ifba/usuario/ValidadorUsuario.java`, linhas 5 a 12). O método era `static` e a lista de palavras proibidas estava fixa dentro dele. A `TelaCadastroUsuario` chamava a classe concreta direto. Para mudar as palavras era preciso editar a classe, e não dava para trocar a regra em teste.
2. **DIP em `TelaProdutos`** (antes: `src/main/java/br/com/ifba/produto/view/TelaProdutos.java`, linha 32). A tela de produtos recebia a classe concreta `TelaCategorias` só para chamar `abrir()`. Uma tela dependia diretamente de outra.
3. **SRP no `main`** (`src/main/java/com/mycompany/prg04joaovictorbarretopimentel/Prg04JoaoVictorBarretoPimentel.java`, linhas 20 a 42). O mesmo método roda as migrations, trata o erro de banco, monta todas as dependências e abre a interface. São motivos diferentes para mudar.
4. **DIP em `GenericDao`** (`src/main/java/br/com/ifba/infrastructure/dao/GenericDao.java`, linhas 40, 57, 80, 102 e 129). O DAO busca o `EntityManager` na classe estática `JPAUtil` em vez de recebê-lo. Por isso os testes do DAO precisam de um banco H2 de verdade.

## Task 03 - Violações corrigidas

- **Violação 1 (OCP/DIP):** `ValidadorUsuario` virou uma classe com instância, e a lista de palavras chega pelo construtor. O construtor vazio mantém a lista antiga (admin, teste, root, senha123), então o comportamento não mudou. Agora a `TelaCadastroUsuario` recebe o validador pelo construtor. Para acrescentar palavras basta passar outra lista, sem editar a classe. Foi criado o `ValidadorUsuarioTest` para cobrir isso.
- **Violação 2 (DIP):** a `TelaProdutos` agora recebe um `Runnable abrirCategorias` no lugar da `TelaCategorias`. O `main` passa `telaCategorias::abrir`. A tela de produtos não conhece mais a tela de categorias.

## Task 04 - Nomes melhorados (refactor/rename)

| Antes | Depois | Arquivo |
|---|---|---|
| `jButton1` | `btnCadastrar` | `TelaCadastroUsuario.java` / `.form` |
| `jLabel1` | `lblLogin` | `TelaCadastroUsuario.java` / `.form` |
| `jButton1ActionPerformed` | `btnCadastrarActionPerformed` | `TelaCadastroUsuario.java` / `.form` |
| `t` | `termoMinusculo` | `FakeProdutoDao.java` |
| `produto(...)` (helper de teste) | `novoProduto(...)` | `ProdutoServiceTest.java` |
| `novo(...)` (helper de teste) | `novoProduto(...)` | `ProdutoDaoTest.java` |
| `precoObrigatorio` | `precoNuloNaoESalvo` | `ProdutoServiceTest.java` |
| `excecaoDeDominioEsconde0Jpa` (tinha um zero no lugar do "o") | `entidadeNaoEncontradaEhUmaPersistenciaException` | `ProdutoDaoTest.java` |

## Task 05 - Um comentário a menos

Em `GenericIDao.java`, o comentário `/** Busca pelo identificador. */` em `findById` só repetia o nome do método. Ele foi reescrito para explicar o porquê: o método lança `EntidadeNaoEncontradaException` em vez de devolver `null`, então quem chama não precisa testar `null`.

## Task 06 - Testes

Todas as mudanças são refatorações e não alteram comportamento. Toda a bateria (`ProdutoServiceTest`, `CategoriaServiceTest`, `ProdutoDaoTest`, `CategoriaDaoTest` e o novo `ValidadorUsuarioTest`) deve continuar verde. O print da execução fica nesta pasta (`testes.jpg`): 43 testes, 100% passando no NetBeans.
