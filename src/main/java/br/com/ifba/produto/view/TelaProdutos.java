package br.com.ifba.produto.view;

import br.com.ifba.infrastructure.exception.EntidadeNaoEncontradaException;
import br.com.ifba.infrastructure.exception.PersistenciaException;
import br.com.ifba.infrastructure.exception.RegraNegocioException;
import br.com.ifba.produto.controller.ProdutoIController;
import br.com.ifba.produto.entity.Produto;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Tela de CRUD de produtos (cadastrar, listar, buscar, editar, excluir).
 *
 * A tela so conhece o controller, recebido pelo construtor. As regras de
 * negocio (nome obrigatorio, preco e estoque nao negativos) ficam no service;
 * aqui so se converte o texto digitado em numero.
 */
public class TelaProdutos extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(TelaProdutos.class.getName());

    private final ProdutoIController controller;
    private final TelaCategorias telaCategorias;

    private final JTextField txtBusca = new JTextField(20);
    private final JTextField txtNome = new JTextField();
    private final JTextField txtFornecedor = new JTextField();
    private final JTextField txtPreco = new JTextField();
    private final JTextField txtEstoque = new JTextField();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Nome", "Fornecedor", "Preço", "Estoque"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    /** ID do produto selecionado na tabela; null quando e um cadastro novo. */
    private Long idSelecionado;

    public TelaProdutos(ProdutoIController controller, TelaCategorias telaCategorias) {
        super("Produtos - PGCD");
        this.controller = controller;
        this.telaCategorias = telaCategorias;
        montarTela();
        // Ao fechar, a aplicacao encerra e o shutdown hook do main fecha o JPA.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        carregarTabela(controller::findAll);
    }

    private void montarTela() {
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar por nome");
        JButton btnTodos = new JButton("Listar todos");
        JButton btnCategorias = new JButton("Categorias");
        busca.add(new JLabel("Nome:"));
        busca.add(txtBusca);
        busca.add(btnBuscar);
        busca.add(btnTodos);
        busca.add(btnCategorias);

        JPanel form = new JPanel(new GridLayout(4, 2, 6, 6));
        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("Fornecedor:"));
        form.add(txtFornecedor);
        form.add(new JLabel("Preço (R$):"));
        form.add(txtPreco);
        form.add(new JLabel("Estoque:"));
        form.add(txtEstoque);

        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnEditar = new JButton("Salvar edição");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botoes.add(btnCadastrar);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);
        botoes.add(btnLimpar);

        JPanel baixo = new JPanel(new BorderLayout(6, 6));
        baixo.add(form, BorderLayout.CENTER);
        baixo.add(botoes, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().add(busca, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(tabela), BorderLayout.CENTER);
        getContentPane().add(baixo, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> {
            txtBusca.setText("");
            carregarTabela(controller::findAll);
        });
        btnCategorias.addActionListener(e -> telaCategorias.abrir());
        btnCadastrar.addActionListener(e -> cadastrar());
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherFormularioComSelecao();
            }
        });

        setSize(760, 520);
        setLocationRelativeTo(null);
    }

    // ---------- operacoes ----------

    private void cadastrar() {
        try {
            controller.save(lerFormulario());
            JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            limparFormulario();
            carregarTabela(controller::findAll);
        } catch (IllegalArgumentException | RegraNegocioException e) {
            avisar(e.getMessage());
        } catch (PersistenciaException e) {
            erro("cadastrar o produto", e);
        }
    }

    private void editar() {
        try {
            Produto produto = lerFormulario();
            produto.setId(idSelecionado);
            controller.update(produto);
            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!");
            limparFormulario();
            carregarTabela(controller::findAll);
        } catch (EntidadeNaoEncontradaException e) {
            avisar(e.getMessage());
            limparFormulario();
            carregarTabela(controller::findAll);
        } catch (IllegalArgumentException | RegraNegocioException e) {
            avisar(e.getMessage());
        } catch (PersistenciaException e) {
            erro("atualizar o produto", e);
        }
    }

    private void excluir() {
        if (idSelecionado != null) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Excluir o produto selecionado?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            Produto produto = new Produto();
            produto.setId(idSelecionado);
            controller.delete(produto);
            JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");
        } catch (RegraNegocioException | EntidadeNaoEncontradaException e) {
            avisar(e.getMessage());
            return;
        } catch (PersistenciaException e) {
            erro("excluir o produto", e);
            return;
        }
        limparFormulario();
        carregarTabela(controller::findAll);
    }

    private void buscar() {
        String termo = txtBusca.getText();
        carregarTabela(() -> controller.findByNome(termo));
    }

    // ---------- apoio ----------

    private void carregarTabela(Supplier<List<Produto>> consulta) {
        try {
            List<Produto> produtos = consulta.get();
            modelo.setRowCount(0);
            for (Produto p : produtos) {
                modelo.addRow(new Object[]{p.getId(), p.getNome(), p.getFornecedor(), p.getPreco(), p.getEstoque()});
            }
        } catch (RegraNegocioException e) {
            avisar(e.getMessage());
        } catch (PersistenciaException e) {
            erro("carregar os produtos", e);
        }
    }

    /** Converte os campos da tela em um Produto. Validacoes de negocio ficam no service. */
    private Produto lerFormulario() {
        BigDecimal preco = null;
        String textoPreco = txtPreco.getText().trim();
        if (!textoPreco.isEmpty()) {
            try {
                preco = new BigDecimal(textoPreco.replace(',', '.'));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Preço inválido. Use apenas números, ex.: 19,90.");
            }
        }
        int estoque = 0;
        String textoEstoque = txtEstoque.getText().trim();
        if (!textoEstoque.isEmpty()) {
            try {
                estoque = Integer.parseInt(textoEstoque);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Estoque inválido. Use um número inteiro.");
            }
        }
        return new Produto(txtNome.getText(), txtFornecedor.getText(), preco, estoque);
    }

    private void preencherFormularioComSelecao() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        idSelecionado = (Long) modelo.getValueAt(linha, 0);
        txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
        Object fornecedor = modelo.getValueAt(linha, 2);
        txtFornecedor.setText(fornecedor == null ? "" : fornecedor.toString());
        txtPreco.setText(String.valueOf(modelo.getValueAt(linha, 3)));
        txtEstoque.setText(String.valueOf(modelo.getValueAt(linha, 4)));
    }

    private void limparFormulario() {
        idSelecionado = null;
        txtNome.setText("");
        txtFornecedor.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
        tabela.clearSelection();
    }

    private void avisar(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private void erro(String acao, PersistenciaException e) {
        LOGGER.log(Level.SEVERE, "Falha ao " + acao, e);
        JOptionPane.showMessageDialog(this,
                "Não foi possível " + acao + ".\n" + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
