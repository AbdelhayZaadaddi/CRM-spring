package com.GL.CRM;

import com.GL.CRM.task.dto.TaskRequest;
import com.GL.CRM.task.dto.TaskResponse;
import com.GL.CRM.task.entity.Task;
import com.GL.CRM.task.entity.TaskPriority;
import com.GL.CRM.task.entity.TaskStatus;
import com.GL.CRM.task.entity.TaskType;
import com.GL.CRM.task.mapper.TaskMapper;
import com.GL.CRM.task.repository.TaskRepository;
import com.GL.CRM.task.service.TaskService;
import com.GL.CRM.user.entity.User;
import com.GL.CRM.user.repositry.UserRepositry;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserRepositry userRepositry;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequest request;
    private TaskResponse response;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        task = Task.builder()
                .id(1L)
                .title("Follow up call")
                .description("Call the client about the new proposal")
                .type(TaskType.CALL)
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .deadline(LocalDateTime.now().plusDays(3))
                .assignedUser(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        request = new TaskRequest();
        request.setTitle("Follow up call");
        request.setDescription("Call the client about the new proposal");
        request.setType(TaskType.CALL);
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);
        request.setDeadline(LocalDateTime.now().plusDays(3));
        request.setAssignedUserId(1L);

        response = TaskResponse.builder()
                .id(1L)
                .title("Follow up call")
                .description("Call the client about the new proposal")
                .type(TaskType.CALL)
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .assignedUserId(1L)
                .assignedUserName("John Doe")
                .build();
    }

    @Test
    void getAll_shouldReturnListOfTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        List<TaskResponse> result = taskService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Follow up call");
    }

    @Test
    void getById_shouldReturnTask_whenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(TaskType.CALL);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.getById(99L));
    }

    @Test
    void create_shouldSaveAndReturnTask_withAssignedUser() {
        when(taskMapper.toEntity(request)).thenReturn(task);
        when(userRepositry.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.create(request);

        assertThat(result.getTitle()).isEqualTo("Follow up call");
        assertThat(result.getAssignedUserId()).isEqualTo(1L);
        verify(taskMapper).toEntity(request);
        verify(taskRepository).save(task);
    }

    @Test
    void create_shouldSaveAndReturnTask_withNoAssignedUser() {
        request.setAssignedUserId(null);
        Task unassignedTask = Task.builder()
                .id(2L)
                .title("Follow up call")
                .type(TaskType.CALL)
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .build();
        TaskResponse unassignedResponse = TaskResponse.builder()
                .id(2L)
                .title("Follow up call")
                .type(TaskType.CALL)
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskMapper.toEntity(request)).thenReturn(unassignedTask);
        when(taskRepository.save(unassignedTask)).thenReturn(unassignedTask);
        when(taskMapper.toResponse(unassignedTask)).thenReturn(unassignedResponse);

        TaskResponse result = taskService.create(request);

        assertThat(result.getAssignedUserId()).isNull();
        verify(userRepositry, never()).findById(any());
    }

    @Test
    void create_shouldThrow_whenAssignedUserNotFound() {
        when(taskMapper.toEntity(request)).thenReturn(task);
        when(userRepositry.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.create(request));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateAndReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepositry.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.update(1L, request);

        assertThat(result.getTitle()).isEqualTo("Follow up call");
        verify(taskMapper).updateEntity(task, request);
        verify(taskRepository).save(task);
    }

    @Test
    void update_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.update(99L, request));
    }

    @Test
    void assignToUser_shouldAssignAndReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepositry.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(response);

        TaskResponse result = taskService.assignToUser(1L, 1L);

        assertThat(result.getAssignedUserId()).isEqualTo(1L);
        verify(taskRepository).save(task);
    }

    @Test
    void assignToUser_shouldThrow_whenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.assignToUser(99L, 1L));
    }

    @Test
    void assignToUser_shouldThrow_whenUserNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepositry.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.assignToUser(1L, 99L));
    }

    @Test
    void unassign_shouldClearAssignedUserAndReturnTask() {
        TaskResponse unassignedResponse = TaskResponse.builder()
                .id(1L)
                .title("Follow up call")
                .type(TaskType.CALL)
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .assignedUserId(null)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(unassignedResponse);

        TaskResponse result = taskService.unassign(1L);

        assertThat(result.getAssignedUserId()).isNull();
        verify(taskRepository).save(task);
    }

    @Test
    void delete_shouldDeleteTask_whenExists() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenTaskNotFound() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> taskService.delete(99L));
        verify(taskRepository, never()).deleteById(any());
    }
}
