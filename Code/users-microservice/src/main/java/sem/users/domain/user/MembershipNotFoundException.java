package sem.users.domain.user;

/**
 * Exception to indicate an already existing membership was not found.
 */
public class MembershipNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public MembershipNotFoundException(Username username, HoaMembership hoaMembership) {
        super(username.toString() + hoaMembership.toString());
    }
}
