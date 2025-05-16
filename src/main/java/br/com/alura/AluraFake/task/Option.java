package br.com.alura.AluraFake.task;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}
