package sem.hoa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sem.hoa.domain.notifications.DuplicateNotificationException;
import sem.hoa.domain.notifications.NotificationService;
import sem.hoa.models.NotificationResponseModel;

import java.util.List;

@RestController
public class NotificationController {
    private final transient NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * API endpoint for saving a new notification in the database.
     *
     * @param message message the notification should display
     * @param username the user to which the notification applies
     * @return HTTP status response whether it was successful or not.
     */
    @PostMapping("/notifications/new")
    public ResponseEntity addNotification(String message, String username) {
        try {
            notificationService.addNotification(message, username);
            return ResponseEntity.ok().build();
        } catch (DuplicateNotificationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * API endpoints for fetching all pending notifications for a user.
     * All returned notifications will be removed from the database as it only stores unseen ones.
     *
     * @param username user to fetch the notifications for
     * @return a list of messages
     */
    @PostMapping("/notifications/get")
    public ResponseEntity<NotificationResponseModel> getNotifications(String username) {
        List<String> messages = notificationService.getNotifications(username);
        NotificationResponseModel responseModel = new NotificationResponseModel(messages);
        return ResponseEntity.ok(responseModel);
    }
}
