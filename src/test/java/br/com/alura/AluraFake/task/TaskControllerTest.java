package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.util.ApplicationRulesException;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private NewTaskDTO validTaskDTO;

    @BeforeEach
    void setup() {
        validTaskDTO = new NewTaskDTO();
        validTaskDTO.setCourseId(1L);
        validTaskDTO.setOrder(1);
        validTaskDTO.setStatement("Enunciado válido");
    }

    @Test
    void newOpenTextExercise_shouldReturnCreated_whenValid() throws Exception {
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(NewTaskDTO.class));
    }

    @Test
    void newOpenTextExercise_shouldReturnBadRequest_whenApplicationRulesExceptionThrown() throws Exception {
        List<ErrorItemDTO> errors = List.of(new ErrorItemDTO("courseId", "Curso não está em modo BUILDING"));
        doThrow(new ApplicationRulesException(errors)).when(taskService).createTask(any(NewTaskDTO.class));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("courseId"))
                .andExpect(jsonPath("$[0].message").value("Curso não está em modo BUILDING"));
    }

    @Test
    void newSingleChoice_shouldReturnCreated_whenValid() throws Exception {
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(NewTaskDTO.class));
    }

    @Test
    void newSingleChoice_shouldReturnBadRequest_whenApplicationRulesExceptionThrown() throws Exception {
        List<ErrorItemDTO> errors = List.of(new ErrorItemDTO("options", "SingleChoice deve ter exatamente 1 opção correta"));
        doThrow(new ApplicationRulesException(errors)).when(taskService).createTask(any(NewTaskDTO.class));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").value("SingleChoice deve ter exatamente 1 opção correta"));
    }

    @Test
    void newMultipleChoice_shouldReturnCreated_whenValid() throws Exception {
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDTO)))
                .andExpect(status().isCreated());

        verify(taskService).createTask(any(NewTaskDTO.class));
    }

    @Test
    void newMultipleChoice_shouldReturnBadRequest_whenApplicationRulesExceptionThrown() throws Exception {
        List<ErrorItemDTO> errors = List.of(new ErrorItemDTO("options", "MultipleChoice deve ter pelo menos 2 corretas e 1 incorreta"));
        doThrow(new ApplicationRulesException(errors)).when(taskService).createTask(any(NewTaskDTO.class));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").value("MultipleChoice deve ter pelo menos 2 corretas e 1 incorreta"));
    }


}




