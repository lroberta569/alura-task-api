package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Test
    void existsByStatement__when_statement_already_exists() {
        User user = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
        userRepository.save(user);
        Course course = new Course("Java", "Curso de Java", user);
        courseRepository.save(course);

        OpenTextTask openTextTask = new OpenTextTask();
        openTextTask.setStatement("O que é Java?");
        openTextTask.setOrder(1);
        openTextTask.setCourse(course);
        taskRepository.save(openTextTask);

        OpenTextTask openTextTask2 = new OpenTextTask();
        openTextTask2.setStatement("O que é Java?");
        openTextTask2.setOrder(1);
        openTextTask2.setCourse(course);
        taskRepository.save(openTextTask2);

        boolean result = taskRepository.existsByStatementAndCourseId("O que é Java?", course.getId());
        assertThat(result).isTrue();

    }

    @Test
    void findByCourseOrderByOrderAsc__returns_tasks_when_course_exists() {
        User user = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
        userRepository.save(user);
        Course course = new Course("Java", "Curso de Java", user);
        course.setStatus(Status.BUILDING);
        course.setCreatedAt(LocalDateTime.now());
        courseRepository.save(course);

        OpenTextTask openTextTask = new OpenTextTask();
        openTextTask.setStatement("O que é Java?");
        openTextTask.setOrder(1);
        openTextTask.setCourse(course);
        openTextTask.setType(Type.OPEN_TEXT);
        openTextTask.setCreatedAt(LocalDateTime.now());
        Task task = taskRepository.save(openTextTask);

        List<Task> result = taskRepository.findByCourseOrderByOrderAsc(course);
        assertThat(result).isEqualTo(List.of(task));
        assertThat(result.size()).isEqualTo(1);

        result = taskRepository.findByCourseOrderByOrderAsc(course);
        assertThat(result).isEqualTo(List.of(task));


    }

}
