package br.com.ifba.infrastructure.exception;

/**
 * Lancada pela camada de servico quando uma regra de negocio e violada
 * (campo obrigatorio vazio, valor negativo, nome repetido...). A mensagem e
 * escrita para ser mostrada diretamente ao usuario.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
