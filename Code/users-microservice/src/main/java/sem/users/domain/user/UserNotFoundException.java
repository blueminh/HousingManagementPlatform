package sem.users.domain.user;

/**
 * Exception to indicate the Username is already in use.
 */
public class UserNotFoundException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public UserNotFoundException(Username username) {
        super(username.toString());
    }
}