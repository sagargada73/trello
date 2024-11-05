package com.Webapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

/**
 * Entity representing a checklist item within a task.
 */
@Entity
@Table(name = "checklist_items")
public class ChecklistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the checklist item

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private Task task; // The task to which this checklist item belongs

    private String description; // Description of the checklist item
    private Boolean isCompleted; // Completion status of the checklist item

    /**
     * Default constructor required by JPA.
     */
    public ChecklistItem() {
    }

    public ChecklistItem(String description, boolean isCompleted) {
        this.description = description;
        this.isCompleted = isCompleted;
    }
    
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