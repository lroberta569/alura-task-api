package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "task_type")
public abstract class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Length(min = 4, max = 255)
    private String statement;
    @Min(value = 1)
    private Integer order;
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private Type type;

    public Task(Long id, String statement, Integer order, Course course, Type type) {
        this.id = id;
        this.statement = statement;
        this.order = order;
        this.course = course;
        this.createdAt = LocalDateTime.now();
        this.type = type;
    }

    public Task() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
