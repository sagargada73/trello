package com.Webapp.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

/**
 * Embeddable class representing a composite key for the Watcher entity.
 * This class combines taskId and userId to form a unique identifier for a watcher.
 */
@Embeddable
public class WatcherId implements Serializable {
    private Long taskId;  // ID of the task being watched
    private Long userId;  // ID of the user watching the task

    /**
     * Default constructor required by JPA.
     */
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

    /**
     * Compares this WatcherId with another object for equality.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WatcherId)) return false;
        WatcherId watcherId = (WatcherId) o;
        return Objects.equals(taskId, watcherId.taskId) &&
               Objects.equals(userId, watcherId.userId);
    }

    /**
     * Generates a hash code for this WatcherId.
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId);
    }
}