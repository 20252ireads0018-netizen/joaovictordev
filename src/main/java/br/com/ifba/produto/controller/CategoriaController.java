package br.com.ifba.produto.controller;

import org.springframework.stereotype.Controller;
import br.com.ifba.produto.entity.Categoria;
import br.com.ifba.produto.service.CategoriaIService;
import java.util.List;

@Controller
public class CategoriaController implements CategoriaIController {

    private final CategoriaIService service;

    public CategoriaController(CategoriaIService service) {
        this.service = service;
    }

    @Override
    public Categoria save(Categoria categoria) {
        return service.save(categoria);
    }

    @Override
    public Categoria update(Categoria categoria) {
        return service.update(categoria);
    }

    @Override
    public void delete(Categoria categoria) {
        service.delete(categoria);
    }

    @Override
    public Categoria findById(Long id) {
        return service.findById(id);
    }

    @Override
    public List<Categoria> findAll() {
        return service.findAll();
    }
}
