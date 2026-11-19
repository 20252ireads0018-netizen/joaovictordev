package br.com.ifba.produto.controller;

import br.com.ifba.produto.entity.Produto;
import br.com.ifba.produto.service.ProdutoIService;
import java.util.List;

/** Repassa os pedidos da tela para o servico. O servico chega pelo construtor. */
public class ProdutoController implements ProdutoIController {

    private final ProdutoIService service;

    public ProdutoController(ProdutoIService service) {
        this.service = service;
    }

    @Override
    public Produto save(Produto produto) {
        return service.save(produto);
    }

    @Override
    public Produto update(Produto produto) {
        return service.update(produto);
    }

    @Override
    public void delete(Produto produto) {
        service.delete(produto);
    }

    @Override
    public Produto findById(Long id) {
        return service.findById(id);
    }

    @Override
    public List<Produto> findAll() {
        return service.findAll();
    }

    @Override
    public List<Produto> findByNome(String termo) {
        return service.findByNome(termo);
    }
}
