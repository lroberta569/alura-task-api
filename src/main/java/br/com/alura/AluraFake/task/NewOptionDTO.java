package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class NewOptionDTO {
    @NotBlank
    @Length(min = 4, max = 80)
    private String option;
    @NotNull
    private boolean isCorrect;

}
