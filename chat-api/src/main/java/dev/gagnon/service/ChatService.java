package dev.gagnon.service;

import dev.gagnon.dto.SendMessageRequest;
import dev.gagnon.model.ChatConversation;
import dev.gagnon.model.ChatMessage;
import dev.gagnon.model.UserOnlineStatus;
import dev.gagnon.repository.ChatMessageRepository;
import dev.gagnon.repository.UserOnlineStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserOnlineStatusRepository userOnlineStatusRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private com.pusher.rest.Pusher pusher;

    @Autowired
    private RestTemplate notificationRestTemplate;

    public ChatMessage sendMessage(SendMessageRequest request) {
        String conversationId = generateConversationId(request.getFromEmail(), request.getToEmail());
        
        System.out.println("=== SEND MESSAGE DEBUG ===");
        System.out.println("From: " + request.getFromEmail());
        System.out.println("To: " + request.getToEmail());
        System.out.println("Generated Conversation ID: " + conversationId);
        
        ChatMessage message = new ChatMessage();
        message.setFromEmail(request.getFromEmail());
        message.setToEmail(request.getToEmail());
        message.setMessage(request.getMessage());
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);
        message.setConversationId(conversationId);

        // Save message to database
        ChatMessage savedMessage = chatMessageRepository.save(message);
        System.out.println("Saved message with ID: " + savedMessage.getId() + " | ConvID: " + savedMessage.getConversationId());
        System.out.println("==========================");

        // Send real-time notification via WebSocket AND Pusher
        try {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", savedMessage.getId());
            messageData.put("fromEmail", savedMessage.getFromEmail());
            messageData.put("toEmail", savedMessage.getToEmail());
            messageData.put("message", savedMessage.getMessage());
            messageData.put("timestamp", savedMessage.getTimestamp().toString());
            messageData.put("isRead", savedMessage.isRead());
            messageData.put("conversationId", savedMessage.getConversationId());

            // Send to conversation topic via WebSocket
            messagingTemplate.convertAndSend("/topic/chat/" + conversationId, messageData);
            
            // Send via Pusher for real-time updates
            pusher.trigger(conversationId, "new-message", messageData);
            
            // Also trigger conversation update for both users
            triggerConversationUpdate(request.getFromEmail(), savedMessage);
            triggerConversationUpdate(request.getToEmail(), savedMessage);
            
            // Send push notification to recipient
            sendChatNotification(savedMessage);
            
            // Trigger unread count update for recipient
            triggerUnreadCountUpdate(request.getToEmail());
            
            System.out.println("Message sent via WebSocket and Pusher to conversation: " + conversationId);
        } catch (Exception e) {
            System.err.println("Failed to send real-time message: " + e.getMessage());
        }

        return savedMessage;
    }

    private void triggerConversationUpdate(String userEmail, ChatMessage message) {
        try {
            Map<String, Object> conversationData = new HashMap<>();
            conversationData.put("conversationId", message.getConversationId());
            conversationData.put("lastMessage", message.getMessage());
            conversationData.put("lastMessageTime", message.getTimestamp().toString());
            conversationData.put("fromEmail", message.getFromEmail());
            conversationData.put("toEmail", message.getToEmail());

            messagingTemplate.convertAndSend("/topic/user/" + userEmail, conversationData);
        } catch (Exception e) {
            System.err.println("Failed to trigger conversation update: " + e.getMessage());
        }
    }

    public List<ChatMessage> getMessages(String user1Email, String user2Email) {
        String conversationId = generateConversationId(user1Email, user2Email);
        System.out.println("=== GET MESSAGES DEBUG ===");
        System.out.println("User1: " + user1Email);
        System.out.println("User2: " + user2Email);
        System.out.println("Generated Conversation ID: " + conversationId);
        
        List<ChatMessage> messages = chatMessageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
        System.out.println("Found " + messages.size() + " messages for conversation: " + conversationId);
        
        for (ChatMessage msg : messages) {
            System.out.println("Message: " + msg.getId() + " | From: " + msg.getFromEmail() + " | To: " + msg.getToEmail() + " | ConvID: " + msg.getConversationId());
        }
        System.out.println("=========================");
        
        return messages;
    }

    public List<ChatConversation> getConversations(String userEmail) {
        List<String> conversationIds = chatMessageRepository.findConversationIdsByUserEmail(userEmail);
        
        List<ChatConversation> conversations = new ArrayList<>();
        
        for (String conversationId : conversationIds) {
            ChatConversation conversation = getConversationDetails(conversationId, userEmail);
            if (conversation != null) {
                conversations.add(conversation);
            }
        }
        
        return conversations.stream()
                .sorted(Comparator.comparing(ChatConversation::getLastMessageTime).reversed())
                .collect(Collectors.toList());
    }

    public void markMessagesAsRead(String fromEmail, String toEmail) {
        String conversationId = generateConversationId(fromEmail, toEmail);
        chatMessageRepository.markMessagesAsReadByConversationAndUser(conversationId, toEmail);
        
        // Trigger read status update via WebSocket
        try {
            Map<String, Object> readData = new HashMap<>();
            readData.put("conversationId", conversationId);
            readData.put("readByEmail", toEmail);
            
            messagingTemplate.convertAndSend("/topic/chat/" + conversationId + "/read", readData);
        } catch (Exception e) {
            System.err.println("Failed to trigger read status update: " + e.getMessage());
        }
        
        // Update unread count for the user
        triggerUnreadCountUpdate(toEmail);
    }

    public void setUserOnline(String userEmail) {
        UserOnlineStatus status = userOnlineStatusRepository.findByEmail(userEmail)
                .orElse(new UserOnlineStatus());
        status.setEmail(userEmail);
        status.setOnline(true);
        status.setLastSeen(LocalDateTime.now());
        userOnlineStatusRepository.save(status);

        // Trigger online status update via WebSocket
        try {
            Map<String, Object> presenceData = new HashMap<>();
            presenceData.put("userEmail", userEmail);
            presenceData.put("isOnline", true);
            presenceData.put("timestamp", LocalDateTime.now().toString());
            
            messagingTemplate.convertAndSend("/topic/presence", presenceData);
        } catch (Exception e) {
            System.err.println("Failed to trigger online status: " + e.getMessage());
        }
    }

    public void setUserOffline(String userEmail) {
        UserOnlineStatus status = userOnlineStatusRepository.findByEmail(userEmail)
                .orElse(new UserOnlineStatus());
        status.setEmail(userEmail);
        status.setOnline(false);
        status.setLastSeen(LocalDateTime.now());
        userOnlineStatusRepository.save(status);

        // Trigger offline status update via WebSocket
        try {
            Map<String, Object> presenceData = new HashMap<>();
            presenceData.put("userEmail", userEmail);
            presenceData.put("isOnline", false);
            presenceData.put("timestamp", LocalDateTime.now().toString());
            
            messagingTemplate.convertAndSend("/topic/presence", presenceData);
        } catch (Exception e) {
            System.err.println("Failed to trigger offline status: " + e.getMessage());
        }
    }

    public boolean isUserOnline(String userEmail) {
        return userOnlineStatusRepository.existsByEmailAndIsOnlineTrue(userEmail);
    }

    private String generateConversationId(String email1, String email2) {
        // Sort emails to ensure consistent conversation ID regardless of order
        // Replace @ and . with safe characters for Pusher channel names
        String safeEmail1 = email1.replace("@", "-at-").replace(".", "-dot-");
        String safeEmail2 = email2.replace("@", "-at-").replace(".", "-dot-");
        
        List<String> emails = Arrays.asList(safeEmail1, safeEmail2);
        Collections.sort(emails);
        return "chat-" + String.join("_", emails);
    }

    private ChatConversation getConversationDetails(String conversationId, String currentUserEmail) {
        System.out.println("=== GET CONVERSATION DETAILS DEBUG ===");
        System.out.println("Conversation ID: " + conversationId);
        System.out.println("Current User: " + currentUserEmail);
        
        List<ChatMessage> messages = chatMessageRepository.findByConversationIdOrderByTimestampDesc(conversationId);
        
        if (messages.isEmpty()) {
            System.out.println("No messages found for conversation: " + conversationId);
            return null;
        }
        
        ChatMessage lastMessage = messages.get(0);
        System.out.println("Last message from: " + lastMessage.getFromEmail() + " to: " + lastMessage.getToEmail());
        
        String otherUserEmail = currentUserEmail.equals(lastMessage.getFromEmail()) 
                ? lastMessage.getToEmail() 
                : lastMessage.getFromEmail();
        System.out.println("Other user email: " + otherUserEmail);
        
        // Get user details from UserService
        Map<String, Object> currentUserDetails = getUserDetails(currentUserEmail);
        Map<String, Object> otherUserDetails = getUserDetails(otherUserEmail);
        
        ChatConversation conversation = new ChatConversation();
        conversation.setId(conversationId);
        
        
        conversation.setBuyerEmail(otherUserEmail);  // Other person
        conversation.setVendorEmail(currentUserEmail); // Current user
        conversation.setBuyerName((String) otherUserDetails.getOrDefault("name", otherUserEmail.split("@")[0]));
        conversation.setVendorName((String) currentUserDetails.getOrDefault("name", currentUserEmail.split("@")[0]));
        
        System.out.println("Set conversation - Current User: " + currentUserEmail + ", Other User: " + otherUserEmail);
        
        conversation.setLastMessage(lastMessage.getMessage());
        conversation.setLastMessageTime(lastMessage.getTimestamp());
        conversation.setUnreadCount(chatMessageRepository.countUnreadMessagesByConversationAndUser(conversationId, currentUserEmail));
        conversation.setBuyerOnline(isUserOnline(otherUserEmail));
        conversation.setVendorOnline(isUserOnline(currentUserEmail));
        
        System.out.println("Final conversation - ID: " + conversation.getId() + ", Other User: " + conversation.getBuyerEmail() + ", Current User: " + conversation.getVendorEmail());
        System.out.println("======================================");
        
        return conversation;
    }

    private Map<String, Object> getUserDetails(String email) {
        try {
            // Call UserService to get user details
            String userServiceUrl = "http://USER-SERVICE/api/users/profile/" + email;
            return restTemplate.getForObject(userServiceUrl, Map.class);
        } catch (Exception e) {
            // Fallback if service is unavailable
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("name", email.split("@")[0]);
            fallback.put("email", email);
            return fallback;
        }
    }

    // Removed isVendor method - no longer needed since users can have dual roles

    public void broadcastTypingStatus(String conversationId, String userEmail, boolean isTyping) {
        try {
            Map<String, Object> typingData = new HashMap<>();
            typingData.put("conversationId", conversationId);
            typingData.put("userEmail", userEmail);
            typingData.put("isTyping", isTyping);
            typingData.put("timestamp", LocalDateTime.now().toString());
            
            messagingTemplate.convertAndSend("/topic/chat/" + conversationId + "/typing", typingData);
        } catch (Exception e) {
            System.err.println("Failed to broadcast typing status: " + e.getMessage());
        }
    }

    public List<ChatMessage> getAllMessagesForDebug() {
        List<ChatMessage> allMessages = chatMessageRepository.findAll();
        System.out.println("=== ALL MESSAGES DEBUG ===");
        System.out.println("Total messages in database: " + allMessages.size());
        for (ChatMessage msg : allMessages) {
            System.out.println("ID: " + msg.getId() + " | From: " + msg.getFromEmail() + " | To: " + msg.getToEmail() + " | ConvID: " + msg.getConversationId() + " | Message: " + msg.getMessage());
        }
        System.out.println("==========================");
        return allMessages;
    }

    public List<ChatMessage> getMessagesByConversationId(String conversationId) {
        System.out.println("=== GET MESSAGES BY CONVERSATION ID ===");
        System.out.println("Conversation ID: " + conversationId);
        List<ChatMessage> messages = chatMessageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
        System.out.println("Found " + messages.size() + " messages");
        for (ChatMessage msg : messages) {
            System.out.println("Message: " + msg.getId() + " | From: " + msg.getFromEmail() + " | To: " + msg.getToEmail() + " | Message: " + msg.getMessage());
        }
        System.out.println("=======================================");
        return messages;
    }

    public int getTotalUnreadMessagesCount(String userEmail) {
        return chatMessageRepository.countUnreadMessagesByUser(userEmail);
    }

    public void markAllMessagesAsReadForUser(String userEmail) {
        List<String> conversationIds = chatMessageRepository.findConversationIdsByUserEmail(userEmail);
        for (String conversationId : conversationIds) {
            chatMessageRepository.markMessagesAsReadByConversationAndUser(conversationId, userEmail);
        }
        
        // Trigger read status update via WebSocket for all conversations
        try {
            Map<String, Object> readData = new HashMap<>();
            readData.put("userEmail", userEmail);
            readData.put("allMessagesRead", true);
            
            messagingTemplate.convertAndSend("/topic/user/" + userEmail + "/read-all", readData);
        } catch (Exception e) {
            System.err.println("Failed to trigger read all status update: " + e.getMessage());
        }
        
        // Update unread count for the user (should be 0 now)
        triggerUnreadCountUpdate(userEmail);
    }

    private void sendChatNotification(ChatMessage message) {
        try {
            // Get recipient user details
            Map<String, Object> senderDetails = getUserDetails(message.getFromEmail());
            String senderName = (String) senderDetails.getOrDefault("name", message.getFromEmail().split("@")[0]);
            
            // Create notification event
            Map<String, Object> notificationEvent = new HashMap<>();
            notificationEvent.put("title", "New Message from " + senderName);
            notificationEvent.put("message", message.getMessage().length() > 50 
                ? message.getMessage().substring(0, 50) + "..." 
                : message.getMessage());
            notificationEvent.put("email", message.getToEmail());
            notificationEvent.put("type", "CHAT_MESSAGE");
            
            // Send notification to notification service
            String notificationServiceUrl = "http://NOTIFICATION-SERVICE/api/notification/send";
            notificationRestTemplate.postForObject(notificationServiceUrl, notificationEvent, String.class);
            
            System.out.println("Chat notification sent to: " + message.getToEmail());
        } catch (Exception e) {
            System.err.println("Failed to send chat notification: " + e.getMessage());
        }
    }

    private void triggerUnreadCountUpdate(String userEmail) {
        try {
            int unreadCount = getTotalUnreadMessagesCount(userEmail);
            
            Map<String, Object> unreadData = new HashMap<>();
            unreadData.put("userEmail", userEmail);
            unreadData.put("unreadCount", unreadCount);
            
            // Send via WebSocket
            messagingTemplate.convertAndSend("/topic/user/" + userEmail + "/unread-count", unreadData);
            
            // Send via Pusher for better reliability
            String channelName = "user-" + userEmail.replace("@", "-at-").replace(".", "-dot-");
            pusher.trigger(channelName, "unread-count-update", unreadData);
            
            System.out.println("Unread count update sent to: " + userEmail + " (count: " + unreadCount + ")");
        } catch (Exception e) {
            System.err.println("Failed to trigger unread count update: " + e.getMessage());
        }
    }
}