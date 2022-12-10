package sem.users.domain.user;

/**
 * Exception to indicate the membership type is not valid.
 */
public class WronngRoleTypeException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public WronngRoleTypeException(String roleType) {
        super(roleType);
    }
}
