package sem.hoa.domain.activity;

public class NoAccessToHoaException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public NoAccessToHoaException(String message) {
        super(message);
    }
}
