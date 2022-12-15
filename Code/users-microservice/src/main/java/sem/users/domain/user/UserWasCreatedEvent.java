package sem.users.domain.user;

/**
 * A DDD domain event that indicated a user was created.
 */
public class UserWasCreatedEvent {
    private final Username username;

    public UserWasCreatedEvent(Username username) {
        this.username = username;
    }

    public Username getUsername() {
        return this.username;
    }
}
