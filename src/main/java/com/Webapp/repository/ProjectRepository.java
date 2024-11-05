package com.Webapp.repository;

import com.Webapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    /**
     * Find a Project by its name.
     *
     * @param name The name of the project to search for
     * @return An Optional containing the Project if found, or an empty Optional if
     *         not found
     */
    Optional<Project> findByName(String name);
}
