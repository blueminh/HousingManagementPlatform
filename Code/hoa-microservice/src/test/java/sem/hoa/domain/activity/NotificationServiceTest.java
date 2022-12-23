package sem.hoa.domain.activity;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sem.hoa.domain.notifications.DuplicateNotificationException;
import sem.hoa.domain.notifications.Notification;
import sem.hoa.domain.notifications.NotificationRepository;
import sem.hoa.domain.notifications.NotificationService;

import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NotificationServiceTest {

    @Autowired
    private transient NotificationRepository notificationRepository;

    @Autowired
    NotificationService notificationService;

    @Transactional
    @Test
    void addNotificationTest() {
        String message = "message";
        String username = "username";
        Notification notification = new Notification(message, username);
        assertDoesNotThrow(() -> notificationService.addNotification(message, username));
        List<Notification> notiflist = notificationRepository.removeAllByUsername(username);
        assertFalse(notiflist.isEmpty());
        assertTrue(notiflist.contains(notification));
    }

    @Test
    void duplicateNotificationTest() {
        String message = "message";
        String username = "username";
        assertDoesNotThrow(() -> notificationService.addNotification(message, username));
        assertThrows(DuplicateNotificationException.class, () -> notificationService.addNotification(message, username));
    }

    @Test
    void nullValueTest() {
        String message = null;
        String username = null;
        assertThrows(InvalidParameterException.class, () -> notificationService.addNotification(message, username));
    }

    @Test
    void getEmptyListTest() {
        assertTrue(notificationService.getNotifications("non existent username").isEmpty());
    }

    @Transactional
    @Test
    void getNotificationsTest() {
        String message = "message";
        String message2 = "message2";
        String username = "username";

        assertDoesNotThrow(() -> notificationService.addNotification(message, username));
        assertDoesNotThrow(() -> notificationService.addNotification(message2, username));
        List<String> notiflist = notificationService.getNotifications(username);
        assertTrue(notiflist.contains(message));
        assertTrue(notiflist.contains(message2));
        assertEquals(2, notiflist.size());

    }

    @Test
    void notificationEntityTest() {
        Notification notification = new Notification();
        notification.setMessage("message");
        notification.setUsername("username");
        assertEquals("message", notification.getMessage());
        assertEquals("username", notification.getUsername());
    }

    @Test
    void equalsHashCodeTest() {
        final Notification notification = new Notification("message", "username");
        final Notification notification2 = new Notification("message2", "username");
        final Notification notification3 = new Notification("message", "username3");
        final Notification notification4 = new Notification("message", "username");
        assertEquals(notification, notification);
        assertFalse(notification.equals(null));
        assertNotEquals(notification, notification2);
        assertNotEquals(notification, notification3);
        assertEquals(notification.hashCode(), notification4.hashCode());

    }
}
