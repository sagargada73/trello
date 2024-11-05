package com.Webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Webapp.model.Notification;
import com.Webapp.model.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Find all unread notifications for a specific user.
     *
     * @param user The user whose unread notifications are to be retrieved
     * @return A list of unread Notification entities for the specified user
     */
    List<Notification> findByUserAndIsReadFalse(User user); // For unread notifications
}
