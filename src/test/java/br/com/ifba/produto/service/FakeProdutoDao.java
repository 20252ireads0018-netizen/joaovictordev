package br.com.ifba.produto.service;

import br.com.ifba.infrastructure.exception.EntidadeNaoEncontradaException;
import br.com.ifba.produto.dao.ProdutoIDao;
import br.com.ifba.produto.entity.Produto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class FakeProdutoDao implements ProdutoIDao {

    final List<Produto> produtos = new ArrayList<>();
    private long proximoId = 1;

    @Override
    public Produto save(Produto produto) {
        produto.setId(proximoId++);
        produtos.add(produto);
        return produto;
    }

    @Override
    public Produto update(Produto produto) {
        Produto atual = findById(produto.getId());
        produtos.set(produtos.indexOf(atual), produto);
        return produto;
    }

    @Override
    public void delete(Produto produto) {
        produtos.remove(findById(produto.getId()));
    }

    @Override
    public Produto findById(Long id) {
        return produtos.stream().filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto", id));
    }

    @Override
    public List<Produto> findAll() {
        List<Produto> lista = new ArrayList<>(produtos);
        lista.sort(Comparator.comparing(Produto::getNome));
        return lista;
    }

    @Override
    public List<Produto> findByNome(String termo) {
        String termoMinusculo = termo.toLowerCase();
        return produtos.stream().filter(p -> p.getNome().toLowerCase().contains(termoMinusculo)).toList();
    }

    @Override
    public List<Produto> findComEstoqueAbaixoDe(int limite) {
        return produtos.stream().filter(p -> p.getEstoque() < limite).toList();
    }
}
