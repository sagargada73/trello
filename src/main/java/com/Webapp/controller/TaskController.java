package com.Webapp.controller;

import com.Webapp.model.*;
import com.Webapp.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WatcherRepository watcherRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @Transactional
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

    @PostMapping("/{taskId}/watchers")
    public ResponseEntity<?> addWatcher(@PathVariable Long taskId, @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");

        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (optionalUser.isPresent() && optionalTask.isPresent()) {
            User user = optionalUser.get();
            Task task = optionalTask.get();
            Project project = task.getProject();

            // Fetch users associated with the project
            List<User> projectUsers = new ArrayList<>(project.getUsers());

            // Check if the user is part of the project's users
            if (!projectUsers.contains(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("User is not assigned to this project and cannot watch the task.");
            }

            WatcherId watcherId = new WatcherId(taskId, userId);
            if (watcherRepository.existsById(watcherId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User is already watching the task.");
            }

            Watcher watcher = new Watcher(watcherId, task, user);
            watcherRepository.save(watcher);
            return ResponseEntity.ok("User added as watcher.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task or User not found.");
    }

    @DeleteMapping("/{taskId}/watchers/{userId}")
    public String removeWatcher(@PathVariable Long taskId, @PathVariable Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        task.removeWatcher(user);
        taskRepository.save(task);
        return "User removed from watchers";
    }

    @GetMapping("/projects/{projectId}/task-watchers")
    public ResponseEntity<Map<String, List<String>>> getTaskWatchersByProject(@PathVariable Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
    
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();
            Map<String, List<String>> taskWatchersMap = new HashMap<>();
    
            // Retrieve all tasks for the project
            List<Task> tasks = taskRepository.findByProject(project);
    
            // For each task, find watchers and map them
            for (Task task : tasks) {
                List<Watcher> taskWatchers = watcherRepository.findByTask(task);
                List<String> watcherNames = new ArrayList<>();
                
                for (Watcher watcher : taskWatchers) {
                    watcherNames.add(watcher.getUser().getName()); // Collect watcher names
                }
    
                // Add task and its watchers to the map
                taskWatchersMap.put(task.getTitle(), watcherNames);
            }
    
            return ResponseEntity.ok(taskWatchersMap);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{taskId}/watchers")
    public Set<User> getWatchers(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        return task.getWatchers();
    }

}
