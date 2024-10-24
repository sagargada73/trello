package com.Webapp.controller;

import com.Webapp.model.Project;
import com.Webapp.model.User;
import com.Webapp.repository.ProjectRepository;
import com.Webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @PostMapping
    public Project createProject(@RequestBody Project project, @RequestParam List<Long> userIds) {
        // Fetch and associate users with the project
        Set<User> users = new HashSet<>(userRepository.findAllById(userIds));
        project.setUsers(users);
        return projectRepository.save(project);
    }

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
