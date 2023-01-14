package sem.hoa.dtos;

import lombok.Data;

@Data
public class HoaModifyDTO {
    private String hoaName;
    private String userCountry;
    private String userCity;
    private String userStreet;
    private int userHouseNumber;
    private String userPostalCode;
}
