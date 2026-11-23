package br.com.ifba;

import br.com.ifba.infrastructure.util.JPAUtil;
import br.com.ifba.infrastructure.util.MigracaoBanco;
import br.com.ifba.produto.view.TelaProdutos;
import javax.swing.SwingUtilities;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProjetoApplication {

    public static void main(String[] args) {
        MigracaoBanco.migrar();
        Runtime.getRuntime().addShutdownHook(new Thread(JPAUtil::close));

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ProjetoApplication.class)
                        .headless(false)
                        .run(args);

        TelaProdutos tela = context.getBean(TelaProdutos.class);
        SwingUtilities.invokeLater(() -> tela.setVisible(true));
    }
}
