package br.com.bandeiramagalhaes.todolist.task;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
  List<TaskModel> findAllByUserId(UUID userId);
//  TaskModel findByIdAndIdUser(UUID id, UUID userId);
}
