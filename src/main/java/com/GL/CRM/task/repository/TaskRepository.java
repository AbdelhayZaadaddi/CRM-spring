package com.GL.CRM.task.repository;

import com.GL.CRM.task.entity.Task;
import com.GL.CRM.task.entity.TaskStatus;
import com.GL.CRM.task.entity.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedUserId(Long userId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByType(TaskType type);

    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countGroupedByStatus();

    @Query("SELECT t.priority, COUNT(t) FROM Task t GROUP BY t.priority")
    List<Object[]> countGroupedByPriority();

    @Query("SELECT t.type, COUNT(t) FROM Task t GROUP BY t.type")
    List<Object[]> countGroupedByType();

    @Query("SELECT t.assignedUser.id, t.assignedUser.name, COUNT(t) FROM Task t WHERE t.assignedUser IS NOT NULL GROUP BY t.assignedUser.id, t.assignedUser.name ORDER BY COUNT(t) DESC")
    List<Object[]> countGroupedByUser();

    @Query("SELECT COUNT(t) FROM Task t WHERE t.deadline IS NOT NULL AND t.deadline < CURRENT_TIMESTAMP AND t.status NOT IN :excludedStatuses")
    long countOverdue(@Param("excludedStatuses") List<TaskStatus> excludedStatuses);
}
