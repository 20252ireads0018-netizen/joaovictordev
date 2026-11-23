package br.com.ifba.produto.service;

import org.springframework.stereotype.Service;
import br.com.ifba.infrastructure.exception.RegraNegocioException;
import br.com.ifba.produto.dao.CategoriaIDao;
import br.com.ifba.produto.entity.Categoria;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService implements CategoriaIService {

    static final int TAMANHO_MAXIMO_NOME = 80;

    private final CategoriaIDao dao;

    public CategoriaService(CategoriaIDao dao) {
        this.dao = dao;
    }

    @Override
    public Categoria save(Categoria categoria) {
        validar(categoria);
        return dao.save(categoria);
    }

    @Override
    public Categoria update(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new RegraNegocioException("Selecione uma categoria para editar.");
        }
        validar(categoria);
        return dao.update(categoria);
    }

    @Override
    public void delete(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new RegraNegocioException("Selecione uma categoria para excluir.");
        }
        dao.delete(categoria);
    }

    @Override
    public Categoria findById(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Informe a categoria que deseja buscar.");
        }
        return dao.findById(id);
    }

    @Override
    public List<Categoria> findAll() {
        return dao.findAll();
    }

    private void validar(Categoria categoria) {
        if (categoria == null) {
            throw new RegraNegocioException("Informe os dados da categoria.");
        }
        String nome = categoria.getNome() == null ? "" : categoria.getNome().trim();
        if (nome.isEmpty()) {
            throw new RegraNegocioException("Informe o nome da categoria.");
        }
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new RegraNegocioException("O nome da categoria deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }
        Optional<Categoria> existente = dao.findByNome(nome);
        if (existente.isPresent() && !existente.get().getId().equals(categoria.getId())) {
            throw new RegraNegocioException("Já existe uma categoria com o nome " + nome + ".");
        }
        categoria.setNome(nome);
    }
}
