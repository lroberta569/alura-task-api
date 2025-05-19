package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.ApplicationRulesException;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    public TaskRepository taskRepository;
    public OptionRepository optionRepository;
    public CourseRepository courseRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, OptionRepository optionRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.optionRepository = optionRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Cria uma nova tarefa com base nos dados recebidos via DTO.
     * Realiza a validação dos dados, instancia a tarefa conforme o tipo e salva as opções
     *
     * @param newTaskDTO DTO contendo os dados da nova tarefa
     * @throws ApplicationRulesException se houver erros de validação
     */
    public void createTask(NewTaskDTO newTaskDTO) {
        validateTask(newTaskDTO);

        Course course = courseRepository.findById(newTaskDTO.getCourseId()).orElseThrow(() ->
                new ApplicationRulesException(List.of(new ErrorItemDTO("courseId", "Curso não encontrado."))));

        Task task;
        switch (newTaskDTO.getType()) {
            case OPEN_TEXT:
                task = new OpenTextTask();
                break;
            case SINGLE_CHOICE:
                task = new SingleChoiceTask();
                break;
            case MULTIPLE_CHOICE:
                task = new MultipleChoiceTask();
                break;
            default:
                throw new ApplicationRulesException(
                        List.of(new ErrorItemDTO("type", "Tipo de tarefa inválido."))
                );
        }
        task.setStatement(newTaskDTO.getStatement());
        task.setCourse(course);
        task.setOrder(newTaskDTO.getOrder());
        task.setType(newTaskDTO.getType());

        task = taskRepository.save(task);

        if (newTaskDTO.getOptions() != null && !newTaskDTO.getOptions().isEmpty()) {
            List<Option> options = new ArrayList<>();
            for (NewOptionDTO dto : newTaskDTO.getOptions()) {
                Option option = new Option();
                option.setTask(task);
                option.setOption(dto.getOption());
                option.setCorrect(dto.isCorrect());
                options.add(option);
            }
            optionRepository.saveAll(options);
        }
    }

    /**
     * Valida os dados fornecidos para criação de uma nova tarefa.
     * Inclui validações de campos obrigatórios, tipo da tarefa, ordem sequencial e opções.
     *
     * @param newTaskDTO DTO com os dados da tarefa a ser validada
     * @throws ApplicationRulesException se houver qualquer erro de validação
     */
    public void validateTask(NewTaskDTO newTaskDTO) {
        List<ErrorItemDTO> errors = new ArrayList<>();

        if (newTaskDTO.getStatement().length() < 4 || newTaskDTO.getStatement().length() > 255) {
            errors.add(new ErrorItemDTO("statement", "O enunciado deve ter entre 4 e 255 caracteres."));
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

        int expectedNextOrder = existingTask.size() + 1;
        if (newTaskDTO.getOrder() > expectedNextOrder) {
            errors.add(new ErrorItemDTO("order", "A ordem das atividades deve ser contínua. Próxima ordem esperada: " + expectedNextOrder));
        }

        resequenceTasks(course, newTaskDTO.getOrder());

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

        if (!errors.isEmpty()) {
            throw new ApplicationRulesException(errors);
        }
    }

    /**
     * Atualiza a ordem das tarefas existentes em um curso, garantindo que a nova tarefa se encaixe corretamente
     * sem que haja conflitos de ordenação.
     *
     * @param course curso ao qual as tarefas pertencem
     * @param newOrder ordem desejada para a nova tarefa
     */
    public void resequenceTasks(Course course, Integer newOrder) {
        List<Task> existingTasks = taskRepository.findByCourseOrderByOrderAsc(course);
        List<Task> updatedTasks = new ArrayList<>();

        for (Task task : existingTasks) {
            if (task.getOrder() >= newOrder) {
                task.setOrder(task.getOrder() + 1);
                updatedTasks.add(task);
            }
        }

        if (!updatedTasks.isEmpty()) {
            taskRepository.saveAll(updatedTasks);
        }

    }

    /**
     * Valida uma tarefa do tipo MultipleChoice, incluindo a quantidade de opções,
     * número mínimo de corretas e incorretas, e a qualidade dos textos.
     *
     * @param newTaskDTO DTO com os dados da tarefa MultipleChoice
     * @throws ApplicationRulesException se as regras de validação forem violadas
     */
    private void validateMultipleChoice(NewTaskDTO newTaskDTO) {
        List<NewOptionDTO> options = newTaskDTO.getOptions();
        List<ErrorItemDTO> errors = new ArrayList<>();

        if (options == null || options.size() < 3 || options.size() > 5) {
            errors.add(new ErrorItemDTO("options", "MultipleChoice deve ter entre 3 e 5 opções."));
        }

        long correctCount = options.stream().filter(NewOptionDTO::isCorrect).count();
        long incorrectCount = options.size() - correctCount;

        if (correctCount < 2 || incorrectCount < 1) {
            errors.add(new ErrorItemDTO("options", "MultipleChoice deve ter pelo menos 2 corretas e 1 incorreta."));
        }

        validateOptionTexts(options, newTaskDTO.getStatement(), errors);

        if (!errors.isEmpty()) {
            throw new ApplicationRulesException(errors);
        }
    }

    /**
     * Valida uma tarefa do tipo SingleChoice, garantindo quantidade correta de opções,
     * uma única opção correta, e textos válidos.
     *
     * @param newTaskDTO DTO com os dados da tarefa SingleChoice
     * @throws ApplicationRulesException se as regras de validação forem violadas
     */
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

        validateOptionTexts(options, newTaskDTO.getStatement(), errors);

        if (!errors.isEmpty()) {
            throw new ApplicationRulesException(errors);
        }

    }

    /**
     * Valida o conteúdo textual das opções da tarefa, garantindo tamanho adequado,
     * unicidade, e que não sejam iguais ao enunciado.
     *
     * @param options lista de opções da tarefa
     * @param statement enunciado da tarefa
     * @param errors lista acumulada de erros de validação
     */
    public void validateOptionTexts(List<NewOptionDTO> options, String statement, List<ErrorItemDTO> errors) {
        Set<String> uniqueTexts = new HashSet<>();

        for (int i = 0; i < options.size(); i++) {
            String text = options.get(i).getOption();

            if (text.length() < 4 || text.length() > 80) {
                errors.add(new ErrorItemDTO("options[" + i + "]", "Texto da opção deve ter entre 4 e 80 caracteres."));
            }

            if (text.equalsIgnoreCase(statement)) {
                errors.add(new ErrorItemDTO("options[" + i + "]", "Opção não pode ser igual ao enunciado."));
            }

            if (!uniqueTexts.add(text)) {
                errors.add(new ErrorItemDTO("options[" + i + "]", "Todas as opções devem ter textos diferentes."));
            }
        }
    }
}

