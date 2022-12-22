package sem.users.domain.user;

/**
 * A DDD domain event indicating a user's Full Name has changed.
 */
public class FullNameWasChangedEvent {
    private final AppUser user;

    public FullNameWasChangedEvent(AppUser user) {
        this.user = user;
    }

    public AppUser getUser() {
        return this.user;
    }
}

