package br.com.ifba.produto.service;

import org.springframework.stereotype.Service;
import br.com.ifba.infrastructure.exception.RegraNegocioException;
import br.com.ifba.produto.dao.ProdutoIDao;
import br.com.ifba.produto.entity.Produto;
import java.util.List;

@Service
public class ProdutoService implements ProdutoIService {

    static final int TAMANHO_MAXIMO_NOME = 120;

    private final ProdutoIDao dao;

    public ProdutoService(ProdutoIDao dao) {
        this.dao = dao;
    }

    @Override
    public Produto save(Produto produto) {
        validar(produto);
        return dao.save(produto);
    }

    @Override
    public Produto update(Produto produto) {
        validar(produto);
        exigirId(produto.getId(), "Selecione um produto para editar.");
        return dao.update(produto);
    }

    @Override
    public void delete(Produto produto) {
        if (produto == null) {
            throw new RegraNegocioException("Selecione um produto para excluir.");
        }
        exigirId(produto.getId(), "Selecione um produto para excluir.");
        dao.delete(produto);
    }

    @Override
    public Produto findById(Long id) {
        exigirId(id, "Informe o produto que deseja buscar.");
        return dao.findById(id);
    }

    @Override
    public List<Produto> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Produto> findByNome(String termo) {
        if (termo == null || termo.isBlank()) {
            throw new RegraNegocioException("Digite parte do nome para buscar.");
        }
        return dao.findByNome(termo.trim());
    }

    @Override
    public List<Produto> findComEstoqueAbaixoDe(int limite) {
        if (limite <= 0) {
            throw new RegraNegocioException("O limite de estoque deve ser maior que zero.");
        }
        return dao.findComEstoqueAbaixoDe(limite);
    }

    private void validar(Produto produto) {
        if (produto == null) {
            throw new RegraNegocioException("Informe os dados do produto.");
        }
        String nome = produto.getNome() == null ? "" : produto.getNome().trim();
        if (nome.isEmpty()) {
            throw new RegraNegocioException("Informe o nome do produto.");
        }
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException("O nome do produto deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
        if (produto.getPreco() == null) {
            throw new RegraNegocioException("Informe o preço do produto.");
        }
        if (produto.getPreco().signum() < 0) {
            throw new RegraNegocioException("O preço não pode ser negativo.");
        }
        if (produto.getEstoque() < 0) {
            throw new RegraNegocioException("O estoque não pode ser negativo.");
        }
        produto.setNome(nome);
        if (produto.getFornecedor() != null) {
            produto.setFornecedor(produto.getFornecedor().trim());
        }
    }

    private void exigirId(Long id, String mensagem) {
        if (id == null) {
            throw new RegraNegocioException(mensagem);
        }
    }
}
