package com.Webapp.model;

import java.util.List;
import java.util.concurrent.Flow;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User implements Flow.Subscriber<Task> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;  // Add email field

    @Transient
    private Flow.Subscription subscription;

    @ManyToMany(mappedBy = "users")
    private List<Project> projects;

    // Default constructor
    public User() {}

    // Constructor with name and email
    public User(String name, String email) {
        this.name = name;
        this.email = email; // Initialize email
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email; // Getter for email
    }

    public void setEmail(String email) {
        this.email = email; // Setter for email
    }

    // Watcher Method
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }
    @Override
    public void onNext(Task task) {
        System.out.println("User " + name + " notified of task update: " + task.getTitle());
        // Additional notification logic can go here
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("Error in task update notification: " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Task updates complete for user " + name);
    }
}