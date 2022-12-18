package sem.hoa.domain.activity;

public class ActivityAlreadyExistsException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public ActivityAlreadyExistsException(String message) {
        super(message);
    }
}
