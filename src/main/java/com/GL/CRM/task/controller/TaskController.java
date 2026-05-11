package com.GL.CRM.task.controller;

import com.GL.CRM.task.dto.TaskRequest;
import com.GL.CRM.task.dto.TaskResponse;
import com.GL.CRM.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll() {
        return ResponseEntity.ok(taskService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(201).body(taskService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.update(id, request));
    }

    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<TaskResponse> assignToUser(@PathVariable Long id,
                                                     @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.assignToUser(id, userId));
    }

    @PutMapping("/{id}/unassign")
    public ResponseEntity<TaskResponse> unassign(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.unassign(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
