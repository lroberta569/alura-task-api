package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.ApplicationRulesException;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new ApplicationRulesException(List.of(new ErrorItemDTO("courseId", "Curso não encontrado."))));

        if (!course.getStatus().equals(Status.BUILDING)) {
            throw new ApplicationRulesException(List.of(new ErrorItemDTO("status", "Curso só pode ser publicado se estiver com status BUILDING")));
        }

        List<Task> tasks = taskRepository.findByCourseOrderByOrderAsc(course);

        if (!hasAllTypes(tasks)) {
            throw new ApplicationRulesException(List.of(new ErrorItemDTO("tasks", "Curso deve ter pelo menos uma tarefa de cada tipo")));
        }

        if (!isSequentialOrder(tasks)) {
            throw new ApplicationRulesException(List.of(new ErrorItemDTO("order", "A ordem das tarefas deve ser contínua e começar em 1")));
        }

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    private boolean hasAllTypes(List<Task> tasks) {
        return tasks.stream().map(Task::getType).collect(Collectors.toSet())
                .containsAll(Set.of(Type.OPEN_TEXT, Type.SINGLE_CHOICE, Type.MULTIPLE_CHOICE));
    }

    private boolean isSequentialOrder(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (!tasks.get(i).getOrder().equals(i + 1)) return false;
        }
        return true;
    }

}
