package com.Webapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "task_watchers")
public class Watcher {

    @EmbeddedId
    private WatcherId id;

    @ManyToOne
    @MapsId("taskId")
    private Task task;

    @ManyToOne
    @MapsId("userId")
    private User user;
    
    public Watcher() {}

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
