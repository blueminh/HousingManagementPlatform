package sem.hoa.dtos;

import lombok.Data;

@Data
public class HoaModifyDTO {
    public String hoaName;
    public String userCountry;
    public String userCity;
    public String userStreet;
    public int userHouseNumber;
    public String userPostalCode;
}
