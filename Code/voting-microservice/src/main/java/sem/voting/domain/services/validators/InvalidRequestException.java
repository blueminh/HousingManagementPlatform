package sem.voting.domain.services.validators;

public class InvalidRequestException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidRequestException(String message) {
        super(message);
    }
}
