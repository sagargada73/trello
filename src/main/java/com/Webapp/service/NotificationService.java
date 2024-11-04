package com.Webapp.service;

import com.Webapp.model.Task;
import com.Webapp.model.User;
import com.Webapp.model.Watcher;
import com.Webapp.repository.WatcherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final WatcherRepository watcherRepository;

    public NotificationService(WatcherRepository watcherRepository) {
        this.watcherRepository = watcherRepository;
    }

    public void notifyWatchers(Task task, String message) {
        // Get the list of Watchers for the task
        List<Watcher> watchers = watcherRepository.findByTask(task);
        System.out.println(watchers);
        // Convert Watchers to Users
        List<User> users = watchers.stream()
                                    .map(Watcher::getUser) // Assuming Watcher has a method getUser()
                                    .collect(Collectors.toList());
        System.out.println("Users: " + users);
        // Notify each User
        for (User user : users) {
            user.receiveNotification(message);
        }
    }
}


