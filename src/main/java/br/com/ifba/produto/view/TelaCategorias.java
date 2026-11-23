package br.com.ifba.produto.view;

import org.springframework.stereotype.Component;
import br.com.ifba.infrastructure.exception.EntidadeNaoEncontradaException;
import br.com.ifba.infrastructure.exception.PersistenciaException;
import br.com.ifba.infrastructure.exception.RegraNegocioException;
import br.com.ifba.produto.controller.CategoriaIController;
import br.com.ifba.produto.entity.Categoria;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
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

@Component
public class TelaCategorias extends JFrame implements JanelaAuxiliar {

    private static final Logger LOGGER = Logger.getLogger(TelaCategorias.class.getName());

    private final CategoriaIController controller;

    private final JTextField txtNome = new JTextField(25);
    private final DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Nome"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);

    private Long idSelecionado;

    public TelaCategorias(CategoriaIController controller) {
        super("Categorias - PGCD");
        this.controller = controller;
        montarTela();
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    @Override
    public void abrir() {
        carregarTabela();
        setVisible(true);
        toFront();
    }

    private void montarTela() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Nome:"));
        form.add(txtNome);

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
        getContentPane().add(new JScrollPane(tabela), BorderLayout.CENTER);
        getContentPane().add(baixo, BorderLayout.SOUTH);

        btnCadastrar.addActionListener(e -> cadastrar());
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                if (linha >= 0) {
                    idSelecionado = (Long) modelo.getValueAt(linha, 0);
                    txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
                }
            }
        });

        setSize(480, 420);
        setLocationRelativeTo(null);
    }

    private void cadastrar() {
        try {
            controller.save(new Categoria(txtNome.getText()));
            JOptionPane.showMessageDialog(this, "Categoria cadastrada com sucesso!");
            limparFormulario();
            carregarTabela();
        } catch (RegraNegocioException e) {
            avisar(e.getMessage());
        } catch (PersistenciaException e) {
            erro("cadastrar a categoria", e);
        }
    }

    private void editar() {
        try {
            Categoria categoria = new Categoria(txtNome.getText());
            categoria.setId(idSelecionado);
            controller.update(categoria);
            JOptionPane.showMessageDialog(this, "Categoria atualizada com sucesso!");
            limparFormulario();
            carregarTabela();
        } catch (RegraNegocioException | EntidadeNaoEncontradaException e) {
            avisar(e.getMessage());
        } catch (PersistenciaException e) {
            erro("atualizar a categoria", e);
        }
    }

    private void excluir() {
        if (idSelecionado != null) {
            int resposta = JOptionPane.showConfirmDialog(this,
                    "Excluir a categoria selecionada?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            Categoria categoria = new Categoria();
            categoria.setId(idSelecionado);
            controller.delete(categoria);
            JOptionPane.showMessageDialog(this, "Categoria excluída com sucesso!");
            limparFormulario();
            carregarTabela();
        } catch (RegraNegocioException | EntidadeNaoEncontradaException e) {
            avisar(e.getMessage());
        } catch (PersistenciaException e) {
            erro("excluir a categoria (verifique se ela não está em uso por algum produto)", e);
        }
    }

    private void carregarTabela() {
        try {
            List<Categoria> categorias = controller.findAll();
            modelo.setRowCount(0);
            for (Categoria c : categorias) {
                modelo.addRow(new Object[]{c.getId(), c.getNome()});
            }
        } catch (PersistenciaException e) {
            erro("carregar as categorias", e);
        }
    }

    private void limparFormulario() {
        idSelecionado = null;
        txtNome.setText("");
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
