package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewOptionDTO {
    @NotBlank
    @Length(min = 4, max = 80)
    private String option;
    @NotNull
    private boolean isCorrect;

    public NewOptionDTO(String option, boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public String getOption() {
        return option;
    }

    public String setOption() {
        return option;
   }

    public boolean isCorrect() {
        return isCorrect;
    }

    public Boolean setCorrect() {
       return isCorrect;
    }
}
