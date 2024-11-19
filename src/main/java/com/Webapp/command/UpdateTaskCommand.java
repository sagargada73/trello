package com.Webapp.command;

import com.Webapp.model.Task;
import com.Webapp.repository.TaskRepository;

public class UpdateTaskCommand implements Command {
    private final TaskRepository taskRepository;
    private final Long taskId;
    private final Task updatedTask;
    private Task originalTask;

    public UpdateTaskCommand(TaskRepository taskRepository, Long taskId, Task updatedTask) {
        this.taskRepository = taskRepository;
        this.taskId = taskId;
        this.updatedTask = updatedTask;
    }

    @Override
    public void execute() {
        this.originalTask = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.save(updatedTask);
    }

    @Override
    public void undo() {
        if (originalTask != null) {
            taskRepository.save(originalTask);
        }
    }
}