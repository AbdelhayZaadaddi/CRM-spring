package com.GL.CRM.task.service;

import com.GL.CRM.task.dto.TaskRequest;
import com.GL.CRM.task.dto.TaskResponse;
import com.GL.CRM.task.entity.Task;
import com.GL.CRM.task.mapper.TaskMapper;
import com.GL.CRM.task.repository.TaskRepository;
import com.GL.CRM.user.entity.User;
import com.GL.CRM.user.repositry.UserRepositry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepositry userRepositry;

    public List<TaskResponse> getAll() {
        return taskRepository.findAll()
                .stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        return taskMapper.toResponse(task);
    }

    public TaskResponse create(TaskRequest request) {
        Task task = taskMapper.toEntity(request);
        if (request.getAssignedUserId() != null) {
            User user = userRepositry.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignedUser(user);
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        taskMapper.updateEntity(task, request);
        if (request.getAssignedUserId() != null) {
            User user = userRepositry.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            task.setAssignedUser(user);
        } else {
            task.setAssignedUser(null);
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse assignToUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        User user = userRepositry.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        task.setAssignedUser(user);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public TaskResponse unassign(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        task.setAssignedUser(null);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }
}
