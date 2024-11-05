package com.Webapp.service;

import com.Webapp.model.Notification;
import com.Webapp.model.Task;
import com.Webapp.model.User;
import com.Webapp.repository.NotificationRepository;
import com.Webapp.repository.WatcherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final WatcherRepository watcherRepository;

    public NotificationService(WatcherRepository watcherRepository) {
        this.watcherRepository = watcherRepository;
    }
    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(Task task, User user, String message) {
        Notification notification = new Notification();
        notification.setTask(task);
        notification.setUser(user);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notification);

            // Print the notification details to the console
    System.out.println("Created notification for user: " + user.getName() + 
    " with message: " + message + 
    " for task: " + task.getTitle());
    }
    
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalse(user);
    }
}