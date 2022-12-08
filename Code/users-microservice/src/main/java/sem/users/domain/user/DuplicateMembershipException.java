package sem.users.domain.user;

public class DuplicateMembershipException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public DuplicateMembershipException(Username username, HoaMembership hoaMembership) {
        super(username.toString() + " " + hoaMembership.toString());
    }
}
