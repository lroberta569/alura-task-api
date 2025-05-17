package br.com.alura.AluraFake.util;

import java.util.List;

public class ApplicationRulesException extends RuntimeException {
    private final List<ErrorItemDTO> errors;

    public ApplicationRulesException(List<ErrorItemDTO> errors) {
        super("Erros de validação de regras de negócio");
        this.errors = errors;
    }

    public List<ErrorItemDTO> getErrors() {
        return errors;
    }
}
