package sem.hoa.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class UserNameHoaNameDTO {
  @Getter
  @Setter
  public String hoaName;

  @Getter
  @Setter
  public String username;

  public UserNameHoaNameDTO(String hoaName, String username) {
    this.hoaName = hoaName;
    this.username = username;
  }
}
