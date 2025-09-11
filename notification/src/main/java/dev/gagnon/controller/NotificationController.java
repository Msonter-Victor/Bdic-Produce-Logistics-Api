package dev.gagnon.controller;

import dev.gagnon.dto.request.NotificationEvent;
import dev.gagnon.dto.request.ReadNotificationRequest;
import dev.gagnon.dto.response.NotificationResponse;
import dev.gagnon.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationEvent event){
        try{
            notificationService.sendNotification(event);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/readNotification")
    public ResponseEntity<?> readNotification(@RequestBody ReadNotificationRequest request){
        try{
           notificationService.readNotification(request);
           return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/readAllNotification")
    public ResponseEntity<?> readAllNotification(@RequestParam String email){
        try{
            notificationService.readAllNotification(email);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getbyId")
    public ResponseEntity<?> getNotificationById(@RequestParam Long id){
        NotificationResponse response = notificationService.viewNotification(id);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getUserAll")
    public ResponseEntity<?> getUserNotifications(@RequestParam String email){
        List<NotificationResponse> response = notificationService.viewUserNotifications(email);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getUserAllUnRead")
    public ResponseEntity<?> getUserUnReadNotifications(@RequestParam String email){
        List<NotificationResponse> response = notificationService.viewUserUnReadNotifications(email);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/deleteById")
    public ResponseEntity<?> deleteNotificationById(@RequestParam Long id){
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/deleteAllUser")
    public ResponseEntity<?> deleteNotificationById(@RequestParam String email){
        notificationService.deleteUserNotifications(email);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteNotifications(){
        notificationService.deleteAllNotifications();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck(){
        return ResponseEntity.ok().body("Notification service is running");
    }

}
