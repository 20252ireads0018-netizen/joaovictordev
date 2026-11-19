package br.com.ifba.produto.service;

import br.com.ifba.infrastructure.exception.EntidadeNaoEncontradaException;
import br.com.ifba.produto.dao.CategoriaIDao;
import br.com.ifba.produto.entity.Categoria;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class FakeCategoriaDao implements CategoriaIDao {

    final List<Categoria> categorias = new ArrayList<>();
    private long proximoId = 1;

    @Override
    public Categoria save(Categoria categoria) {
        categoria.setId(proximoId++);
        categorias.add(categoria);
        return categoria;
    }

    @Override
    public Categoria update(Categoria categoria) {
        Categoria atual = findById(categoria.getId());
        categorias.set(categorias.indexOf(atual), categoria);
        return categoria;
    }

    @Override
    public void delete(Categoria categoria) {
        categorias.remove(findById(categoria.getId()));
    }

    @Override
    public Categoria findById(Long id) {
        return categorias.stream().filter(c -> c.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Categoria", id));
    }

    @Override
    public List<Categoria> findAll() {
        return new ArrayList<>(categorias);
    }

    @Override
    public Optional<Categoria> findByNome(String nome) {
        return categorias.stream().filter(c -> c.getNome().equalsIgnoreCase(nome.trim())).findFirst();
    }
}
