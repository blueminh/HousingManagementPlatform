package sem.hoa.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@IdClass(MembershipID.class)
@Table(name = "memberManagements")
@NoArgsConstructor
public class Membership {

  @Id
  @Column(name = "username", nullable = false)
  private String username;

  @Id
  @Column(name = "hoaID", nullable = false)
  private int hoaID;

  // Enum type is complicated to be persisted in the database -> simply use a boolean value
  @Column(name = "isboardmember")
  private boolean isBoardMember;

  @Getter
  @Setter
  @Column(name = "joiningDate")
  private Long joiningDate;

  @Getter
  @Setter
  @Column(name = "joiningBoardDate")
  private Long joiningBoardDate;

  public Membership(String username, int hoaID, boolean isBoardMember, long joiningDate, long joiningBoardDate) {
    this.username = username;
    this.hoaID = hoaID;
    this.isBoardMember = isBoardMember;
    this.joiningDate = joiningDate;
    this.joiningBoardDate = joiningBoardDate;
  }

  public String getUsername() {
    return username;
  }

  public int getHoaID() {
    return hoaID;
  }

  public boolean isBoardMember() {
    return isBoardMember;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setHoaID(int hoaID) {
    this.hoaID = hoaID;
  }

  public void setBoardMember(boolean boardMember) {
    isBoardMember = boardMember;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Membership that = (Membership) o;
    return hoaID == that.hoaID && isBoardMember == that.isBoardMember && username.equals(that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, hoaID, isBoardMember);
  }
}
