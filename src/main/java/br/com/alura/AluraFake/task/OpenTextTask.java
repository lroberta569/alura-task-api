package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("OPEN_TEXT")
public class OpenTextTask extends Task {

    public OpenTextTask(Long id, String statement, Integer order, Course course, Type type) {
        super(id, statement, order, course, type);
    }

}
