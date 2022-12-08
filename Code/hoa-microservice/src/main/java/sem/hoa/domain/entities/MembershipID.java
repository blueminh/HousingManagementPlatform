package sem.hoa.domain.entities;

import java.io.Serializable;
import java.util.Objects;

public class MembershipID implements Serializable {
  private String username;
  private int hoaID;

  public MembershipID(String username, int hoaID) {
    this.username = username;
    this.hoaID = hoaID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MembershipID that = (MembershipID) o;
    return hoaID == that.hoaID && Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, hoaID);
  }
}
