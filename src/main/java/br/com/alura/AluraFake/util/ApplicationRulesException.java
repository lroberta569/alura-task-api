package br.com.alura.AluraFake.util;

import lombok.Getter;

import java.util.List;

@Getter
public class ApplicationRulesException extends RuntimeException {
    private final List<ErrorItemDTO> errors;
    public ApplicationRulesException(List<ErrorItemDTO> errors) {
        this.errors = errors;
    }
}
