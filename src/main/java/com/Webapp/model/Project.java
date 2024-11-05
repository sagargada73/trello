package com.Webapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a project in the system.
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the project

    private String name; // Name of the project
    private String description; // Description of the project

    /**
     * Many-to-Many relationship with User entity.
     * This represents the users associated with this project.
     */
    @ManyToMany
    @JoinTable(name = "project_users", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

    /**
     * Creation date of the project.
     * This field is automatically set by the database and cannot be updated.
     */
    @Column(name = "created_date", updatable = false, insertable = false)
    private LocalDateTime createdDate;

    /**
     * One-to-Many relationship with Task entity.
     * This represents the tasks associated with this project.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<>();

    /**
     * Default constructor required by JPA.
     */
    public Project() {
    }

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    // Note: There's no setter for createdDate as it's managed by the database
}