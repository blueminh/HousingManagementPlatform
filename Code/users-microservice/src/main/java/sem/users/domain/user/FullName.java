package sem.users.domain.user;

import java.util.Objects;

/**
 * A DDD value object representing the full name of a user in our domain.
 */
public class FullName {
    private final transient String full_name;

    public FullName(String full_name) {
        this.full_name = full_name;
    }

    @Override
    public String toString() {
        return full_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FullName fullname1 = (FullName) o;
        return full_name.equals(fullname1.full_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(full_name);
    }
}
