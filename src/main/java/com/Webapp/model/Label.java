package com.Webapp.model;

import jakarta.persistence.*;

/**
 * Entity representing a label that can be associated with tasks.
 */
@Entity
@Table(name = "labels")
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the label

    @Column(unique = true)
    private String name; // Unique name of the label

    /**
     * Default constructor required by JPA.
     */
    public Label() {
    }

    public Label(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}