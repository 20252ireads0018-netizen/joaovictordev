package br.com.ifba.usuario;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ValidadorUsuarioTest {

    @Test
    void listaPadraoBloqueiaPalavraProibidaSemDiferenciarCaixa() {
        assertTrue(new ValidadorUsuario().contemPalavraProibida("meuADMIN01"));
    }

    @Test
    void listaPadraoAceitaLoginComum() {
        assertFalse(new ValidadorUsuario().contemPalavraProibida("joaovictor"));
    }

    @Test
    void novaListaDePalavrasNaoExigeAlterarAClasse() {
        ValidadorUsuario validador = new ValidadorUsuario(List.of("ifba"));
        assertTrue(validador.contemPalavraProibida("aluno_ifba"));
        assertFalse(validador.contemPalavraProibida("admin"));
    }
}
