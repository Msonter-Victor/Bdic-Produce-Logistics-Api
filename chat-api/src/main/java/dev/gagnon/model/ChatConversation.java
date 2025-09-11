package dev.gagnon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversation {
    private String id;
    private String buyerEmail;
    private String vendorEmail;
    private String buyerName;
    private String vendorName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
    private boolean buyerOnline;
    private boolean vendorOnline;
}