package com.Webapp.controller;

import com.Webapp.model.*;
import com.Webapp.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST controller for managing projects.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieve all projects.
     *
     * @return List of all projects
     */
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Retrieve users associated with a specific project.
     *
     * @param projectId ID of the project
     * @return ResponseEntity containing list of users or not found status
     */
    @GetMapping("/{projectId}/users")
    public ResponseEntity<List<User>> getUsersByProject(@PathVariable Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isPresent()) {
            List<User> users = new ArrayList<>(projectOptional.get().getUsers()); // Convert Set<User> to List<User>
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new project with associated users.
     *
     * @param project Project to be created
     * @param userIds List of user IDs to associate with the project
     * @return Created project
     */
    @PostMapping
    public Project createProject(@RequestBody Project project, @RequestParam List<Long> userIds) {
        // Fetch and associate users with the project
        Set<User> users = new HashSet<>(userRepository.findAllById(userIds));
        project.setUsers(users);
        return projectRepository.save(project);
    }

    /**
     * Add users to an existing project.
     *
     * @param id      Project ID
     * @param userIds List of user IDs to add to the project
     * @return ResponseEntity containing updated project or appropriate error status
     */
    @PutMapping("/{id}/add-users")
    public ResponseEntity<Project> addUsersToProject(@PathVariable Long id, @RequestBody List<Long> userIds) {
        Optional<Project> projectOptional = projectRepository.findById(id);

        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            // Fetch and add the new users
            List<User> newUsers = userRepository.findAllById(userIds);

            if (newUsers.size() != userIds.size()) {
                return ResponseEntity.badRequest().body(null); // Some user IDs were invalid
            }

            project.getUsers().addAll(newUsers); // Add new users to the existing list
            projectRepository.save(project);
            return ResponseEntity.ok(project);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove users from an existing project.
     *
     * @param id      Project ID
     * @param userIds List of user IDs to remove from the project
     * @return ResponseEntity containing updated project or appropriate error status
     */
    @PutMapping("/{id}/remove-users")
    public ResponseEntity<Project> removeUsersFromProject(@PathVariable Long id, @RequestBody List<Long> userIds) {
        Optional<Project> projectOptional = projectRepository.findById(id);

        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            // Fetch users to remove
            List<User> usersToRemove = userRepository.findAllById(userIds);

            if (usersToRemove.size() != userIds.size()) {
                return ResponseEntity.badRequest().body(null); // Some user IDs were invalid
            }

            project.getUsers().removeAll(usersToRemove); // Remove the users
            projectRepository.save(project);
            return ResponseEntity.ok(project);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a project.
     *
     * @param id Project ID to delete
     * @return ResponseEntity with no content if successful, or not found status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}