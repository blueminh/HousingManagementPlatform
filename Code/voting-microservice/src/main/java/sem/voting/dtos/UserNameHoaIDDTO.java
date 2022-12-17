package sem.voting.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class UserNameHoaIDDTO {
  @Getter
  @Setter
  public String username;

  @Getter
  @Setter
  public int hoaID;

  public UserNameHoaIDDTO(String username, int hoaID) {
    this.username = username;
    this.hoaID = hoaID;
  }
}
