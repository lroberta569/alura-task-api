package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByStatementAndCourseId(String statement, Long courseId);

    List<Task> findByCourseOrderByOrderAsc(Course course);
}
