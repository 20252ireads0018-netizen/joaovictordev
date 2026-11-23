# Atividade 22 - Spring no projeto

Branch: `feature/atividade-22-spring`

## Task 01 - Spring no projeto
- `pom.xml`: dependência `spring-boot-starter` 3.2.0. É o starter básico, sem servidor web.
- Classe de entrada nova: `br.com.ifba.ProjetoApplication`, com `@SpringBootApplication`, `.headless(false)` e `context.getBean(TelaProdutos.class)`. A tela já vem montada do contêiner, com controller, service e dao.
- O `main` antigo, que montava tudo na mão, foi removido. O `exec.mainClass` agora aponta para a `ProjetoApplication`.
- `maven.compiler.release` foi de 26 para 21. O Spring Boot 3.2 não lê classes compiladas para Java 26 durante a varredura de componentes.

## Task 02 - Uma anotação por camada
| Camada | Classes | Anotação |
|---|---|---|
| View | `TelaProdutos`, `TelaCategorias`, `LoginView`, `TelaCadastroUsuario` | `@Component` |
| Controller | `ProdutoController`, `CategoriaController` | `@Controller` |
| Service | `ProdutoService`, `CategoriaService` | `@Service` |
| DAO | `ProdutoDao`, `CategoriaDao` | `@Repository` |

Não há nenhum `@Autowired`: o Spring injeta pelos construtores que já existiam.

Um único ajuste: a `TelaProdutos` recebia um `Runnable` para abrir a tela de categorias, e `Runnable` não é um bean. Ela passou a receber a interface `JanelaAuxiliar`, que a `TelaCategorias` implementa. Continua sem conhecer a classe concreta.

## Task 03 - A lógica não mudou
A mesma suíte de antes (43 testes) passa sem alterar nenhuma classe de teste. Os testes de Service continuam usando o FakeDao, sem subir o Spring.
- `1-testes-antes.jpg`: barra verde antes do Spring
- `2-testes-depois.jpg`: barra verde depois do Spring
- `3-tela-abrindo-pelo-spring.jpg`: a tela abrindo pela `ProjetoApplication`, sem o main antigo (log "Started ProjetoApplication")

## Task 04 - application.properties
- A URL, o usuário, a senha, o driver, o ddl-auto e o show-sql saíram do `persistence.xml` e foram para `src/main/resources/application.properties`. O `persistence.xml` agora só lista as entidades.
- A classe `ConfiguracaoBanco` lê o arquivo. O `JPAUtil` passa essas propriedades ao criar o `EntityManagerFactory`, e o `MigracaoBanco` (Flyway) usa a mesma conexão.
- Nos testes, `src/test/resources/application.properties` aponta para o H2 em memória.
- O banco padrão da aplicação é H2 em arquivo (`./data`). Para usar MySQL, basta trocar as linhas de conexão no `application.properties`.
