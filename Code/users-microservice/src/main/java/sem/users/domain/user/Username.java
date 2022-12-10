package sem.users.domain.user;

import lombok.EqualsAndHashCode;

/**
 * A DDD value object representing a Username in our domain.
 */
@EqualsAndHashCode
public class Username implements Comparable<Username> {
    private final transient String usernamex;

    public Username(String username) {
        // validate Username
        this.usernamex = username;
    }

    @Override
    public String toString() {
        return usernamex;
    }

    @Override
    public int compareTo(Username o) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.usernamex, o.usernamex);
    }
}
