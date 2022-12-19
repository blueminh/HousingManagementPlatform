package sem.hoa.dtos;

import lombok.Data;

@Data
public class JoiningRequestModel {
    public String hoaName;

    public String userName;

    //address has to be stored in the request
    public String country;
    public String city;

    /**
     * Constructor for joining request model.
     *
     * @param hoaName hoaName
     * @param userName userName
     * @param country country
     * @param city city
     */
    public JoiningRequestModel(String hoaName, String userName, String country, String city) {
        this.hoaName = hoaName;
        this.userName = userName;
        this.country = country;
        this.city = city;
    }
}
