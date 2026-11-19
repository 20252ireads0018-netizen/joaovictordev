package br.com.ifba.produto.controller;

import br.com.ifba.produto.entity.Produto;
import java.util.List;

/** Ponto de entrada da tela de produtos para as operacoes do sistema. */
public interface ProdutoIController {

    Produto save(Produto produto);

    Produto update(Produto produto);

    void delete(Produto produto);

    Produto findById(Long id);

    List<Produto> findAll();

    List<Produto> findByNome(String termo);
}
