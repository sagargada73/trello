package com.Webapp.controller;

import com.Webapp.command.CommandInvoker;
import com.Webapp.command.CreateTaskCommand;
import com.Webapp.command.UpdateTaskCommand;
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

/**
 * REST controller for managing tasks.
 */
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

    @Autowired
    private CommandInvoker commandInvoker;

    /**
     * Create a new task.
     *
     * @param task The task object to be created.
     * @return ResponseEntity containing created task.
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        CreateTaskCommand command = new CreateTaskCommand(taskRepository, task);
        commandInvoker.executeCommand(command);
        return ResponseEntity.ok(task);
    }

    /**
     * Update an existing task by its ID.
     *
     * @param taskId        The ID of the task to update.
     * @param taskUpdateDTO The DTO containing updated fields.
     * @return ResponseEntity containing updated task.
     */
    @PutMapping("/{taskId}")
    @Transactional
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @RequestBody TaskUpdateDTO taskUpdateDTO) {
        // Fetch existing task or throw exception if not found.
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
        List<String> oldItems = existingTask.getChecklistItems().stream().map(ChecklistItem::getDescription)
                .collect(Collectors.toList());
        List<String> newItems = taskUpdateDTO.getChecklistItems().stream().map(ChecklistItem::getDescription)
                .collect(Collectors.toList());
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
            notification.setMessage(
                    "Task '" + updatedTask.getTitle() + "' has been updated. Changes: " + String.join(", ", changes));
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
        UpdateTaskCommand command = new UpdateTaskCommand(taskRepository, taskId, existingTask);
        commandInvoker.executeCommand(command);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Retrieve unread notifications for a specific user by their ID.
     *
     * @param userId The ID of the User whose notifications are being retrieved.
     * @return ResponseEntity containing list of unread notifications or not found
     *         status if User doesn't exist.
     */
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Notification> notifications = notificationService.getUnreadNotifications(user);// Fetch unread
                                                                                                // notifications for
                                                                                                // User.
            return ResponseEntity.ok(notifications);// Return list of notifications as response entity.
        }
        return ResponseEntity.notFound().build();// Return not found status if User doesn't exist.
    }

    @PostMapping("/undo")
    public ResponseEntity<?> undoLastOperation() {
        commandInvoker.undoLastCommand();
        return ResponseEntity.ok("Last operation undone");
    }

    /**
     * Add a label to a specific Task by its ID.
     *
     * @param taskId The ID of the Task to which a label is being added.
     * @param label  The Label object containing information about what label to
     *               add.
     * @return Updated Task object with added label.
     */
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

            /* Add label to current Task */
            /* Return updated Task object after saving changes */
            /* If no such Task exists, throw an exception */
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found");
        }
    }

    /**
     * Add a checklist item to a specific task.
     *
     * @param taskId        The ID of the task to which the checklist item will be
     *                      added.
     * @param checklistItem The checklist item to be added.
     * @return The updated task with the new checklist item.
     * @throws RuntimeException if the task is not found.
     */
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

    /**
     * Update the due date of a specific task.
     *
     * @param taskId  The ID of the task to update.
     * @param dueDate The new due date for the task.
     * @return The updated task with the new due date.
     * @throws RuntimeException if the task is not found.
     */
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

    /**
     * Retrieve all tasks in the system.
     *
     * @return A list of all tasks.
     */
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Add a watcher to a specific task.
     *
     * @param taskId  The ID of the task to which the watcher will be added.
     * @param request A map containing the user ID of the watcher to be added.
     * @return ResponseEntity with appropriate status and message.
     */
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

    /**
     * Remove a watcher from a specific task.
     *
     * @param taskId The ID of the task from which the watcher will be removed.
     * @param userId The ID of the user to be removed as a watcher.
     * @return A string indicating the success of the operation.
     * @throws RuntimeException if either the task or user is not found.
     */
    @DeleteMapping("/{taskId}/watchers/{userId}")
    public String removeWatcher(@PathVariable Long taskId, @PathVariable Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        task.removeWatcher(user);
        taskRepository.save(task);
        return "User removed from watchers";
    }

    /**
     * Get a map of tasks and their watchers for a specific project.
     *
     * @param projectId The ID of the project for which to retrieve task watchers.
     * @return ResponseEntity containing a map of task titles to lists of watcher
     *         names.
     */
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

    /**
     * Get all watchers for a specific task.
     *
     * @param taskId The ID of the task for which to retrieve watchers.
     * @return A set of users watching the specified task.
     * @throws RuntimeException if the task is not found.
     */
    @GetMapping("/{taskId}/watchers")
    public Set<User> getWatchers(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        return task.getWatchers();
    }

}
