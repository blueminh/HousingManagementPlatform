package sem.hoa.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@IdClass(MembershipID.class)
@Table(name = "memberManagements")
@NoArgsConstructor
@AllArgsConstructor
public class Membership {
    @Id
    @Column(name = "username", nullable = false)
    private String username;

    @Id
    @Column(name = "hoaID", nullable = false)
    private int hoaID;

    // Enum type is complicated to be persisted in the database -> simply use a boolean value
    @Column(name = "isboardmember")
    @Getter
    private boolean boardMember;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "houseNumber")
    private int houseNumber;

    @Column(name = "postalCode")
    private String postalCode;

    public int getHoaID() {
        return hoaID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHoaID(int hoaID) {
        this.hoaID = hoaID;
    }

    public void setBoardMember(boolean boardMember) {
        this.boardMember = boardMember;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Membership that = (Membership) o;
        return hoaID == that.hoaID && boardMember == that.boardMember && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, hoaID, boardMember);
    }
}
