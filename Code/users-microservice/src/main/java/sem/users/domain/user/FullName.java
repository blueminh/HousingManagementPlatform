package sem.users.domain.user;
import java.util.Objects;

/**
 * A DDD value object representing the full name of a user in our domain.
 */
public class FullName {
    private final transient String fullname;

    public FullName(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String toString() {
        return fullname;
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
        return fullname.equals(fullname1.fullname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullname);
    }
}
