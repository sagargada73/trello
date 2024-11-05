package com.Webapp.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class TaskUpdateDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskState state;
    private Set<Label> labels;
    private List<ChecklistItem> checklistItems;
    private List<Long> userIds;

    // Getters and Setters
    // ...
    public List<ChecklistItem> getChecklistItems() {
        return checklistItems;
    }
    public String getDescription() {
        return description;
    }
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    public Set<Label> getLabels() {
        return labels;
    }
    public TaskState getState() {
        return state;
    }
    public String getTitle() {
        return title;
    }
    public List<Long> getUserIds() {  // Getter for user IDs
        return userIds;
    }
    public void setChecklistItems(List<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
    public void setState(TaskState state) {
        this.state = state;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserIds(List<Long> userIds) {  // Setter for user IDs
        this.userIds = userIds;
    }
    
}
