package sem.hoa.domain.activity;

public class NoSuchActivityException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public NoSuchActivityException(String message) {
        super(message);
    }
}
