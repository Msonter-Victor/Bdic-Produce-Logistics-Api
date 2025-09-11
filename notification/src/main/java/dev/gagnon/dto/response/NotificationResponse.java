package dev.gagnon.dto.response;

import dev.gagnon.data.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private String email;
    private String type;
    private String createdAt;
    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.createdAt = notification.getCreatedAt().toString();
        this.type = notification.getType().toString();
        this.email = notification.getRecipient().getEmail();
    }
}
