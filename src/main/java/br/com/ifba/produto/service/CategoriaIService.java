package br.com.ifba.produto.service;

import br.com.ifba.produto.entity.Categoria;
import java.util.List;

public interface CategoriaIService {

    Categoria save(Categoria categoria);

    Categoria update(Categoria categoria);

    void delete(Categoria categoria);

    Categoria findById(Long id);

    List<Categoria> findAll();
}
