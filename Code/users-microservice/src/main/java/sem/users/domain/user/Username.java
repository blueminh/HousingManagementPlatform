package sem.users.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a Username in our domain.
 */
@EqualsAndHashCode
public class Username {
    @EqualsAndHashCode.Include
    private final transient String usernamex;

    public Username(String username) {
        // validate Username
        this.usernamex = username;
    }

    @Override
    public String toString() {
        return usernamex;
    }
}
