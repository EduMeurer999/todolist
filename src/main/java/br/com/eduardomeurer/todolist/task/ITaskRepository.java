package br.com.eduardomeurer.todolist.task;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends  JpaRepository<TaskModel, UUID>{
    List<TaskModel>findByIdUser(UUID id);
    TaskModel findByIdAndIdUser(UUID id, UUID idUser);
}
