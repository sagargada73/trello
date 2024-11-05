package com.Webapp.model;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private LocalDateTime timestamp;
    private boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters, setters, and constructors
    public Long getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }
    public Task getTask() {
        return task;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public User getUser() {
        return user;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
    public void setTask(Task task) {
        this.task = task;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public void setUser(User user) {
        this.user = user;
    }
}

