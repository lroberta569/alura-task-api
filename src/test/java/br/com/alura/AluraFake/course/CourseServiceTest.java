package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.util.ApplicationRulesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CourseServiceTest {
    private CourseRepository courseRepository;
    private TaskRepository taskRepository;
    private CourseService courseService;

    @BeforeEach
    void setup() {
        courseRepository = mock(CourseRepository.class);
        taskRepository = mock(TaskRepository.class);
        courseService = new CourseService(courseRepository, taskRepository);
    }

    @Test
    void publishCourse_shouldThrowException_whenCourseNotFound() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            courseService.publishCourse(1L);
        });

        assertEquals("courseId", ex.getErrors().get(0).getField());
        assertEquals("Curso não encontrado.", ex.getErrors().get(0).getMessage());
    }

    @Test
    void publishCourse_shouldThrowException_whenStatusNotBuilding() {
        Course course = new Course();
        course.setStatus(Status.PUBLISHED);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            courseService.publishCourse(1L);
        });

        assertEquals("status", ex.getErrors().get(0).getField());
        assertEquals("Curso só pode ser publicado se estiver com status BUILDING", ex.getErrors().get(0).getMessage());
    }

    @Test
    void publishCourse_shouldThrowException_whenMissingTaskTypes() {
        Course course = new Course();
        course.setStatus(Status.BUILDING);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));

        List<Task> tasks = List.of(
                taskOfType(Type.OPEN_TEXT),
                taskOfType(Type.OPEN_TEXT)
        );
        when(taskRepository.findByCourseOrderByOrderAsc(course)).thenReturn(tasks);

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            courseService.publishCourse(1L);
        });

        assertEquals("tasks", ex.getErrors().get(0).getField());
        assertEquals("Curso deve ter pelo menos uma tarefa de cada tipo", ex.getErrors().get(0).getMessage());
    }

    @Test
    void publishCourse_shouldThrowException_whenOrderNotSequential() {
        Course course = new Course();
        course.setStatus(Status.BUILDING);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));

        List<Task> tasks = List.of(
                taskOfTypeAndOrder(Type.OPEN_TEXT, 1),
                taskOfTypeAndOrder(Type.SINGLE_CHOICE, 3),
                taskOfTypeAndOrder(Type.MULTIPLE_CHOICE, 2)
        );
        when(taskRepository.findByCourseOrderByOrderAsc(course)).thenReturn(tasks);

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            courseService.publishCourse(1L);
        });

        assertEquals("order", ex.getErrors().get(0).getField());
        assertEquals("A ordem das tarefas deve ser contínua e começar em 1", ex.getErrors().get(0).getMessage());
    }

    @Test
    void publishCourse_shouldPublishCourse_whenAllValid() {
        Course course = new Course();
        course.setStatus(Status.BUILDING);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));

        List<Task> tasks = List.of(
                taskOfTypeAndOrder(Type.OPEN_TEXT, 1),
                taskOfTypeAndOrder(Type.SINGLE_CHOICE, 2),
                taskOfTypeAndOrder(Type.MULTIPLE_CHOICE, 3)
        );
        when(taskRepository.findByCourseOrderByOrderAsc(course)).thenReturn(tasks);

        courseService.publishCourse(1L);

        assertEquals(Status.PUBLISHED, course.getStatus());
        assertNotNull(course.getPublishedAt());

        verify(courseRepository).save(course);
    }

    private Task taskOfType(Type type) {
        return new Task() {{
            setType(type);
            setOrder(1);
        }};
    }

    private Task taskOfTypeAndOrder(Type type, int order) {
        return new Task() {{
            setType(type);
            setOrder(order);
        }};
    }
}

