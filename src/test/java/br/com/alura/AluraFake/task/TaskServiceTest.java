package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.ApplicationRulesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private TaskService taskService;

    private Course buildingCourse;

    @BeforeEach
    void setup() {
        buildingCourse = new Course();
        buildingCourse.setId(1L);
        buildingCourse.setStatus(Status.BUILDING);
    }

    @Test
    void createTask__should_return_bad_request_when_course_not_found() {

        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(999L);
        newTaskDTO.setStatement("O que aprendemos na aula de hoje?");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);


        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            taskService.createTask(newTaskDTO);
        });

        assertEquals("courseId", ex.getErrors().get(0).getField());
        assertEquals("Curso não encontrado.", ex.getErrors().get(0).getMessage());
    }

    @Test
    void validateTask__should_throw_exception_when_order_invalid() {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Teste válido");
        newTaskDTO.setOrder(0);
        newTaskDTO.setType(Type.OPEN_TEXT);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));
        when(taskRepository.existsByStatementAndCourseId("Teste válido", 1L)).thenReturn(false);
        when(taskRepository.findByCourseOrderByOrderAsc(buildingCourse)).thenReturn(List.of());

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            taskService.validateTask(newTaskDTO);
        });

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.getField().equals("order")));
    }

    @Test
    void validateTask__should_throw_exception_when_singleChoice_with_multiple_correct_options() {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Questão única");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.SINGLE_CHOICE);

        List<NewOptionDTO> options = List.of(
                new NewOptionDTO("Opção A", true),
                new NewOptionDTO("Opção B", true)
        );
        newTaskDTO.setOptions(options);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));
        when(taskRepository.existsByStatementAndCourseId("Questão única", 1L)).thenReturn(false);
        when(taskRepository.findByCourseOrderByOrderAsc(buildingCourse)).thenReturn(List.of());

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            taskService.validateTask(newTaskDTO);
        });

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.getField().equals("options")));
    }

    @Test
    void validateTask__should_pass_with_valid_openText_task() {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Questão aberta");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);
        newTaskDTO.setOptions(new ArrayList<>());

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));
        when(taskRepository.existsByStatementAndCourseId("Questão aberta", 1L)).thenReturn(false);
        when(taskRepository.findByCourseOrderByOrderAsc(buildingCourse)).thenReturn(List.of());

        assertDoesNotThrow(() -> {
            taskService.validateTask(newTaskDTO);
        });
    }

    @Test
    void validateTask__should_throw_exception_when_multipleChoice_with_less_than_2_correct_options() {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Questão múltipla");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.MULTIPLE_CHOICE);
        newTaskDTO.setOptions(List.of(
                new NewOptionDTO("Opção 1", true),
                new NewOptionDTO("Opção 2", false),
                new NewOptionDTO("Opção 3", false)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));
        when(taskRepository.existsByStatementAndCourseId("Questão múltipla", 1L)).thenReturn(false);
        when(taskRepository.findByCourseOrderByOrderAsc(buildingCourse)).thenReturn(List.of());

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            taskService.validateTask(newTaskDTO);
        });

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.getMessage().contains("MultipleChoice deve ter pelo menos 2 corretas e 1 incorreta.")));
    }

    @Test
    void validateTask__should_throw_exception_when_openText_has_options() {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Texto aberto");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);
        newTaskDTO.setOptions(List.of(new NewOptionDTO("Opção inválida", false)));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));
        when(taskRepository.existsByStatementAndCourseId("Texto aberto", 1L)).thenReturn(false);
        when(taskRepository.findByCourseOrderByOrderAsc(buildingCourse)).thenReturn(List.of());

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            taskService.validateTask(newTaskDTO);
        });

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.getField().equals("options")));
    }

    @Test
    void validateTask__should_throw_exception_when_options_have_duplicates() {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Pergunta com duplicadas");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.SINGLE_CHOICE);
        newTaskDTO.setOptions(List.of(
                new NewOptionDTO("Repetida", true),
                new NewOptionDTO("Repetida", false)
        ));

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));
        when(taskRepository.existsByStatementAndCourseId("Pergunta com duplicadas", 1L)).thenReturn(false);
        when(taskRepository.findByCourseOrderByOrderAsc(buildingCourse)).thenReturn(List.of());

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            taskService.validateTask(newTaskDTO);
        });

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.getMessage().contains("diferentes")));
    }

    @Test
    void validateTask__should_throw_exception_when_order_not_continuous() {
        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Nova questão");
        newTaskDTO.setOrder(5);

        newTaskDTO.setType(Type.OPEN_TEXT);
        newTaskDTO.setOptions(new ArrayList<>());

        Task t1 = new OpenTextTask();
        t1.setOrder(1);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(buildingCourse));
        when(taskRepository.existsByStatementAndCourseId("Nova questão", 1L)).thenReturn(false);
        when(taskRepository.findByCourseOrderByOrderAsc(buildingCourse)).thenReturn(List.of(t1));

        ApplicationRulesException ex = assertThrows(ApplicationRulesException.class, () -> {
            taskService.validateTask(newTaskDTO);
        });

        assertTrue(ex.getErrors().stream().anyMatch(e -> e.getField().equals("order")));
    }

    @Test
    void resequenceTasks__should_increment_order_for_later_tasks() {
        Task task1 = new OpenTextTask();
        task1.setOrder(1);
        Task task2 = new OpenTextTask();
        task2.setOrder(2);
        Task task3 = new OpenTextTask();
        task3.setOrder(3);

        List<Task> existing = List.of(task1, task2, task3);

        when(taskRepository.findByCourseOrderByOrderAsc(buildingCourse)).thenReturn(existing);

        taskService.resequenceTasks(buildingCourse, 2);

        assertEquals(1, task1.getOrder());
        assertEquals(3, task2.getOrder());
        assertEquals(4, task3.getOrder());

        verify(taskRepository).saveAll(argThat(tasks ->
                StreamSupport.stream(tasks.spliterator(), false)
                        .anyMatch(t -> t.getOrder() == 3 || t.getOrder() == 4)
        ));
    }

    @Test
    void createTask__should_throw_exception_when_course_status_is_not_building() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.PUBLISHED);

        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Exercício inválido");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        ApplicationRulesException exception = assertThrows(ApplicationRulesException.class, () -> {
            taskService.createTask(newTaskDTO);
        });

        assertTrue(exception.getErrors().stream().anyMatch(e ->
                e.getField().equals("courseId") && e.getMessage().contains("BUILDING")));
    }

    @Test
    void createTask__should_throw_exception_when_statement_already_exists_in_course() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("Tarefa duplicada");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.existsByStatementAndCourseId("Tarefa duplicada", 1L)).thenReturn(true);
        when(taskRepository.findByCourseOrderByOrderAsc(course)).thenReturn(List.of());

        ApplicationRulesException exception = assertThrows(ApplicationRulesException.class, () -> {
            taskService.createTask(newTaskDTO);
        });

        assertTrue(exception.getErrors().stream().anyMatch(e ->
                e.getField().equals("statement") && e.getMessage().contains("atividade com esse enunciado")));
    }

    @Test
    void createTask__should_throw_exception_when_multiple_choice_has_no_incorrect_option() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        List<NewOptionDTO> options = List.of(
                new NewOptionDTO("Opção 1", true),
                new NewOptionDTO("Opção 2", true),
                new NewOptionDTO("Opção 3", true)
        );

        NewTaskDTO dto = new NewTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Questão com opções inválidas");
        dto.setOrder(1);
        dto.setType(Type.MULTIPLE_CHOICE);
        dto.setOptions(options);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseOrderByOrderAsc(course)).thenReturn(List.of());
        when(taskRepository.existsByStatementAndCourseId(any(), any())).thenReturn(false);

        ApplicationRulesException exception = assertThrows(ApplicationRulesException.class, () -> {
            taskService.createTask(dto);
        });

        assertTrue(exception.getErrors().stream().anyMatch(e ->
                e.getField().equals("options") && e.getMessage().contains("pelo menos 2 corretas e 1 incorreta")));
    }

    @Test
    void createTask__should_throw_exception_when_statement_is_too_short() {
        Course course = new Course();
        course.setId(1L);
        course.setStatus(Status.BUILDING);

        NewTaskDTO newTaskDTO = new NewTaskDTO();
        newTaskDTO.setCourseId(1L);
        newTaskDTO.setStatement("abc");
        newTaskDTO.setOrder(1);
        newTaskDTO.setType(Type.OPEN_TEXT);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(taskRepository.findByCourseOrderByOrderAsc(course)).thenReturn(List.of());
        when(taskRepository.existsByStatementAndCourseId(any(), any())).thenReturn(false);

        ApplicationRulesException exception = assertThrows(ApplicationRulesException.class, () -> {
            taskService.createTask(newTaskDTO);
        });

        assertTrue(exception.getErrors().stream().anyMatch(e ->
                e.getField().equals("statement") && e.getMessage().contains("entre 4 e 255")));
    }
}


