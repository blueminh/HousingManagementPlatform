package sem.voting.domain.services.implementations;

public class AddOptionException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public AddOptionException(String message) {
        super(message);
    }
}
