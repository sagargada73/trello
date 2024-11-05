package com.Webapp.controller;

import com.Webapp.model.*;
import com.Webapp.repository.*;
import com.Webapp.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{taskId}")
    @Transactional
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @RequestBody TaskUpdateDTO taskUpdateDTO) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
    
        List<String> changes = new ArrayList<>();
    
        // Update title
        if (!Objects.equals(existingTask.getTitle(), taskUpdateDTO.getTitle())) {
            changes.add("Title updated from '" + existingTask.getTitle() + "' to '" + taskUpdateDTO.getTitle() + "'");
            existingTask.setTitle(taskUpdateDTO.getTitle());
        }
    
        // Update description
        if (!Objects.equals(existingTask.getDescription(), taskUpdateDTO.getDescription())) {
            changes.add("Description updated");
            existingTask.setDescription(taskUpdateDTO.getDescription());
        }
    
        // Update due date
        if (!Objects.equals(existingTask.getDueDate(), taskUpdateDTO.getDueDate())) {
            changes.add("Due date updated from " + existingTask.getDueDate() + " to " + taskUpdateDTO.getDueDate());
            existingTask.setDueDate(taskUpdateDTO.getDueDate());
        }
    
        // Update state
        if (existingTask.getState() != taskUpdateDTO.getState()) {
            changes.add("State updated from " + existingTask.getState() + " to " + taskUpdateDTO.getState());
            existingTask.setState(taskUpdateDTO.getState());
        }
    
        // Update labels
        Set<String> oldLabels = existingTask.getLabels().stream().map(Label::getName).collect(Collectors.toSet());
        Set<String> newLabels = taskUpdateDTO.getLabels().stream().map(Label::getName).collect(Collectors.toSet());
        if (!oldLabels.equals(newLabels)) {
            changes.add("Labels updated from " + oldLabels + " to " + newLabels);
            existingTask.getLabels().clear();
            for (Label label : taskUpdateDTO.getLabels()) {
                Label existingLabel = labelRepository.findByName(label.getName())
                        .orElseGet(() -> labelRepository.save(label));
                existingTask.getLabels().add(existingLabel);
            }
        }
    
        // Update checklist items
        List<String> oldItems = existingTask.getChecklistItems().stream().map(ChecklistItem::getDescription).collect(Collectors.toList());
        List<String> newItems = taskUpdateDTO.getChecklistItems().stream().map(ChecklistItem::getDescription).collect(Collectors.toList());
        if (!oldItems.equals(newItems)) {
            changes.add("Checklist items updated from " + oldItems + " to " + newItems);
            existingTask.getChecklistItems().clear();
            for (ChecklistItem item : taskUpdateDTO.getChecklistItems()) {
                item.setTask(existingTask);
                existingTask.getChecklistItems().add(checklistItemRepository.save(item));
            }
        }
    
        // Save the updated task
        Task updatedTask = taskRepository.save(existingTask);
    
        // Create notifications for watchers
        List<Watcher> watchers = watcherRepository.findByTask(updatedTask);
        for (Watcher watcher : watchers) {
            User user = watcher.getUser();
            Notification notification = new Notification();
            notification.setMessage("Task '" + updatedTask.getTitle() + "' has been updated. Changes: " + String.join(", ", changes));
            notification.setTimestamp(LocalDateTime.now());
            notification.setTask(updatedTask);
            notification.setUser(user);
            notificationRepository.save(notification);
        }
    
        // Log changes to console
        System.out.println("Task updated: " + updatedTask.getTitle());
        for (String change : changes) {
            System.out.println("- " + change);
        }
    
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Notification> notifications = notificationService.getUnreadNotifications(user);
            return ResponseEntity.ok(notifications);
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
