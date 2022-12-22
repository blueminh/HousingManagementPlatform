package sem.users.domain.user;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import sem.users.HasEvents;


/**
 * A DDD entity representing an application user in our domain.
 */
@Entity
@Table(name = "users")
@NoArgsConstructor
public class AppUser extends HasEvents {
    /**
     * Identifier for the application user.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "username", nullable = false, unique = true)
    @Convert(converter = UsernameAttributeConverter.class)
    private Username username;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = HashedPasswordAttributeConverter.class)
    private HashedPassword password;

    @Column(name = "fullname", nullable = false, unique = false)
    @Convert(converter = FullnameAttributeConverter.class)
    private FullName fullname;



    /**
     * Create new application user.
     *
     * @param username The username for the new user
     * @param password The password for the new user
     */
    public AppUser(Username username, HashedPassword password, FullName fullname) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.recordThat(new UserWasCreatedEvent(username));

    }

    public Username getUsername() {
        return username;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public FullName getFullName() {
        return fullname;
    }

    /**
     * Equality is only based on the identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppUser appUser = (AppUser) o;
        return username.equals(appUser.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
