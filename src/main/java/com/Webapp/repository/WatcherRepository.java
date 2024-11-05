package com.Webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Webapp.model.*;

import java.util.List;

@Repository
public interface WatcherRepository extends JpaRepository<Watcher, WatcherId> {
    /**
     * Find all Watchers associated with a specific Task.
     *
     * @param task The Task for which to retrieve watchers
     * @return A List of Watchers associated with the given Task
     */
    List<Watcher> findByTask(Task task);
}
