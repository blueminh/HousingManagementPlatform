package sem.voting.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class UserNameDTO {
  @Getter
  @Setter
  String username;

  public UserNameDTO(String username) {
    this.username = username;
  }
}
