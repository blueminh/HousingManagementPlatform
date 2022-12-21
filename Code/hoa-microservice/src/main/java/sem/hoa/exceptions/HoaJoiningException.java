package sem.hoa.exceptions;

public class HoaJoiningException extends Exception
        implements java.io.Serializable {

    public static final long serialVersionUID = 432198745L;

    public HoaJoiningException(String errMessage) {
        super(errMessage);
    }
}