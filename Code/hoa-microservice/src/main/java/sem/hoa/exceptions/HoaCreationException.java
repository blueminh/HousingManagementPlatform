package sem.hoa.exceptions;

public class HoaCreationException extends Exception
        implements java.io.Serializable {

    public static final long serialVersionUID = 432198744L;

    public HoaCreationException(String errMessage) {
        super(errMessage);
    }
}
