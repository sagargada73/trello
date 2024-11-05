package com.Webapp.model;

import jakarta.persistence.*;

/**
 * Entity representing the relationship between a task and a user who is
 * watching it.
 * This is used to implement the task watching feature in the system.
 */
@Entity
@Table(name = "task_watchers")
public class Watcher {

    /**
     * Composite primary key for the Watcher entity.
     */
    @EmbeddedId
    private WatcherId id;

    /**
     * The task being watched.
     * The @MapsId annotation is used to map the taskId in the composite key.
     */
    @ManyToOne
    @MapsId("taskId")
    private Task task;

    /**
     * The user watching the task.
     * The @MapsId annotation is used to map the userId in the composite key.
     */
    @ManyToOne
    @MapsId("userId")
    private User user;

    /**
     * Default constructor required by JPA.
     */
    public Watcher() {
    }

    public Watcher(WatcherId id, Task task, User user) {
        this.id = id;
        this.task = task;
        this.user = user;
    }

    // Getters and Setters
    public WatcherId getId() {
        return id;
    }

    public void setId(WatcherId id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
        this.id.setTaskId(task.getId());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.id.setUserId(user.getId());
    }
}
