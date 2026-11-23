package com.mycompany.prg04joaovictorbarretopimentel;

import br.com.ifba.infrastructure.util.JPAUtil;
import br.com.ifba.infrastructure.util.MigracaoBanco;
import br.com.ifba.produto.controller.CategoriaController;
import br.com.ifba.produto.controller.CategoriaIController;
import br.com.ifba.produto.controller.ProdutoController;
import br.com.ifba.produto.controller.ProdutoIController;
import br.com.ifba.produto.dao.CategoriaDao;
import br.com.ifba.produto.dao.ProdutoDao;
import br.com.ifba.produto.service.CategoriaService;
import br.com.ifba.produto.service.ProdutoService;
import br.com.ifba.produto.view.TelaCategorias;
import br.com.ifba.produto.view.TelaProdutos;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Prg04JoaoVictorBarretoPimentel {

    public static void main(String[] args) {
        try {
            var resultado = MigracaoBanco.migrar();
            System.out.println("Flyway: " + resultado.migrationsExecuted
                    + " migration(s) aplicada(s); versao atual do banco: " + resultado.targetSchemaVersion);
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Nao foi possivel preparar o banco de dados:\n" + e.getMessage(),
                    "Erro ao iniciar", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(JPAUtil::close));

        ProdutoIController produtoController = new ProdutoController(new ProdutoService(new ProdutoDao()));
        CategoriaIController categoriaController = new CategoriaController(new CategoriaService(new CategoriaDao()));

        SwingUtilities.invokeLater(() -> {
            TelaCategorias telaCategorias = new TelaCategorias(categoriaController);
            new TelaProdutos(produtoController, telaCategorias::abrir).setVisible(true);
        });
    }
}
