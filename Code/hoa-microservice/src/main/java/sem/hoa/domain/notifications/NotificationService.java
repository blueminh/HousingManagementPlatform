package sem.hoa.domain.notifications;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    private final transient NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Method to add a new notification to the database.
     *
     * @param message message of the notification
     * @param username user to which it will be sent
     * @throws DuplicateNotificationException if a notification with all matching parameters already exists
     */
    public void addNotification(String message, String username) throws DuplicateNotificationException {
        if (notificationRepository.existsByMessageAndUsername(message, username)) {
            throw new DuplicateNotificationException();
        }

        Notification notification = new Notification(message, username);
        notificationRepository.save(notification);
        return;
    }

    /**
     * Method to get all notifications for a user.
     *
     * @param username user for whom the notifications are being sent
     * @return A list of messages
     */
    public List<String> getNotifications(String username) {
        if (!notificationRepository.existsByUsername(username)) {
            return new ArrayList<String>();
        }

        List<Notification> notifications = notificationRepository.removeAllByUsername(username);
        List<String> result = new ArrayList<>();
        for (Notification notification : notifications) {
            result.add(notification.getMessage());
        }
        return result;
    }
}
