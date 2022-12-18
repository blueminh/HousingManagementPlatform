package sem.hoa.domain.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
public class MembershipID implements Serializable {
  @Getter
  @Setter
  private String username;
  @Getter
  @Setter
  private int hoaID;

  public MembershipID(){}

  public MembershipID(String username, int hoaID) {
    this.username = username;
    this.hoaID = hoaID;
  }
}
