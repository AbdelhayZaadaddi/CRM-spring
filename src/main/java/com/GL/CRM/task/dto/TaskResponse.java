package com.GL.CRM.task.dto;

import com.GL.CRM.task.entity.TaskPriority;
import com.GL.CRM.task.entity.TaskStatus;
import com.GL.CRM.task.entity.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskType type;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime deadline;
    private Long assignedUserId;
    private String assignedUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
