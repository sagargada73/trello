package com.Webapp.model;

import java.util.List;
import java.util.concurrent.Flow;

import jakarta.persistence.*;

/**
 * Entity representing a user in the system.
 * This class also implements Flow.Subscriber to support reactive programming
 * for task updates.
 */
@Entity
@Table(name = "users")
public class User implements Flow.Subscriber<Task> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    @Transient
    private Flow.Subscription subscription; // Subscription for reactive programming

    @ManyToMany(mappedBy = "users")
    private List<Project> projects; // Projects associated with this user

        /**
     * Default constructor required by JPA.
     */
    public User() {
    }

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

    /**
     * Called when the User subscribes to task updates.
     * Part of the Flow.Subscriber interface implementation.
     *
     * @param subscription The subscription to task updates
     */
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    /**
     * Called when a subscribed task is updated.
     * Part of the Flow.Subscriber interface implementation.
     *
     * @param task The updated task
     */
    @Override
    public void onNext(Task task) {
        System.out.println("User " + name + " notified of task update: " + task.getTitle());
    }

    /**
     * Called when there's an error in the task update stream.
     * Part of the Flow.Subscriber interface implementation.
     *
     * @param throwable The error that occurred
     */
    @Override
    public void onError(Throwable throwable) {
        System.err.println("Error in task update notification: " + throwable.getMessage());
    }

    /**
     * Called when the task update stream is completed.
     * Part of the Flow.Subscriber interface implementation.
     */
    @Override
    public void onComplete() {
        System.out.println("Task updates complete for user " + name);
    }

    /**
     * Handles receiving a notification.
     *
     * @param message The notification message
     */
    public void receiveNotification(String message) {
        // Logic for handling notifications (e.g., storing in a list, logging, etc.)
        System.out.println("Notification for " + this.name + ": " + message);
    }
}