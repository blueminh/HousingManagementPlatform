package sem.users.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a hashed password in our domain.
 */
@EqualsAndHashCode
public class HashedPassword {
    @EqualsAndHashCode.Include
    private final transient String hash;

    public HashedPassword(String hash) {
        // Validate input
        this.hash = hash;
    }

    @Override
    public String toString() {
        return hash;
    }
}
