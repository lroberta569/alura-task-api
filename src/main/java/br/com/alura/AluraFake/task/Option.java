package br.com.alura.AluraFake.task;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Length(min = 4, max = 80)
    @Column(nullable = false)
    private String option;
    @Column(nullable = false)
    private Boolean isCorrect;
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public Option(Long id, Task task, Boolean isCorrect, String option) {
        this.id = id;
        this.task = task;
        this.isCorrect = isCorrect;
        this.option = option;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public @Length(min = 4, max = 80) String getOption() {
        return option;
    }

    public void setOption(@Length(min = 4, max = 80) String option) {
        this.option = option;
    }
}
