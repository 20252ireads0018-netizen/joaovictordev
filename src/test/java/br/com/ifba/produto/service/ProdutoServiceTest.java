package br.com.ifba.produto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.ifba.infrastructure.exception.RegraNegocioException;
import br.com.ifba.produto.entity.Produto;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Regras de negocio de Produto testadas com o FakeProdutoDao, sem banco. */
class ProdutoServiceTest {

    private FakeProdutoDao dao;
    private ProdutoService service;

    @BeforeEach
    void preparar() {
        dao = new FakeProdutoDao();
        service = new ProdutoService(dao);
    }

    private Produto produto(String nome, String preco, int estoque) {
        return new Produto(nome, "Fornecedor X", preco == null ? null : new BigDecimal(preco), estoque);
    }

    @Test
    void salvarProdutoValidoGravaNoDao() {
        Produto salvo = service.save(produto("Mouse", "49.90", 10));
        assertNotNull(salvo.getId());
        assertEquals(1, dao.produtos.size());
    }

    @Test
    void salvarRemoveEspacosDoNome() {
        Produto salvo = service.save(produto("  Teclado  ", "120.00", 3));
        assertEquals("Teclado", salvo.getNome());
    }

    @Test
    void nomeVazioNaoESalvo() {
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.save(produto("   ", "10.00", 1)));
        assertEquals("Informe o nome do produto.", e.getMessage());
        assertTrue(dao.produtos.isEmpty());
    }

    @Test
    void nomeMuitoLongoNaoESalvo() {
        String nome = "a".repeat(ProdutoService.TAMANHO_MAXIMO_NOME + 1);
        assertThrows(RegraNegocioException.class, () -> service.save(produto(nome, "10.00", 1)));
    }

    @Test
    void precoObrigatorio() {
        assertThrows(RegraNegocioException.class, () -> service.save(produto("Cabo", null, 1)));
    }

    @Test
    void precoNegativoNaoESalvo() {
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.save(produto("Cabo", "-1.00", 1)));
        assertEquals("O preço não pode ser negativo.", e.getMessage());
    }

    @Test
    void estoqueNegativoNaoESalvo() {
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.save(produto("Cabo", "5.00", -2)));
        assertEquals("O estoque não pode ser negativo.", e.getMessage());
    }

    @Test
    void precoZeroEEstoqueZeroSaoPermitidos() {
        Produto salvo = service.save(produto("Brinde", "0", 0));
        assertNotNull(salvo.getId());
    }

    @Test
    void editarSemIdLancaExcecao() {
        assertThrows(RegraNegocioException.class, () -> service.update(produto("Mouse", "10.00", 1)));
    }

    @Test
    void editarProdutoExistenteAtualizaNoDao() {
        Produto salvo = service.save(produto("Mouse", "10.00", 1));
        Produto alterado = produto("Mouse Gamer", "99.90", 5);
        alterado.setId(salvo.getId());
        service.update(alterado);
        assertEquals("Mouse Gamer", dao.findById(salvo.getId()).getNome());
    }

    @Test
    void excluirSemIdLancaExcecao() {
        assertThrows(RegraNegocioException.class, () -> service.delete(new Produto()));
    }

    @Test
    void excluirRemoveDoDao() {
        Produto salvo = service.save(produto("Monitor", "800.00", 2));
        service.delete(salvo);
        assertTrue(dao.produtos.isEmpty());
    }

    @Test
    void buscaPorNomeVaziaLancaExcecao() {
        assertThrows(RegraNegocioException.class, () -> service.findByNome("  "));
    }

    @Test
    void buscaPorNomeRepassaTermoSemEspacos() {
        service.save(produto("Mouse sem fio", "50.00", 1));
        service.save(produto("Teclado", "80.00", 1));
        assertEquals(1, service.findByNome("  mouse ").size());
    }

    @Test
    void limiteDeEstoqueDeveSerPositivo() {
        assertThrows(RegraNegocioException.class, () -> service.findComEstoqueAbaixoDe(0));
    }
}
