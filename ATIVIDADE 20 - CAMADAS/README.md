Atividade 20 - Arquitetura em camadas

Camadas: View -> Controller -> Service -> DAO.

Criei service e controller (com interface) para Produto e Categoria, no pacote br.com.ifba.produto.

As validacoes que estavam na tela foram para o service (nome obrigatorio, preco e estoque nao negativos, nome de categoria repetido).

O service recebe o dao pelo construtor e o controller recebe o service. Quem monta tudo e o main.

Telas: TelaProdutos e TelaCategorias, com salvar, listar, editar e excluir, chamando so o controller.

Testes: FakeProdutoDao e FakeCategoriaDao com lista em memoria, usados no ProdutoServiceTest e no CategoriaServiceTest (sem banco).

Busca na camada view por "dao" e "EntityManager": nenhum resultado.
