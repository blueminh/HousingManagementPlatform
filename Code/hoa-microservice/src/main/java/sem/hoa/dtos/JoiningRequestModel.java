package sem.hoa.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class JoiningRequestModel {
  @Getter
  @Setter
  public String hoaName;

  @Getter
  @Setter
  public String username;

  //address has to be stored in the request
  public String country;
  public String city;

  public JoiningRequestModel(String hoaName, String username, String country, String city) {
    this.hoaName = hoaName;
    this.username = username;
    this.country = country;
    this.city = city;
  }
}
