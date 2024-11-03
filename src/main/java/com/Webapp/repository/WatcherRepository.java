package com.Webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Webapp.model.Watcher;
import com.Webapp.model.WatcherId;
import com.Webapp.model.Task;

import java.util.List;
@Repository
public interface WatcherRepository extends JpaRepository<Watcher, WatcherId> {
    List<Watcher> findByTask(Task task);
}
