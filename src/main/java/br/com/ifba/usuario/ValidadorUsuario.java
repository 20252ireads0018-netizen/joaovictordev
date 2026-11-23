package br.com.ifba.usuario;

import java.util.List;

public class ValidadorUsuario {

    private static final List<String> PALAVRAS_PROIBIDAS_PADRAO = List.of("admin", "teste", "root", "senha123");

    private final List<String> palavrasProibidas;

    public ValidadorUsuario() {
        this(PALAVRAS_PROIBIDAS_PADRAO);
    }

    public ValidadorUsuario(List<String> palavrasProibidas) {
        this.palavrasProibidas = List.copyOf(palavrasProibidas);
    }

    public boolean contemPalavraProibida(String texto) {
        String textoMinusculo = texto.toLowerCase();
        return palavrasProibidas.stream()
                .anyMatch(palavra -> textoMinusculo.contains(palavra.toLowerCase()));
    }
}
