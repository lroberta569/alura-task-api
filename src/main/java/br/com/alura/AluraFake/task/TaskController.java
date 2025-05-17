package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.util.ApplicationRulesException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Transactional
    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@Valid @RequestBody NewTaskDTO newTaskDTO) {
        newTaskDTO.setType(Type.OPEN_TEXT);
        try {
            taskService.createTask(newTaskDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ApplicationRulesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        }
    }

    @Transactional
    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice(@Valid @RequestBody NewTaskDTO newTaskDTO) {
        newTaskDTO.setType(Type.SINGLE_CHOICE);
        try {
            taskService.createTask(newTaskDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ApplicationRulesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        }
    }

    @Transactional
    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice(@Valid @RequestBody NewTaskDTO newTaskDTO) {
        newTaskDTO.setType(Type.MULTIPLE_CHOICE);
        try {
            taskService.createTask(newTaskDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (ApplicationRulesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrors());
        }
    }

}