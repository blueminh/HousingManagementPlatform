package sem.hoa.exceptions;

import java.io.Serializable;

public class HoaCreationException extends Exception
        implements java.io.Serializable {

    public static final long serialVersionUID = 432198744;

    public HoaCreationException(String errMessage) {
        super(errMessage);
    }
}
