package sem.users.domain.user;

/**
 * Exception to indicate the Username is already in use.
 */
public class UsernameAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public UsernameAlreadyInUseException(Username username) {
        super(username.toString() + " is already in use!");
    }
}
