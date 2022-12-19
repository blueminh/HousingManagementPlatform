package sem.hoa.domain.activity;

public class UserAlreadyParticipatesException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public UserAlreadyParticipatesException(String message) {
        super(message);
    }
}
