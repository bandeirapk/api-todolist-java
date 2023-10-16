package br.com.bandeiramagalhaes.todolist.task;

import br.com.bandeiramagalhaes.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody TaskModel newTask, HttpServletRequest request) {
    var userId = request.getAttribute("userId");
    newTask.setUserId((UUID) userId);

    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(newTask.getStartAt()) || currentDate.isAfter(newTask.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/término não pode ser menor que a data atual");
    }

    if (newTask.getStartAt().isAfter(newTask.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início não pode ser maior que a data de término");
    }

    return ResponseEntity.status(HttpStatus.OK).body(taskRepository.save(newTask));
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request) {
    var userId = request.getAttribute("userId");
    return this.taskRepository.findAllByUserId((UUID) userId);
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel task, @PathVariable UUID id, HttpServletRequest request) {
    var userId = request.getAttribute("userId");
    var actuallyTask = this.taskRepository.findById(id).orElse(null);

    if (actuallyTask == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
    }

    if (!actuallyTask.getUserId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você não tem permissão para alterar essa tarefa");
    }

    Utils.copyNonNullProperties(task, actuallyTask);

    assert actuallyTask != null;
    return ResponseEntity.ok().body(this.taskRepository.save(actuallyTask));
  }
}
