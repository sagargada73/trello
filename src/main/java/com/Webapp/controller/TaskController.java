package com.Webapp.controller;

import com.Webapp.model.ChecklistItem;
import com.Webapp.model.Task;
import com.Webapp.model.TaskState;
import com.Webapp.model.Label;
import com.Webapp.repository.ChecklistItemRepository;
import com.Webapp.repository.LabelRepository;
import com.Webapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{id}/state")
    public ResponseEntity<Task> updateTaskState(@PathVariable Long id, @RequestParam TaskState state) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setState(state);
            return ResponseEntity.ok(taskRepository.save(task));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{taskId}/labels")
    public Task addLabelToTask(@PathVariable Long taskId, @RequestBody Label label) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            // Check if label already exists in the database or save it
            Optional<Label> existingLabel = labelRepository.findByName(label.getName());
            Label labelToAdd = existingLabel.orElseGet(() -> labelRepository.save(label));

            // Add label to the task
            task.getLabels().add(labelToAdd);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }
    

    @PostMapping("/{taskId}/checklist")
    public Task addChecklistItem(@PathVariable Long taskId, @RequestBody ChecklistItem checklistItem) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            // Set the task for the checklist item and save it
            checklistItem.setTask(task);
            checklistItemRepository.save(checklistItem);

            // Update task's checklist items
            task.getChecklistItems().add(checklistItem);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    @PutMapping("/{taskId}/duedate")
    public Task updateDueDate(@PathVariable Long taskId, @RequestParam LocalDateTime dueDate) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setDueDate(dueDate);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }


    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
