package com.Webapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "checklist_items")
public class ChecklistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference
    private Task task;

    private String description;
    private Boolean isCompleted;

    // Default constructor
    public ChecklistItem() {}

    public ChecklistItem(String description, boolean isCompleted) {
        this.description = description;
        this.isCompleted = isCompleted;
    }
    // Constructor with fields
    public ChecklistItem(Task task, String description, Boolean isCompleted) {
        this.task = task;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }
    public Long getId() {
        return id;
    }
    public Boolean getIsCompleted() {
        return isCompleted;
    }
    public Task getTask() {
        return task;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public void setTask(Task task) {
        this.task = task;
    }
}
