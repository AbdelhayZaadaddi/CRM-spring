package com.GL.CRM.task.dto;

import com.GL.CRM.task.entity.TaskPriority;
import com.GL.CRM.task.entity.TaskStatus;
import com.GL.CRM.task.entity.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Task type is required")
    private TaskType type;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime deadline;

    private Long assignedUserId;
}
