package br.com.fiap.exception;

public class ProcessoNaoAbertoException extends RegraNegocioException {

    public ProcessoNaoAbertoException(String mensagem) {
        super(mensagem);
    }
}
