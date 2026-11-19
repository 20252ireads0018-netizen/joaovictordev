package br.com.ifba.produto.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.ifba.infrastructure.exception.RegraNegocioException;
import br.com.ifba.produto.entity.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Regras de negocio de Categoria testadas com o FakeCategoriaDao, sem banco. */
class CategoriaServiceTest {

    private FakeCategoriaDao dao;
    private CategoriaService service;

    @BeforeEach
    void preparar() {
        dao = new FakeCategoriaDao();
        service = new CategoriaService(dao);
    }

    @Test
    void salvarCategoriaValidaGravaNoDao() {
        service.save(new Categoria("Periféricos"));
        assertEquals(1, dao.categorias.size());
    }

    @Test
    void nomeVazioNaoESalvo() {
        RegraNegocioException e = assertThrows(RegraNegocioException.class, () -> service.save(new Categoria(" ")));
        assertEquals("Informe o nome da categoria.", e.getMessage());
        assertTrue(dao.categorias.isEmpty());
    }

    @Test
    void nomeMuitoLongoNaoESalvo() {
        String nome = "x".repeat(CategoriaService.TAMANHO_MAXIMO_NOME + 1);
        assertThrows(RegraNegocioException.class, () -> service.save(new Categoria(nome)));
    }

    @Test
    void nomeRepetidoNaoESalvoMesmoComCaixaDiferente() {
        service.save(new Categoria("Monitores"));
        assertThrows(RegraNegocioException.class, () -> service.save(new Categoria("MONITORES")));
        assertEquals(1, dao.categorias.size());
    }

    @Test
    void editarMantendoOMesmoNomeEPermitido() {
        Categoria salva = service.save(new Categoria("Cabos"));
        Categoria editada = new Categoria("Cabos");
        editada.setId(salva.getId());
        assertDoesNotThrow(() -> service.update(editada));
    }

    @Test
    void editarParaNomeDeOutraCategoriaLancaExcecao() {
        service.save(new Categoria("Cabos"));
        Categoria outra = service.save(new Categoria("Fontes"));
        Categoria editada = new Categoria("Cabos");
        editada.setId(outra.getId());
        assertThrows(RegraNegocioException.class, () -> service.update(editada));
    }

    @Test
    void editarSemIdLancaExcecao() {
        assertThrows(RegraNegocioException.class, () -> service.update(new Categoria("Cabos")));
    }

    @Test
    void excluirSemIdLancaExcecao() {
        assertThrows(RegraNegocioException.class, () -> service.delete(new Categoria("Cabos")));
    }

    @Test
    void salvarRemoveEspacosDoNome() {
        Categoria salva = service.save(new Categoria("  Áudio "));
        assertEquals("Áudio", salva.getNome());
    }
}
