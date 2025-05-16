package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.ApplicationRulesException;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService {
    public TaskRepository taskRepository;
    public OptionRepository optionRepository;
    public CourseRepository courseRepository;

    public void createTask(NewTaskDTO newTaskDTO) {


    }

    public void validateTask(NewTaskDTO newTaskDTO) {
        List<ErrorItemDTO> errors = new ArrayList<>();

        if (newTaskDTO.getStatement().length() < 4 || newTaskDTO.getStatement().length() > 255) {
            errors.add(new ErrorItemDTO("stratement", "O enunciado deve ter entre 4 e 255 caracteres."));
        }
        if (newTaskDTO.getOrder() == null || newTaskDTO.getOrder() < 1) {
            errors.add(new ErrorItemDTO("order", "A ordem deve ser maior que 0."));
        }

        Course course = courseRepository.findById(newTaskDTO.getCourseId())
                .orElseThrow(() -> new ApplicationRulesException(
                        List.of(new ErrorItemDTO("courseId", "Curso não encontrado."))
                ));

        if (!course.getStatus().equals(Status.BUILDING)) {
            errors.add(new ErrorItemDTO("courseId", "Atividades só podem ser adicionadas em cursos com status BUILDING."));
        }

        if (taskRepository.existsByStatementAndCourseId(newTaskDTO.getStatement(), newTaskDTO.getCourseId())) {
            errors.add(new ErrorItemDTO("statement", "Já existe uma atividade com esse enunciado no curso."));
        }

        List<Task> existingTask = taskRepository.findByCourseOrderByOrderAsc(course);
        for (Task task : existingTask) {
            if (task.getOrder() >= newTaskDTO.getOrder()) {
                task.setOrder(newTaskDTO.getOrder() + 1);
            }
        }
        if (newTaskDTO.getType() == Type.OPEN_TEXT) {
            if (newTaskDTO.getOptions() != null && !newTaskDTO.getOptions().isEmpty()) {
                errors.add(new ErrorItemDTO("options", "Atividade OpenText não pode ter opções"));
            }
        } else if (newTaskDTO.getType() == Type.SINGLE_CHOICE) {
            validateSingleChoice(newTaskDTO);
        } else if (newTaskDTO.getType() == Type.MULTIPLE_CHOICE) {
            validateMultipleChoice(newTaskDTO);
        } else {
            errors.add(new ErrorItemDTO("type", "Tipo de atividade inválido"));
        }
    }

    private void validateMultipleChoice(NewTaskDTO newTaskDTO) {
    }

    private void validateSingleChoice(NewTaskDTO newTaskDTO) {
        List<NewOptionDTO> options = newTaskDTO.getOptions();
        List<ErrorItemDTO> errors = new ArrayList<>();

        if (options == null || options.size() < 2 || options.size() > 5) {
            errors.add(new ErrorItemDTO("options", "SingleChoice deve ter entre 2 e 5 opções."));
        }

        long correctCount = options == null ? 0 : options.stream().filter(NewOptionDTO::isCorrect).count();
        if (correctCount != 1) {
            errors.add(new ErrorItemDTO("options", "SingleChoice deve ter exatamente 1 opção correta."));
        }

        Set<String> uniqueTexts = new HashSet<>();
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                NewOptionDTO option = options.get(i);
                String optionText = option.getOption();

                if (optionText.length() < 4 || optionText.length() > 80) {
                    errors.add(new ErrorItemDTO("options[" + i + "]", "Texto da opção deve ter entre 4 e 80 caracteres."));
                }
                if (optionText.equalsIgnoreCase(newTaskDTO.getStatement())) {
                    errors.add(new ErrorItemDTO("options[" + i + "]", "Opção não pode ser igual ao enunciado."));
                }
                if (!uniqueTexts.add(optionText)) {
                    errors.add(new ErrorItemDTO("options[" + i + "]", "Todas as opções devem ter textos diferentes."));

                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ApplicationRulesException(errors);
        }

    }
}

