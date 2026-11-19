package br.com.ifba.produto.controller;

import br.com.ifba.produto.entity.Categoria;
import java.util.List;

public interface CategoriaIController {

    Categoria save(Categoria categoria);

    Categoria update(Categoria categoria);

    void delete(Categoria categoria);

    Categoria findById(Long id);

    List<Categoria> findAll();
}
