package sem.users.domain.user;

import java.util.ArrayList;
import java.util.Objects;
import javax.persistence.*;
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

    @Column(name = "memberships", nullable = false)
    @Convert(converter = MembershipAttributeConverter.class)
    private Membership membership;


    /**
     * Create new application user.
     *
     * @param username The username for the new user
     * @param password The password for the new user
     */
    public AppUser(Username username, HashedPassword password) {
        this.username = username;
        this.password = password;
        this.membership = new Membership(new ArrayList<HoaMembership>());
        this.recordThat(new UserWasCreatedEvent(username));

    }

    public void changePassword(HashedPassword password) {
        this.password = password;
        this.recordThat(new PasswordWasChangedEvent(this));
    }

    public Username getUsername() {
        return username;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public Membership getMembership() {
        return this.membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    /**
     * Updates the HOA membership for an existing membership of the user.
     *
     * @param hoa the HOA membership to update to.
     *
     * @return false if the user doesn't already have a membership for that HOA,
     *          true if successfully updated.
     *
     *          Note: The equals method of HOAMembership only checks for the HOAID,
     *          therefore the contains method will return a membership
     *          for the same HOA even if the role is different. This is by design.
     */
    public boolean updateHoa(HoaMembership hoa) {
        if (!this.membership.getMembershipList().contains(hoa)) {
            return false;
        }
        this.membership.getMembershipList().remove(hoa);
        return this.membership.getMembershipList().add(hoa);

    }

    /**
     * Adds a new membership.
     *
     * @param hoa membership to add
     *
     * @return boolean whether it was successful
     */
    public boolean addMembership(HoaMembership hoa) {

        if (this.membership.getMembershipList().contains(hoa)) {
            return false;
        }
        return this.membership.getMembershipList().add(hoa);
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
