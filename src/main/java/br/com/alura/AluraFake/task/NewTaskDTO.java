package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewTaskDTO {

    @NotBlank
    @Length(min = 4, max = 255)
    private String statement;
    @NotNull
    @Min(1)
    private Integer order;
    @NotNull
    private Long courseId;
    private Type type;
    private List<NewOptionDTO> options;

}
