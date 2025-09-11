package dev.gagnon.service;

import dev.gagnon.dto.request.NotificationEvent;
import dev.gagnon.dto.request.ReadNotificationRequest;
import dev.gagnon.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void sendNotification(NotificationEvent event);

    void readNotification(ReadNotificationRequest request);

    void readAllNotification(String email);

    NotificationResponse viewNotification(Long id);

    List<NotificationResponse> viewUserNotifications(String email);

    void deleteNotification(Long id);

    void deleteUserNotifications(String email);

    void deleteAllNotifications();

    List<NotificationResponse> viewUserUnReadNotifications(String email);
}
