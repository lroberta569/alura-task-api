package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class NewTaskDTO {

    @NotBlank
    @Length(min = 4, max = 255, message = "O enunciado deve ter entre 4 e 255 caracteres.")
    private String statement;
    @NotNull
    @Min(value = 1, message = "A ordem deve ser maior que 0.")
    private Integer order;
    @NotNull
    private Long courseId;
    private Type type;
    private List<NewOptionDTO> options;

    public NewTaskDTO(String statement, List<NewOptionDTO> options, Type type, Long courseId, Integer order) {
        this.statement = statement;
        this.options = options;
        this.type = type;
        this.courseId = courseId;
        this.order = order;
    }

    public NewTaskDTO() {

    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public List<NewOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<NewOptionDTO> options) {
        this.options = options;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
