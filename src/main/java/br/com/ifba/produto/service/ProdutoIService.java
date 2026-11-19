package br.com.ifba.produto.service;

import br.com.ifba.produto.entity.Produto;
import java.util.List;

/** Regras de negocio de Produto. */
public interface ProdutoIService {

    Produto save(Produto produto);

    Produto update(Produto produto);

    void delete(Produto produto);

    Produto findById(Long id);

    List<Produto> findAll();

    List<Produto> findByNome(String termo);

    List<Produto> findComEstoqueAbaixoDe(int limite);
}
