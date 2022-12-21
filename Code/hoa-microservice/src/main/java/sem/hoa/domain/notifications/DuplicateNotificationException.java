package sem.hoa.domain.notifications;

public class DuplicateNotificationException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public DuplicateNotificationException() {
        super("This notification already exists in the database!");
    }
}
