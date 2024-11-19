package com.Webapp.command;

import com.Webapp.model.Task;
import com.Webapp.repository.TaskRepository;

public class CreateTaskCommand implements Command {
    private final TaskRepository taskRepository;
    private final Task task;
    private Long createdTaskId;

    public CreateTaskCommand(TaskRepository taskRepository, Task task) {
        this.taskRepository = taskRepository;
        this.task = task;
    }

    @Override
    public void execute() {
        Task createdTask = taskRepository.save(task);
        this.createdTaskId = createdTask.getId();
    }

    @Override
    public void undo() {
        if (createdTaskId != null) {
            taskRepository.deleteById(createdTaskId);
        }
    }
}