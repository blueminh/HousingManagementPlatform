package sem.users.domain.user;

import java.util.Objects;

/**
 * A DDD value object representing the full name of a user in our domain.
 */
public class FullName {
    private final transient String name;

    public FullName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
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
        return name.equals(fullname1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
