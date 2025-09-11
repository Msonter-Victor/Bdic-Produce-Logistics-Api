package dev.gagnon.service;

import dev.gagnon.data.NotificationType;
import dev.gagnon.data.model.Notification;
import dev.gagnon.data.model.User;
import dev.gagnon.data.repository.NotificationRepository;
import dev.gagnon.data.repository.UserRepository;
import dev.gagnon.dto.request.NotificationEvent;
import dev.gagnon.dto.request.ReadNotificationRequest;
import dev.gagnon.dto.response.NotificationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void sendNotification(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setRead(false);
        notification.setTitle(event.getTitle());
        notification.setMessage(event.getMessage());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setType(NotificationType.valueOf(event.getType()));
        User user = userRepository.findByEmail(event.getEmail());
        notification.setRecipient(user);
        notificationRepository.save(notification);
    }

    @Override
    public void readNotification(ReadNotificationRequest request) {
        Notification notification = notificationRepository.findById(request.getId())
                .orElseThrow(()->new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void readAllNotification(String email) {
        List<Notification> notifications = notificationRepository.getNotificationByUserEmail(email);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public NotificationResponse viewNotification(Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        assert notification != null;
        return new NotificationResponse(notification);
    }

    @Override
    public List<NotificationResponse> viewUserNotifications(String email) {
        List<Notification> notifications = notificationRepository.getNotificationByUserEmail(email);
        if(notifications.isEmpty())return List.of();
        return notifications.stream().map(NotificationResponse::new).toList();
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void deleteUserNotifications(String email) {
        List<Notification> notifications = notificationRepository.getNotificationByUserEmail(email);
        if(notifications.isEmpty())return;
        notificationRepository.deleteAll(notifications);
    }

    @Override
    public void deleteAllNotifications() {
        notificationRepository.deleteAll();
    }

    @Override
    public List<NotificationResponse> viewUserUnReadNotifications(String email) {
        List<Notification> notifications = notificationRepository.getNotificationByUserEmail(email);
        List<Notification>unReadNotifications=
                notifications.stream()
                        .filter(notification -> !notification.isRead())
                        .toList();
        if (unReadNotifications.isEmpty())return List.of();
        return unReadNotifications.stream().map(NotificationResponse::new).toList();
    }


}
