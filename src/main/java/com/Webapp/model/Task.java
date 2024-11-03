package com.Webapp.model;

import java.util.concurrent.Flow;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tasks")
public class Task implements Flow.Publisher<Task>{ // Implementing Flow.Publisher

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TaskState state = TaskState.TO_DO; // Default state

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "created_date", updatable = false, insertable = false)
    private LocalDateTime createdDate;

    @ManyToMany
    @JoinTable(
        name = "task_users",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers;

    // Connection to watcher table
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "task_watchers",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> watchers = new HashSet<>();

    @Transient
    private final List<Flow.Subscriber<? super Task>> subscribers = new ArrayList<>();

    // Constructors, Getters, and Setters
    public Task() {}

    public Task(String title, String description, TaskState state) {
        this.title = title;
        this.description = description;
        this.state = state;
    }

    @ManyToMany
    @JoinTable(
        name = "task_labels",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> checklistItems = new ArrayList<>();

        public Long getId() {
            return id;
        }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public Set<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
    public void setChecklistItems(List<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    public List<ChecklistItem> getChecklistItems() {
        return checklistItems;
    }
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    public Set<Label> getLabels() {
        return labels;
    }

    // Watcher Code
    public Set<User> getWatchers() {
        return watchers;
    }

    public void addWatcher(User user) {
        watchers.add(user);
    }

    public void removeWatcher(User user) {
        watchers.remove(user);
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Task> subscriber) {
        subscribers.add(subscriber);
        subscriber.onSubscribe(new TaskSubscription(subscriber, this));
    }

    private void notifyObservers() {
        for (Flow.Subscriber<? super Task> subscriber : subscribers) {
            subscriber.onNext(this);
        }
    }

    public void updateTask(String newTitle, String newDescription) {
        this.title = newTitle;
        this.description = newDescription;
        notifyObservers();
    }


    // Subscription inner class to handle flow control
    private static class TaskSubscription implements Flow.Subscription {
        private final Flow.Subscriber<? super Task> subscriber;
        private final Task task;

        public TaskSubscription(Flow.Subscriber<? super Task> subscriber, Task task) {
            this.subscriber = subscriber;
            this.task = task;
        }

        @Override
        public void request(long n) {
            subscriber.onNext(task);
        }

        @Override
        public void cancel() {
            // Handle cancellation logic if needed
        }
    }
}
