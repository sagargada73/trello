package com.Webapp.repository;

import com.Webapp.model.Task;
import com.Webapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    /**
     * Find a Task by its ID.
     *
     * @param id The ID of the task to search for
     * @return An Optional containing the Task if found, or an empty Optional if not
     *         found
     */
    Optional<Task> findById(Long id);

    /**
     * Find all Tasks associated with a specific Project.
     *
     * @param project The Project for which to retrieve tasks
     * @return A List of Tasks associated with the given Project
     */
    List<Task> findByProject(Project project);
}
