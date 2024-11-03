package com.Webapp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class WatcherId implements Serializable {
    private Long taskId;
    private Long userId;

    // Default constructor
    public WatcherId() {}

    public WatcherId(Long taskId, Long userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WatcherId)) return false;
        WatcherId watcherId = (WatcherId) o;
        return Objects.equals(taskId, watcherId.taskId) &&
               Objects.equals(userId, watcherId.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId);
    }
}
