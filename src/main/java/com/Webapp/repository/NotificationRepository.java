package com.Webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Webapp.model.Notification;
import com.Webapp.model.User;

import java.util.List;
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsReadFalse(User user); // For unread notifications
}

