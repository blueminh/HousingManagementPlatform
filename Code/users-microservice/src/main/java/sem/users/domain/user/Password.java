package sem.users.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a password in our domain.
 */
@EqualsAndHashCode
public class Password {
    @EqualsAndHashCode.Include
    private final transient String passwordValue;

    public Password(String password) {
        // Validate input
        this.passwordValue = password;
    }

    @Override
    public String toString() {
        return passwordValue;
    }
}