package com.GL.CRM.task.repository;

import com.GL.CRM.task.entity.Task;
import com.GL.CRM.task.entity.TaskStatus;
import com.GL.CRM.task.entity.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUserId(Long userId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByType(TaskType type);
}
